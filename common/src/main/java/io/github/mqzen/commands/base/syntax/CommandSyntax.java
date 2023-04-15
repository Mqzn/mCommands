package io.github.mqzen.commands.base.syntax;

import io.github.mqzen.commands.arguments.Argument;
import io.github.mqzen.commands.arguments.ArgumentLiteral;
import io.github.mqzen.commands.base.Information;
import io.github.mqzen.commands.base.context.CommandContext;
import io.github.mqzen.commands.base.context.DelegateCommandContext;
import io.github.mqzen.commands.base.manager.AmbiguityChecker;
import io.github.mqzen.commands.base.manager.CommandManager;
import io.github.mqzen.commands.base.manager.flags.ContextFlagRegistry;
import io.github.mqzen.commands.utilities.text.TextConvertible;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CommandSyntax<S> implements TextConvertible<S> {

	// args and execution

	@NotNull
	private final static String[] argumentFormatPrefixSuffix = {
					"<", ">", "[", "]"
	};
	@NotNull
	private final CommandManager<?, S> manager;
	@NotNull
	private final String commandLabel;
	@NotNull
	private final CommandExecution<S> execution;
	@NotNull
	private final List<Argument<?>> arguments;
	@NotNull
	@Getter
	private final SyntaxFlags flags;
	private final int trimmedLength;
	private final int withoutFlagsOrOptionalLength;
	@Nullable
	@Getter
	private Information info = null;

	private CommandSyntax(@NotNull CommandManager<?, S> manager,
	                      @NotNull String commandLabel,
	                      @NotNull CommandExecution<S> execution,
	                      @NotNull SyntaxFlags flags,
	                      @NotNull Argument<?>... args) {
		this.manager = manager;
		this.commandLabel = commandLabel;
		this.execution = execution;
		this.flags = flags;
		this.arguments = Arrays.asList(args);
		this.trimmedLength = trimmedSyntaxLength();
		this.withoutFlagsOrOptionalLength = trimmedLength - flags.count();
	}

	private CommandSyntax(@NotNull CommandManager<?, S> manager,
	                      @NotNull String commandLabel,
	                      @NotNull CommandExecution<S> execution,
	                      @NotNull Argument<?>... args) {
		this(manager, commandLabel, execution, SyntaxFlags.of(), args);
	}

	@NotNull
	public static <S> CommandSyntax<S> of(@NotNull CommandManager<?, S> manager,
	                                      @NotNull String commandLabel,
	                                      @NotNull CommandExecution<S> execution,
	                                      @NotNull Argument<?>... args) {
		return new CommandSyntax<>(manager, commandLabel, execution, args);
	}

	@NotNull
	public static <S> CommandSyntax<S> of(@NotNull CommandManager<?, S> manager,
	                                      @NotNull String commandLabel,
	                                      @NotNull CommandExecution<S> execution,
	                                      @NotNull SyntaxFlags flags,
	                                      @NotNull Argument<?>... args) {
		return new CommandSyntax<>(manager, commandLabel, execution, flags, args);
	}


	public static boolean isArgRequired(String argSyntax) {
		return argSyntax.startsWith(argumentFormatPrefixSuffix[0]) && argSyntax.endsWith(argumentFormatPrefixSuffix[1]);
	}

	public static boolean isArgOptional(String argSyntax) {
		return argSyntax.startsWith(argumentFormatPrefixSuffix[2]) && argSyntax.endsWith(argumentFormatPrefixSuffix[3]);
	}

	public static boolean isArgLiteral(String argSyntax) {
		return !isArgRequired(argSyntax) && !isArgOptional(argSyntax);
	}

	public static String fetchArgId(String argSyntax) {
		StringBuilder builder = new StringBuilder(argSyntax);
		builder.deleteCharAt(argSyntax.length() - 1);
		builder.deleteCharAt(0);

		return builder.toString();
	}

	@Nullable
	public Argument<?> getArgument(int index) {

		if (index < 0 || index >= arguments.size()) return null;
		return arguments.get(index);
	}

	public CommandSyntax<S> withInfo(@NotNull String permission, @NotNull String description) {
		this.info = new Information(permission, description);
		return this;
	}

	public CommandSyntax<S> withFlag(String flag) {
		flags.addFlag(flag);
		return this;
	}

	public int length() {
		return arguments.size();
	}


	public int withoutOptionalLength() {
		return trimmedLength;
	}

	public int withoutFlagsOrOptionalArgumentsLength() {
		return withoutFlagsOrOptionalLength;
	}

	/**
	 * Checks if the syntax matches the context input
	 * Here are some examples:
	 * First syntax: /perms user <user> permission add <permission> [value] (literal => word => literal => literal => word)
	 * Second Syntax: /perms user <user> permission remove <permission> (literal => word => literal => literal => word)
	 *
	 * @param commandContext the input
	 * @return whether the syntax is suitable for the context used !
	 */
	public boolean matchesContext(@NotNull DelegateCommandContext<S> commandContext) {

		manager.log("Checking syntax '%s' with context '%s'", this.formatted(), commandContext.rawFormat());
		if (!AmbiguityChecker.hasLiteralArgs(this) && useSpace()) {
			manager.log("Syntax has space consumer argument without literals");
			return true;
		}


		final int capacity = this.arguments.size();
		manager.log("number of required arguments '%s'", capacity);

		manager.log("number of flags used in context %s", commandContext.flagsUsed());

		final int maxRawArgsCount = commandContext.getRawArguments().size();
		final int minRawArgsCount = maxRawArgsCount - commandContext.flagsUsed();

		manager.log("minimum raw Args= %s, maximum raw Args =%s", minRawArgsCount, maxRawArgsCount);

		/*if(capacity < minRawArgsCount || capacity > maxRawArgsCount) {
			return false;
		}*/

		for (int index = 0, rawIndex = 0; index < capacity; index++) {

			Argument<?> required = arguments.get(index);

			String raw = commandContext.getRawArgument(rawIndex);

			if (raw == null) {

				if (required.isOptional()) {
					rawIndex++;
					continue;
				}

				return rawIndex < commandContext.getRawArguments().size();
			}

			if (ContextFlagRegistry.isRawArgumentFlag(raw)) {
				manager.log("Raw arg '%s' is a flag", raw);
				raw = commandContext.getRawArgument(++rawIndex);
				manager.log("Skipped to next raw argument '%s'", raw);
			}

			manager.log("Required argument id is '%s' at index '%s'", required.id(), index);

			if (required instanceof ArgumentLiteral && !required.id().equalsIgnoreCase(raw)) {

				return false;
			}

			rawIndex++;
		}


		return true;
	}

	public boolean useSpace() {
		for (var arg : arguments)
			if (arg.useRemainingSpace()) return true;

		return false;
	}

	public @NotNull List<Argument<?>> getArguments() {
		return arguments;
	}

	public void execute(S sender, CommandContext<S> commandContext) {
		execution.execute(sender, commandContext);
	}

	private int trimmedSyntaxLength() {
		//without optional args

		List<Argument<?>> argsCopy = new ArrayList<>(arguments);
		argsCopy.removeIf(Argument::isOptional);

		return argsCopy.size();
	}

	public void debug() {

		manager.log("Argument for this syntax :");
		for (var arg : arguments) {
			manager.log("- %s<%s>", arg.getClass().getSimpleName(), arg.id());
		}

	}

	public String formatted() {

		String start = manager.commandStarter() == ' ' ? "" : String.valueOf(manager.commandStarter());
		StringBuilder builder = new StringBuilder(start).append(commandLabel).append(" ");

		for (int i = 0; i < arguments.size(); i++) {

			var arg = arguments.get(i);

			if (arg instanceof ArgumentLiteral) {
				builder.append(arg.id()).append(" ");
				continue;
			}

			String prefix, suffix;
			if (arg.isOptional()) {
				prefix = argumentFormatPrefixSuffix[2];
				suffix = argumentFormatPrefixSuffix[3];
			} else {
				prefix = argumentFormatPrefixSuffix[0];
				suffix = argumentFormatPrefixSuffix[1];
			}

			builder.append(prefix)
							.append(arg.id())
							.append(suffix);

			if (i != arguments.size() - 1) builder.append(" ");


		}

		return builder.toString();
	}

	@Override
	public @NonNull TextComponent toText(@NotNull S sender) {
		return Component.text(this.formatted());
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CommandSyntax<?> that)) return false;
		return commandLabel.equals(that.commandLabel) && arguments.equals(that.arguments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(commandLabel, arguments);
	}

}
