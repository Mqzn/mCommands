package io.github.mqzn.commands.base.syntax;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.arguments.ArgumentLiteral;
import io.github.mqzn.commands.base.Information;
import io.github.mqzn.commands.base.context.CommandContext;
import io.github.mqzn.commands.base.context.DelegateCommandContext;
import io.github.mqzn.commands.base.manager.AmbiguityChecker;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.manager.flags.ContextFlagRegistry;
import io.github.mqzn.commands.utilities.text.TextConvertible;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CommandSyntax<S> implements TextConvertible<S> {

	// args and execution

	@NotNull
	private final static String[] argumentFormatPrefixSuffix = {
					"<", ">", "[", "]"
	};

	@Getter
	private final Class<?> senderClass;

	@NotNull
	private final String commandLabel;

	@NotNull
	private final CommandExecution<S, ?> execution;

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


	<C> CommandSyntax(Class<C> senderClass,
	                  @NotNull String commandLabel,
	                  @NotNull CommandExecution<S, C> execution,
	                  @NotNull SyntaxFlags flags,
	                  @NotNull List<Argument<?>> args) {
		this.senderClass = senderClass;
		this.commandLabel = commandLabel;
		this.execution = execution;
		this.flags = flags;
		this.arguments = args;
		this.trimmedLength = trimmedSyntaxLength();
		this.withoutFlagsOrOptionalLength = trimmedLength - flags.count();
	}

	<C> CommandSyntax(@NotNull Class<C> senderClass,
	                  @NotNull String commandLabel,
	                  @NotNull CommandExecution<S, C> execution,
	                  @NotNull List<Argument<?>> args) {
		this(senderClass, commandLabel, execution, SyntaxFlags.of(), args);
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

	public void setInfo(@NotNull String permission, @NotNull String description) {
		this.info = new Information(permission, description);
	}

	public void addFlag(String flag) {
		flags.addFlag(flag);
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
	 *
	 * @param commandContext the input
	 * @return whether the syntax is suitable for the context used !
	 */
	public boolean matchesContext(@NotNull DelegateCommandContext<S> commandContext) {

		if (!AmbiguityChecker.hasLiteralArgs(this) && useSpace()) {
			return true;
		}


		final int capacity = this.arguments.size();

		final int maxRawArgsCount = commandContext.getRawArguments().size();
		final int minRawArgsCount = maxRawArgsCount - commandContext.flagsUsed();

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
				raw = commandContext.getRawArgument(++rawIndex);
			}

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

	@SuppressWarnings("unchecked")
	public <C> void execute(C sender, CommandContext<S> commandContext) {
		((CommandExecution<S, C>) execution).execute(sender, commandContext);
	}

	private int trimmedSyntaxLength() {
		//without optional args

		List<Argument<?>> argsCopy = new ArrayList<>(arguments);
		argsCopy.removeIf(Argument::isOptional);

		return argsCopy.size();
	}


	public String formatted(CommandManager<?, S> commandManager) {

		String start = commandManager.commandStarter() == ' ' ? "" : String.valueOf(commandManager.commandStarter());
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
	public @NonNull TextComponent toText(@NotNull CommandManager<?, S> manager, @NotNull S sender) {
		return Component.text(this.formatted(manager));
	}


	public void setInfo(@Nullable Information info) {
		this.info = info;
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
