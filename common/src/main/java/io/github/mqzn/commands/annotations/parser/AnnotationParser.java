package io.github.mqzn.commands.annotations.parser;

import io.github.mqzn.commands.annotations.*;
import io.github.mqzn.commands.annotations.subcommands.SubCommand;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.arguments.ArgumentData;
import io.github.mqzn.commands.arguments.ArgumentNumber;
import io.github.mqzn.commands.base.CommandInfo;
import io.github.mqzn.commands.base.CommandRequirement;
import io.github.mqzn.commands.base.Information;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.base.cooldown.CommandCooldown;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import io.github.mqzn.commands.base.syntax.SubCommandBuilder;
import io.github.mqzn.commands.base.syntax.SyntaxFlags;
import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import io.github.mqzn.commands.sender.SenderWrapper;
import io.github.mqzn.commands.utilities.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Pattern;

public final class AnnotationParser<S> {


	@NotNull
	public final static String SUB_COMMAND_EXECUTE_METHOD = "execute";

	@NotNull
	private final CommandManager<?, S> manager;

	@NotNull
	private Class<?> senderType;

	public AnnotationParser(@NotNull CommandManager<?, S> manager) {
		this.manager = manager;
		senderType = manager.getSenderWrapper().senderType();
	}


	/**
	 * Parses the annotated command into a command
	 * then registers it directly through the registered
	 * command manager !
	 *
	 * @param annotatedCommand the command instance to register
	 * @param <C>              the type of the command class
	 */
	@SuppressWarnings("unchecked")
	public <E extends Enum<E>, C, CO> void parse(CO annotatedCommand) {

		if (!checkAnnotation(annotatedCommand)) return;
		Command cmdAnnotation = annotatedCommand.getClass().getAnnotation(Command.class);
		assert cmdAnnotation != null;

		io.github.mqzn.commands.base.Command.Builder<S> builder = io.github.mqzn.commands.base.Command.builder(manager, cmdAnnotation.name())
						.info(new CommandInfo(cmdAnnotation.permission().isEmpty() ? null : cmdAnnotation.permission(), cmdAnnotation.description(), cmdAnnotation.aliases()));

		if (annotatedCommand.getClass().isAnnotationPresent(Cooldown.class)) {
			Cooldown cooldown = annotatedCommand.getClass().getAnnotation(Cooldown.class);
			assert cooldown != null;
			CommandCooldown commandCooldown = new CommandCooldown(cooldown.value(), cooldown.unit());
			builder.cooldown(commandCooldown);
		}

		for (var requirementClass : cmdAnnotation.requirements()) {

			if (Enum.class.isAssignableFrom(requirementClass)) {
				Class<E> enumRequirement = (Class<E>) requirementClass;
				for (E requirement : enumRequirement.getEnumConstants())
					builder.requirement((CommandRequirement<S>) requirement);

			} else {
				try {
					Constructor<CommandRequirement<?>> constructor = (Constructor<CommandRequirement<?>>) requirementClass.getDeclaredConstructor();
					builder.requirement((CommandRequirement<S>) constructor.newInstance());

				} catch (NoSuchMethodException | InvocationTargetException
				         | InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}

			}

		}

		SubCommand[] subs = annotatedCommand.getClass().getAnnotationsByType(SubCommand.class);
		for (SubCommand subCommand : subs) {
			builder = this.loadSub(cmdAnnotation, subCommand, builder);
		}

		Method[] methods = annotatedCommand.getClass().getMethods();
		for (var method : methods) {
			//manager.log("Checking method %s", method.getName());
			if (!checkMethod(method)) {
				if (method.getParameters().length == 1 && method.isAnnotationPresent(Default.class)) {

					//default Execution
					builder.defaultExecutor((sender, context) -> {

						try {
							method.invoke(annotatedCommand, sender);
						} catch (IllegalAccessException | InvocationTargetException e) {
							throw new RuntimeException(e);
						}

					});
				}

				continue;
			}

			//manager.log("Parsing method %s", method.getName());
			Syntax syntaxMeta = method.getAnnotation(Syntax.class);
			assert syntaxMeta != null;

			var loadedData = loadMethodParameters(manager, cmdAnnotation.name(), syntaxMeta, method);
			var arguments = loadedData.getRight();
			var flags = loadedData.getLeft();


			if (syntaxMeta.senderType() != Object.class)
				senderType = syntaxMeta.senderType();


			CommandSyntaxBuilder<S, C> syntaxBuilder = CommandSyntaxBuilder.genericBuilder((Class<C>) senderType, cmdAnnotation.name());

			for (var arg : arguments) {
				if (arg == null) continue;
				syntaxBuilder = syntaxBuilder.argument(arg);
			}

			syntaxBuilder.info(new Information(syntaxMeta.permission(), syntaxMeta.description()))
							.flags(flags)
							.execute((sender, context) -> {
								Object[] valuesToUse = readValues(method, sender, context);

								try {
									method.invoke(annotatedCommand, valuesToUse);
								} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
									throw new RuntimeException(e);
								}

							});

			builder.syntax(syntaxBuilder.build());

		}

		manager.registerCommand(builder.build());
	}

	private <C> io.github.mqzn.commands.base.Command.Builder<S> loadSub(Command cmd,
	                                                                    SubCommand subCommand,
	                                                                    io.github.mqzn.commands.base.Command.Builder<S> builder) {

		Class<?> subClass = subCommand.value();

		SubCommandInfo info = subClass.getAnnotation(SubCommandInfo.class);
		if (info == null) {
			throw new IllegalArgumentException(subInfoNotFound(subClass));
		}

		var subCommandObject = this.<C>loadSubCommandBuilder(cmd, subClass).build();

		for (var childClass : info.children()) {
			SubCommandInfo childInfo = childClass.getAnnotation(SubCommandInfo.class);
			if (childInfo == null) continue;
			subCommandObject.addChild(childInfo.name());
		}


		if (!info.parent().equals(Object.class)) {
			SubCommandInfo parentInfo = info.parent().getAnnotation(SubCommandInfo.class);
			if (parentInfo == null) {
				throw new IllegalArgumentException(subInfoNotFound(info.parent()));
			}

			subCommandObject.setParent(parentInfo.name());
		}

		return builder.syntax(subCommandObject);
	}

	@SuppressWarnings({"unchecked", "deprecation"})
	private <C> SubCommandBuilder<S, C> loadSubCommandBuilder(Command cmd,
	                                                          Class<?> subClass) {

		Object subCommandInstance;
		try {
			var subClassConstructor = subClass.getConstructor();
			if (!subClassConstructor.isAccessible()) {
				subClassConstructor.setAccessible(true);
			}

			subCommandInstance = subClassConstructor.newInstance();

		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		Syntax subSyntax = subClass.getAnnotation(Syntax.class);
		if (subSyntax == null) {
			throw new IllegalArgumentException(String.format("Failed to load subcommand from class '%s' as the class doesn't have @Syntax annotation", subClass.getName()));
		}

		SubCommandInfo subCmdInfo = subClass.getAnnotation(SubCommandInfo.class);
		if (subCmdInfo == null) {
			throw new IllegalArgumentException(String.format("Failed to load subcommand from class '%s' as the class doesn't have @SubCommandInfo annotation", subClass.getName()));
		}

		Class<C> senderType = (Class<C>) subSyntax.senderType();
		var subBuilder = SubCommandBuilder.<S, C>genericBuilder(senderType, cmd.name(), subCmdInfo.name())
						.aliases(subCmdInfo.aliases());

		Method executeMethod = Arrays.stream(subClass.getDeclaredMethods())
						.filter(method -> method.getParameters().length != 0
										&& method.isAnnotationPresent(SubCommandExecution.class)
										&& method.getName().equalsIgnoreCase(SUB_COMMAND_EXECUTE_METHOD))
						.findFirst().orElse(null);

		if (executeMethod == null) {
			throw new IllegalArgumentException(String.format("Failed to load subcommand from class '%s' as the class doesn't have an execute method that is annotated by @SubCommandExecution", subClass.getName()));
		}

		if (!subSyntax.senderType().equals(Object.class)) {
			subBuilder = subBuilder.senderType(senderType);
		} else {
			subBuilder = subBuilder.senderType((Class<C>) manager.getSenderWrapper().senderType());
		}

		var methodData = loadParentalSubCommandsParameters(manager, cmd.name(), subClass, executeMethod);

		for (var arg : methodData.arguments) {
			subBuilder = subBuilder.argument(arg);
		}

		subBuilder = subBuilder.flags(methodData.flags);

		subBuilder = subBuilder.info(new Information(subSyntax.permission(), subSyntax.description()))
						.execute((sender, context) -> {
							Object[] valuesToUse = readValues(executeMethod, sender, context);

							try {
								executeMethod.invoke(subCommandInstance, valuesToUse);
							} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
								throw new RuntimeException(e);
							}

						});


		return subBuilder;
	}


	private <C> boolean checkAnnotation(C command) {
		return command.getClass().isAnnotationPresent(Command.class);
	}


	private boolean checkMethod(Method method) {

		Parameter[] parameters = method.getParameters();
		if (parameters.length == 0) {
			return false;
		}

		if (!method.isAnnotationPresent(Syntax.class)) return false;
		Syntax meta = method.getAnnotation(Syntax.class);
		assert meta != null;

		if (!this.isSenderParam(meta, parameters[0]))
			throw new IllegalArgumentException(
							String.format("First parameter in method '%s' is not a valid command sender instance !", method.getName())
			);

		if (Modifier.isStatic(method.getModifiers()))
			throw new IllegalArgumentException(
							String.format("Method method '%s' is declared static !!", method.getName())
			);


		for (int i = 1; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			if (parameter.isAnnotationPresent(Arg.class) && parameter.isAnnotationPresent(Flag.class)) {
				throw new IllegalArgumentException(String.format(
								"Parameter named '%s' in method '%s' cannot have both annotations @Arg and @Flag ", parameter.getName(), method.getName())
				);
			}
			if (!checkArgParameter(parameters[i])) return false;
		}

		return (method.getReturnType().equals(Void.class)
						|| method.getReturnType().equals(void.class));
	}

	private boolean checkArgParameter(Parameter parameter) {
		return (parameter.isAnnotationPresent(Arg.class) && !parameter.isAnnotationPresent(Flag.class))
						|| (!parameter.isAnnotationPresent(Arg.class) && parameter.isAnnotationPresent(Flag.class));
	}


	@SuppressWarnings("unchecked")
	private <E extends Enum<E>, T> @Nullable Argument<T> getArgFromParameter(CommandManager<?, S> manager, String command, Parameter parameter) {

		Arg annotation = parameter.getAnnotation(Arg.class);
		if (annotation == null) return null;

		ArgumentData data = ArgumentData.of(annotation.id(), annotation.optional(), false);

		Class<?> type = parameter.getType();

		@Nullable Argument<T> arg;
		if (parameter.isAnnotationPresent(Greedy.class) && type.equals(String.class))
			arg = (Argument<T>) Argument.Array(data.getId());

		else if (type.equals(String.class))
			arg = (Argument<T>) Argument.word(data);

		else if (type.isEnum()) {
			arg = (Argument<T>) Argument.Enum(data, (Class<E>) type);

			Class<E> aEnum = (Class<E>) type;
			for (E constant : aEnum.getEnumConstants()) {
				((Argument<E>) arg).suggest(constant);
			}

		} else arg = (Argument<T>) manager.typeRegistry().convertArgument(data, type);

		if (arg != null && !annotation.defaultValue().isBlank() && !annotation.defaultValue().isEmpty()) {
			try {
				arg.setDefaultValue(arg.parse(command, annotation.defaultValue()));
			} catch (ArgumentParseException e) {
				throw new RuntimeException(e);
			}

			if (annotation.description().isEmpty() || annotation.description().isBlank()) {
				arg = arg.description(annotation.description());
			}

		}


		return arg;
	}


	private @Nullable String getFlagFromParameter(@NotNull Parameter parameter) {

		Flag flag = parameter.getAnnotation(Flag.class);
		if (flag == null || (!parameter.getType().equals(boolean.class) && !parameter.getType().equals(Boolean.class)))
			return null;

		return flag.name();
	}


	/**
	 * Reads values from the context used and links it with the method
	 * to be invoked when the command is executed
	 * for example: a method has 2 params, one must be  the command sender and the other can be an argument
	 * the cmd executed is /test2 alert hello world, the second argument is stated to be {@link Greedy} which
	 * will consume the remaining arguments after it's position/index
	 * basically the values are the parameters of the method to be used in invocation of that method
	 * first param must be the sender {@link SenderWrapper} and the other params can be other data types
	 *
	 * @param method  the method that will be invoked
	 * @param sender  the command sender
	 * @param context the command context
	 * @return the parameters of the method to be used in invocation of that method
	 */
	@NotNull
	private <C> Object[] readValues(@NotNull Method method, @NotNull C sender, Context<S> context) {

		var parameters = method.getParameters();
		Object[] values = new Object[parameters.length];
		values[0] = sender;

		for (int p = 1; p < parameters.length; p++) {

			Object value;
			Parameter parameter = parameters[p];
			if (isParamFlag(parameter)) {
				String flagName = getFlagFromParameter(parameter);
				assert flagName != null;
				value = context.flags().isPresent(flagName);
			} else {

				assert isParamArgument(parameter);
				Arg annotation = parameter.getAnnotation(Arg.class);
				assert annotation != null;

				value = context.getArgument(annotation.id());
			}

			values[p] = value;
		}

		return values;
	}

	private boolean isParamArgument(@NotNull Parameter parameter) {
		return parameter.isAnnotationPresent(Arg.class) && !parameter.isAnnotationPresent(Flag.class);
	}

	private boolean isParamFlag(@NotNull Parameter parameter) {
		return parameter.isAnnotationPresent(Flag.class) && !parameter.isAnnotationPresent(Arg.class);
	}

	private @NotNull ResolvedSubCommandMethod loadParentalSubCommandsParameters(
					final @NotNull CommandManager<?, S> manager,
					final @NotNull String commandName,
					final @NotNull Class<?> subCommandClass,
					final @NotNull Method method
	) {

		if (!subCommandClass.isAnnotationPresent(SubCommandInfo.class)) {
			throw new IllegalArgumentException(subInfoNotFound(subCommandClass));
		}

		if (!subCommandClass.isAnnotationPresent(Syntax.class)) {
			throw new IllegalArgumentException(String.format("Subcommand class %s is NOT annotated with @Syntax", subCommandClass.getName()));
		}

		Syntax syntax = subCommandClass.getAnnotation(Syntax.class);
		assert syntax != null;
		SubCommandInfo info = subCommandClass.getAnnotation(SubCommandInfo.class);
		assert info != null;

		Class<?> parent = info.parent();
		if (parent.equals(Object.class)) {

			var data = loadMethodParameters(manager, commandName, syntax, method);
			var args = data.getRight();

			Argument<?>[] modifiedArgs = new Argument[args.length + 1];
			modifiedArgs[0] = Argument.literal(info.name())
							.aliases(info.aliases());

			if (args.length - 1 >= 0) System.arraycopy(args, 0, modifiedArgs, 1, args.length - 1);

			return new ResolvedSubCommandMethod(args, modifiedArgs, data.getLeft());
		}

		LinkedList<String> args = new LinkedList<>();

		args.add(info.name());
		args.addAll(Arrays.asList(syntax.syntax().split(Pattern.quote(" "))));


		do {
			SubCommandInfo parentInfo = parent.getAnnotation(SubCommandInfo.class);
			assert parentInfo != null;

			Syntax parentSyntax = parent.getAnnotation(Syntax.class);
			assert parentSyntax != null;

			for (var split : parentSyntax.syntax().split(Pattern.quote(" "))) {
				args.addFirst(split);
			}

			args.addFirst(parentInfo.name());

			parent = parentInfo.parent();

		} while (!parent.equals(Object.class));


		Syntax newSyntax = new Syntax() {

			private final String syntaxStr = String.join(" ", args);

			@Override
			public Class<? extends Annotation> annotationType() {
				return Syntax.class;
			}

			@Override
			public String syntax() {
				return syntaxStr;
			}

			@Override
			public Class<?> senderType() {
				return syntax.senderType();
			}

			@Override
			public String description() {
				return syntax.description();
			}

			@Override
			public String permission() {
				return syntax.permission();
			}

		};

		String[] split = syntax.syntax().split(Pattern.quote(" "));
		Argument<?>[] arguments = new Argument[split.length];
		Parameter[] parameters = method.getParameters();
		for (int i = 0, p = 1; i < arguments.length && p < parameters.length; i++, p++) {

			Parameter parameter = parameters[p];
			Arg argAnnotation = parameter.getAnnotation(Arg.class);
			if (argAnnotation == null) {
				throw new IllegalStateException(String.format("redundant parameter in method %s doesnt have '@Arg' ", method.getName()));
			}
			String id = CommandSyntax.fetchArgId(argAnnotation.id());
			while (!id.equalsIgnoreCase(split[i])) {
				p++;
				if (p >= parameters.length) break;
				parameter = parameters[p];
				argAnnotation = parameter.getAnnotation(Arg.class);
				if (argAnnotation == null) {
					throw new IllegalStateException(String.format("redundant parameter in method %s doesnt have '@Arg' ", method.getName()));
				}

				id = CommandSyntax.fetchArgId(argAnnotation.id());
			}

			arguments[i] = getArgFromParameter(manager, commandName, parameter);
		}

		var data = loadMethodParameters(manager, commandName, newSyntax, method);
		return new ResolvedSubCommandMethod(arguments, data.getRight(), data.getLeft());
	}


	/**
	 * Loads arguments from the method and it's parameters
	 * into an array of required syntax arguments
	 *
	 * @param manager the manager needed to load the argument types using the internal
	 *                argument type registry !
	 * @param method  the method that has the execution
	 * @return the args detected from the annotated command method , along with
	 * the flags detected !
	 */

	private <T> Pair<SyntaxFlags, Argument<?>[]> loadMethodParameters(
					final @NotNull CommandManager<?, S> manager,
					final @NotNull String commandName,
					final @NotNull Syntax syntaxMeta,
					final @NotNull Method method
	) {

		String syntax = syntaxMeta.syntax();

		String[] split = syntax.split(Pattern.quote(" "));
		Argument<?>[] args = new Argument[split.length];
		Parameter[] typeParameters = method.getParameters();

		SyntaxFlags flags = SyntaxFlags.of();

		for (int i = 0, p = 1; i < split.length && p < typeParameters.length; i++, p++) {
			String arg = split[i];

			if (CommandSyntax.isArgLiteral(arg)) {
				args[i] = Argument.literal(arg);
				p--;

			} else {

				Parameter parameter = typeParameters[p];
				Arg parameterArgAnnotation = parameter.getAnnotation(Arg.class);
				assert parameterArgAnnotation != null;

				String syntaxId = CommandSyntax.fetchArgId(arg);
				if (!syntaxId.equals(parameterArgAnnotation.id()))
					throw new IllegalArgumentException(String.format(
									"Argument id in syntax '%s' doesn't match the corresponding parameter arg id '%s'", syntaxId, parameterArgAnnotation.id()));


				boolean optional = CommandSyntax.isArgOptional(arg);
				if (optional != parameterArgAnnotation.optional())
					throw new IllegalArgumentException(String.format(
									"Argument optional status(optional=%b) in syntax doesn't match the corresponding parameter optional status(optional=%b)", optional, parameterArgAnnotation.optional()));

				@Nullable Argument<T> argument = getArgFromParameter(manager, commandName, parameter);

				if (argument != null) {
					argument.setOptional(optional);

					this.handleArgumentIfNumber(argument, parameter);

					if (parameter.isAnnotationPresent(Suggest.class)) {
						Suggest suggest = parameter.getAnnotation(Suggest.class);
						assert suggest != null;

						for (var suggestion : suggest.value()) {
							try {
								argument.suggest(argument.parse(commandName, suggestion));
							} catch (ArgumentParseException e) {
								throw new RuntimeException(e);
							}
						}

					}


					args[i] = argument;

				} else if (parameter.isAnnotationPresent(Arg.class)) {

					throw new IllegalArgumentException(
									String.format("Parameter '%s' in method '%s' "
																	+ "has no parser for that type",
													parameter.getName(), method.getName()));
				}


			}

		}

		for (Parameter parameter : typeParameters) {

			if (isSenderParam(syntaxMeta, parameter)) continue;

			String flag = getFlagFromParameter(parameter);

			String exceptionMessage = null;

			if (!isParamArgument(parameter))
				exceptionMessage = String.format("Redundant parameter '%s' in method '%s' with type '%s'",
								parameter.getName(), method.getName(), parameter.getType().getName());
			else if (flag != null && !manager.flagRegistry().flagExists(flag))
				exceptionMessage = String.format("Unknown flag '%s' parameter in method '%s'", flag, method.getName());


			if (exceptionMessage != null)
				throw new IllegalArgumentException(exceptionMessage);

			//adding the collected flag
			flags.addFlag(flag);

		}

		return Pair.of(flags, args);
	}


	private boolean isSenderParam(Syntax meta, Parameter parameter) {

		return manager.getSenderWrapper().canBeSender(parameter.getType())
						|| (manager.senderProviderRegistry().hasProviderFor(parameter.getType())
						&& meta.senderType().isAssignableFrom(parameter.getType()));
	}

	@SuppressWarnings("unchecked")
	private <N extends Number> void handleArgumentIfNumber(Argument<?> argument, Parameter parameter) {
		if (!(argument instanceof ArgumentNumber) || !parameter.isAnnotationPresent(Range.class)) {
			return;
		}

		ArgumentNumber<N> argNum = (ArgumentNumber<N>) argument;

		Range range = parameter.getAnnotation(Range.class);
		assert range != null;

		if (!range.min().isEmpty())
			argNum.min(argNum.getParser().apply(range.min()));

		if (!range.max().isEmpty())
			argNum.max(argNum.getParser().apply(range.max()));


		manager.setNumericArgumentSuggestions(argNum);
	}

	private String subInfoNotFound(Class<?> subClass) {
		return String.format("Subcommand class '%s' is NOT annotated with @SubCommandInfo", subClass.getName());
	}


	private record ResolvedSubCommandMethod(Argument<?>[] arguments,
	                                        Argument<?>[] actualArguments,
	                                        SyntaxFlags flags) {


	}

}