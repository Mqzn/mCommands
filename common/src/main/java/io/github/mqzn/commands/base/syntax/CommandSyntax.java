package io.github.mqzn.commands.base.syntax;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.arguments.ArgumentLiteral;
import io.github.mqzn.commands.base.Information;
import io.github.mqzn.commands.base.SenderWrapper;
import io.github.mqzn.commands.base.context.CommandContext;
import io.github.mqzn.commands.base.context.DelegateCommandContext;
import io.github.mqzn.commands.base.manager.AmbiguityChecker;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.manager.flags.ContextFlagRegistry;
import io.github.mqzn.commands.base.syntax.tree.CommandTree;
import io.github.mqzn.commands.utilities.ArgumentSyntaxUtility;
import io.github.mqzn.commands.utilities.text.TextConvertible;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CommandSyntax<S> implements TextConvertible<S>, Comparable<CommandSyntax<S>> {
	
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
	private final CommandManager<?, S> manager;
	
	@Nullable
	protected Information info = null;
	
	<C> CommandSyntax(@NotNull CommandManager<?, S> manager,
	                  @NotNull Class<C> senderClass,
	                  @NotNull String commandLabel,
	                  @Nullable CommandExecution<S, C> execution,
	                  @NotNull SyntaxFlags flags,
	                  @NotNull List<Argument<?>> args) {
		this.manager = manager;
		this.senderClass = senderClass;
		this.commandLabel = commandLabel;
		this.execution = execution;
		this.flags = flags;
		this.arguments = args;
		arguments.removeIf(Objects::isNull);
	}
	
	public static <S> List<Argument<?>> getArguments(CommandTree<S> tree, CommandSyntax<S> syntax) {
		if (syntax instanceof SubCommandSyntax<S> sub) return tree.getParentalArguments(sub.key());
		return syntax.getArguments();
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
	 *
	 * @param commandContext the input
	 * @return whether the syntax is suitable for the context used !
	 */
	public boolean matchesContext(@NotNull DelegateCommandContext<S> commandContext) {
		
		if (!AmbiguityChecker.hasLiteralArgs(this) && useSpace()) {
			return true;
		}
		
		final int capacity = arguments.size();
		
		int minSyntaxLength = (int) arguments.stream()
			.filter((arg) -> !arg.isOptional() && !arg.useRemainingSpace())
			.count() - flags.count();
		
		int maxSyntaxLength = capacity + flags.count();
		
		int greedyIndex = -1;
		for (int i = 0; i < arguments.size(); i++) {
			if (arguments.get(i).useRemainingSpace()) {
				greedyIndex = i;
				break;
			}
		}
		
		int rawLength = commandContext.getRawArguments().size();
		if (greedyIndex != -1) {
			final int originalRawLength = rawLength;
			rawLength = originalRawLength - (originalRawLength - greedyIndex);
		}
		
		if (rawLength < minSyntaxLength || rawLength > maxSyntaxLength) return false;
		
		for (int index = 0, rawIndex = 0; index < capacity; index++) {
			
			Argument<?> required = getArgument(index);
			String raw = commandContext.getRawArgument(rawIndex);
			
			assert required != null;
			
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
				
				if (!required.id().equalsIgnoreCase(raw) && !ArgumentSyntaxUtility.aliasesIncludes(((ArgumentLiteral) required).getAliases(), raw)) {
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
	
	public boolean hasArg(Argument<?> argument) {
		return arguments.contains(argument);
	}
	
	@SuppressWarnings("unchecked")
	public <C> void execute(C sender, CommandContext<S> commandContext) {
		if (execution == null) return;
		((CommandExecution<S, C>) execution).execute(sender, commandContext);
	}
	
	@Override
	public @NotNull TextComponent toText(@NotNull CommandManager<?, S> manager, @NotNull S sender) {
		return Component.text(ArgumentSyntaxUtility.format(manager, commandLabel, arguments));
	}
	
	public boolean isSubCommand() {
		return this instanceof SubCommandSyntax<S>;
	}
	
	@Override
	public String toString() {
		return ArgumentSyntaxUtility.format(manager, "", arguments);
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
	
	@Override
	public int compareTo(@NotNull CommandSyntax<S> other) {
		List<Argument<?>> args = this.getArguments();
		List<Argument<?>> otherArgs = other.getArguments();
		
		if (args.isEmpty() && otherArgs.isEmpty()) return 0;
		if (args.isEmpty()) return -1;
		if (otherArgs.isEmpty()) return 1;
		
		var firstArg = args.get(0);
		var otherFirstArg = otherArgs.get(0);
		
		if (firstArg instanceof ArgumentLiteral && otherFirstArg instanceof ArgumentLiteral)
			return args.size() - otherArgs.size();
		
		else if (firstArg instanceof ArgumentLiteral)
			return -1;
		
		else if (otherFirstArg instanceof ArgumentLiteral)
			return 1;
		
		return args.size() - otherArgs.size();
	}
	
	public boolean checkHasPermission(@NotNull SenderWrapper<S> wrapper, S sender) {
		if (info == null || info.permission() == null
			|| info.permission().isEmpty() || info.permission().isBlank()) return true;
		
		return wrapper.hasPermission(sender, info.permission());
	}
	
}
