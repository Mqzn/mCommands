package io.github.mqzn.commands.base.tree;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.context.DelegateCommandContext;
import io.github.mqzn.commands.base.manager.flags.ContextFlagRegistry;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class represents a tree data structure
 * containing all nodes of subcommands of a
 * particular command.
 *
 * @param <S> the sender type
 * @author Mqzen
 */

public final class CommandTree<S> {
	
	@NotNull
	private final Command<S> command;
	
	@NotNull
	private final Map<String, SubCommandSyntax<S>> subCommands = new HashMap<>();
	
	@NotNull
	private final Map<String, CommandNode<S>> roots = new HashMap<>();
	
	@NotNull
	private final Map<String, SubCommandArgumentWrapper<S>> argumentWrappers = new HashMap<>();
	
	private CommandTree(@NotNull Command<S> command) {
		this.command = command;
		this.init();
	}
	
	/**
	 * Constructs a new CommandTree instance using the command
	 * specified for the tree
	 *
	 * @param command the command that will hold this tree
	 * @param <S>     the sender type
	 * @return the command tree instance
	 */
	public static <S> CommandTree<S> create(@NotNull Command<S> command) {
		return new CommandTree<>(command);
	}
	
	public static <S> @Nullable String getLastRawArgument(@NotNull DelegateCommandContext<S> context) {
		String lastArgument = context.getRawArgument(context.getRawArguments().size() - 1);
		for (int i = context.getRawArguments().size() - 1; i >= 0; i--) {
			String raw = context.getRawArgument(i);
			if (ContextFlagRegistry.isRawArgumentFlag(raw)) continue;
			lastArgument = raw;
			break;
		}
		
		return lastArgument;
	}
	
	/**
	 * Creates chains linked to their starting subcommand
	 * where the subcommand must be a root
	 */
	private void init() {
		
		for (CommandSyntax<S> syntax : command.syntaxes()) {
			if (!syntax.isSubCommand()) continue;
			
			SubCommandSyntax<S> subCommandSyntax = (SubCommandSyntax<S>) syntax;
			subCommands.put(subCommandSyntax.getName(), subCommandSyntax);
			
			if (subCommandSyntax.isOrphan()) {
				roots.put(subCommandSyntax.getName(), new CommandNode<>(command, subCommandSyntax));
			}
			
		}
		
		for (var sub : subCommands.values()) {
			argumentWrappers.put(sub.getName(), SubCommandArgumentWrapper.wrap(this, sub));
		}
		
		
		for (CommandNode<S> root : roots.values()) {
			
			if (root.data.hasChildren()) {
				loadChildrenNodes(root);
			}
			
		}
		
		
	}
	
	/**
	 * Loads children nodes from the root
	 *
	 * @param root the root
	 */
	
	private void loadChildrenNodes(CommandNode<S> root) {
		
		for (var child : root.data.getChildren()) {
			
			SubCommandSyntax<S> childSubCommand = subCommands.get(child);
			CommandNode<S> childNode = new CommandNode<>(command, childSubCommand);
			root.addNextNode(childNode);
			
			loadChildrenNodes(childNode);
		}
		
	}
	
	/**
	 * Searches through the subcommands for appropriate subcommand
	 * that was used in the context
	 *
	 * @param context the command context determined by the sender
	 * @return the subcommand to execute
	 */
	public synchronized @NotNull TraversingResult<S> traverse(@NotNull DelegateCommandContext<S> context) {
		
		
		CommandNode<S> root = findRoot(context);
		if (root == null) {
			return new TraversingResult<>(null, TraversingResultState.NOT_FOUND);
		}
		
		if (matchesContext(root.data, context)) {
			return new TraversingResult<>(root.data, TraversingResultState.FOUND);
		}
		
		String lastArg = getLastRawArgument(context);
		
		if (lastArg != null && root.data.matches(lastArg))
			return new TraversingResult<>(root.data, TraversingResultState.FOUND_INCOMPLETE);
		else if (root.data.hasChildren())
			return searchInRoot(lastArg, root, context);
		
		return new TraversingResult<>(null, TraversingResultState.NOT_FOUND);
	}
	
	/**
	 * Finds the root subcommand to start searching in its
	 * chain of children
	 *
	 * @param context the context being executed
	 * @return the root subcommand
	 */
	private CommandNode<S> findRoot(DelegateCommandContext<S> context) {
		
		String nextLiteral = null;
		for (String next : context.getRawArguments()) {
			if (!ContextFlagRegistry.isRawArgumentFlag(next)) {
				nextLiteral = next;
				break;
			}
		}
		
		if (nextLiteral == null) {
			return null;
		}
		
		var rootNode = roots.get(nextLiteral.toLowerCase());
		if (rootNode != null)
			return rootNode;
		
		for (CommandNode<S> root : roots.values()) {
			if (CommandSyntax.aliasesIncludes(root.data.getAliases(), nextLiteral)) {
				return root;
			}
			
		}
		
		return null;
	}
	
	/**
	 * Searches for children used in
	 * the root subcommand
	 *
	 * @param root    the root subcommand
	 * @param context the context being executed
	 * @return the subcommand child that matches the context being executed
	 */
	private @NotNull TraversingResult<S> searchInRoot(@Nullable String lastArg, CommandNode<S> root, DelegateCommandContext<S> context) {
		
		for (CommandNode<S> nextNode : root.nextNodes) {
			
			if (this.matchesContext(nextNode.data, context))
				return new TraversingResult<>(nextNode.data, TraversingResultState.FOUND);
			
			else if (lastArg != null && nextNode.data.matches(lastArg))
				return new TraversingResult<>(nextNode.data, TraversingResultState.FOUND_INCOMPLETE);
			
			else
				return searchInRoot(lastArg, nextNode, context);
			
		}
		
		return new TraversingResult<>(null, TraversingResultState.NOT_FOUND);
	}
	
	@Nullable
	public SubCommandSyntax<S> getSubCommand(String name) {
		return subCommands.get(name);
	}
	
	@Nullable
	private SubCommandArgumentWrapper<S> getArgWrapper(String subCmdName) {
		return argumentWrappers.get(subCmdName);
	}
	
	
	@NotNull
	public List<Argument<?>> getParentalArguments(String subCmdName) {
		var wrapper = getArgWrapper(subCmdName);
		if (wrapper == null) return Collections.emptyList();
		
		return wrapper.parentalArguments();
	}
	
	
	/**
	 * Checks if the syntax matches the context input
	 * Here are some examples:
	 *
	 * @param commandContext the input
	 * @return whether the syntax is suitable for the context used !
	 */
	private boolean matchesContext(@NotNull SubCommandSyntax<S> subCommand, @NotNull DelegateCommandContext<S> commandContext) {
		
		int subPosition = getSubCommandPosition(subCommand, commandContext);
		var arguments = subCommand.getArguments();
		boolean flagsUsed = usedFlagsInContext(commandContext);
		final int flagsCount = subCommand.getFlags().count();
		
		int minSyntaxLength = (int) arguments.stream()
			.filter((arg) -> !arg.isOptional())
			.count();
		
		int maxSyntaxLength = arguments.size() + (flagsUsed ? flagsCount : 0);
		
		final int rawArgsLength = commandContext.getRawArguments().size();
		
		int rawLength = rawArgsLength-subPosition-1;
		
		return rawLength >= minSyntaxLength && rawLength <= maxSyntaxLength;
	}
	
	private boolean usedFlagsInContext(DelegateCommandContext<S> context) {
		for (var arg : context.getRawArguments()) {
			if (ContextFlagRegistry.isRawArgumentFlag(arg)) return true;
		}
		
		return false;
	}
	
	private int getSubCommandPosition(SubCommandSyntax<S> syntax, DelegateCommandContext<S> context) {
		for (int i = 0; i < context.getRawArguments().size(); i++) {
			String raw = context.getRawArgument(i);
			if (raw == null) return -1;
			
			if (syntax.matches(raw)) return i;
		}
		return -1;
	}
	
	public enum TraversingResultState {
		
		NOT_FOUND,
		
		FOUND,
		
		FOUND_INCOMPLETE;
		
	}
	
	/**
	 * A class that defines a subcommand node,
	 * each node has a reference to its children nodes,
	 * the children can be more than one, that's why
	 * a set is being used
	 *
	 * @param <S> the type of the sender
	 */
	private static class CommandNode<S> {
		
		@NotNull
		private final Set<CommandNode<S>> nextNodes;
		@NotNull
		@Setter
		private SubCommandSyntax<S> data;
		
		CommandNode(Command<S> command, @NotNull SubCommandSyntax<S> data) {
			this.data = data;
			this.nextNodes = new HashSet<>(command.syntaxes().size());
		}
		
		public void addNextNode(CommandNode<S> node) {
			nextNodes.add(node);
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof CommandNode<?> that)) return false;
			return data.equals(that.data);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(data);
		}
		
	}
	
	public record TraversingResult<S>(@Nullable CommandSyntax<S> data, @NotNull TraversingResultState state) {
	
	}
	
}
