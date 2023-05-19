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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandSyntax<S> implements TextConvertible<S> {
	
	@NotNull
	private final static String[] argumentFormatPrefixSuffix = {
		"<", ">", "[", "]"
	};
	
	@NotNull
	protected final List<Argument<?>> arguments;
	
	@Nullable
	protected final CommandExecution<S, ?> execution;
	
	@NotNull
	protected final Class<?> senderClass;
	
	@NotNull
	protected final String commandLabel;
	
	@NotNull
	protected final SyntaxFlags flags;
	
	@Nullable
	protected Information info = null;
	
	<C> CommandSyntax(@NotNull Class<C> senderClass,
	                  @NotNull String commandLabel,
	                  @Nullable CommandExecution<S, C> execution,
	                  @NotNull SyntaxFlags flags,
	                  @NotNull List<Argument<?>> args) {
		this.senderClass = senderClass;
		this.commandLabel = commandLabel;
		this.execution = execution;
		this.flags = flags;
		this.arguments = args;
		arguments.removeIf(Objects::isNull);
	}
	
	
	public static <S, C> CommandSyntax<S> empty(
		Class<C> senderClass, String cmdLabel) {
		return new CommandSyntax<>(senderClass, cmdLabel,
			(sender, context) -> {
			}, SyntaxFlags.of(), new ArrayList<>());
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
	
	public static boolean aliasesIncludes(CommandAliases commandAliases, String name) {
		return aliasesIncludes(commandAliases.getArray(), name);
	}
	
	public static boolean aliasesIncludes(String[] aliases, String name) {
		for (String aliase : aliases) {
			if (aliase.equalsIgnoreCase(name))
				return true;
		}
		
		return false;
	}
	
	public @NotNull Class<?> getSenderClass() {
		return senderClass;
	}
	
	public @Nullable CommandExecution<S, ?> getExecution() {
		return execution;
	}
	
	public @Nullable Information getInfo() {
		return info;
	}
	
	public void setInfo(@Nullable Information info) {
		this.info = info;
	}
	
	public @NotNull SyntaxFlags getFlags() {
		return flags;
	}
	
	public @NotNull String getCommandLabel() {
		return commandLabel;
	}
	
	@Nullable
	public Argument<?> getArgument(int index) {
		
		if (index < 0 || index >= arguments.size()) return null;
		return arguments.get(index);
	}
	
	public void addFlag(String flag) {
		flags.addFlag(flag);
	}
	
	public int length() {
		return arguments.size();
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
		
		int minSyntaxLength = (int) arguments.stream()
			.filter((arg) -> !arg.isOptional())
			.count() - flags.count();
		
		int maxSyntaxLength = arguments.size() + flags.count();
		int rawLength = commandContext.getRawArguments().size();
		
		if (rawLength < minSyntaxLength || rawLength > maxSyntaxLength) return false;
		
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
			
			while (ContextFlagRegistry.isRawArgumentFlag(raw)) {
				raw = commandContext.getRawArgument(++rawIndex);
			}
			
			if (required instanceof ArgumentLiteral) {
				
				if (!required.id().equalsIgnoreCase(raw) && !aliasesIncludes(((ArgumentLiteral) required).getAliases(), raw)) {
					return false;
				}
				
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
		if (execution == null) return;
		((CommandExecution<S, C>) execution).execute(sender, commandContext);
	}
	
	public String formatted(CommandManager<?, S> commandManager) {
		
		String start = commandManager.commandPrefix() == ' ' ? "" : String.valueOf(commandManager.commandPrefix());
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
	public @NotNull TextComponent toText(@NotNull CommandManager<?, S> manager, @NotNull S sender) {
		return Component.text(this.formatted(manager));
	}
	
	public boolean isSubCommand() {
		return this instanceof SubCommandSyntax<S>;
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
