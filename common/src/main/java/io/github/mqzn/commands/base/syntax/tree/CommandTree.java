package io.github.mqzn.commands.base.syntax.tree;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.arguments.ArgumentStringArray;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.context.DelegateCommandContext;
import io.github.mqzn.commands.base.manager.flags.ContextFlagRegistry;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import io.github.mqzn.commands.help.SubCommandHelp;
import io.github.mqzn.commands.utilities.ArgumentSyntaxUtility;

import java.util.*;
import java.util.function.Predicate;

/**
 * This class represents a tree data structure
 * containing all nodes of subcommands of a
 * particular command.
 *
 * @param <S> the sender type
 * @author Mqzen
 */
public final class CommandTree<S> {
	
	private final Command<S> command;
	private final Map<SubCommandKey<S>, SubCommandSyntax<S>> subCommands;
	private final Map<String, CommandNode<S>> roots;
	private final SubCommandArgumentTree<S> subtree;
	
	private CommandTree(Command<S> command) {
		this.command = command;
		this.subCommands = new HashMap<>();
		this.roots = new HashMap<>();
		init();
		this.subtree = SubCommandArgumentTree.wrap(command, this);
	}
	
	public static <S> CommandTree<S> create(Command<S> command) {
		return new CommandTree<>(command);
	}
	
	public static <S> String getLastRawArgument(DelegateCommandContext<S> context) {
		String lastArgument = context.getRawArgument(context.getRawArguments().size() - 1);
		for (int i = context.getRawArguments().size() - 1; i >= 0; i--) {
			String raw = context.getRawArgument(i);
			if (ContextFlagRegistry.isRawArgumentFlag(raw)) continue;
			lastArgument = raw;
			break;
		}
		return lastArgument;
	}
	
	public Map<String, CommandNode<S>> getRoots() {
		return roots;
	}
	
	/**
	 * Creates chains linked to their starting subcommand
	 * where the subcommand must be a root
	 */
	private void init() {
		for (CommandSyntax<S> syntax : command.syntaxes()) {
			if (!syntax.isSubCommand()) continue;
			SubCommandSyntax<S> subCommandSyntax = (SubCommandSyntax<S>) syntax;
			
			if (subCommandSyntax.hasChildren()) {
				SubCommandHelp<S> helpSubCommand = new SubCommandHelp<>(command.manager(), command.name(), subCommandSyntax);
				SubCommandKey<S> helpSubKey = SubCommandKey.create(helpSubCommand);
				subCommands.putIfAbsent(helpSubKey, helpSubCommand);
				subCommandSyntax.addChild(helpSubCommand);
			}
			
			SubCommandKey<S> subKey = SubCommandKey.create(subCommandSyntax);
			
			subCommands.put(subKey, subCommandSyntax);
			
			
			if (subCommandSyntax.isOrphan()) {
				roots.put(subCommandSyntax.getName(), new CommandNode<>(
					command, subCommandSyntax
				));
			}
		}
		
		for (CommandNode<S> root : roots.values()) {
			if (root.data.hasChildren()) {
				loadChildrenNodes(root);
			}
			//debugRootContent(root);
		}
		
		
	}
	
	/**
	 * Loads children nodes from the root
	 *
	 * @param root the root
	 */
	private void loadChildrenNodes(CommandNode<S> root) {
		
		for (String child : root.data.getChildren()) {
			String parentName = root.data.getName().equalsIgnoreCase(child) ? null : root.data.getName();
			
			SubCommandSyntax<S> childSubCommand = getSubCommand(SubCommandKey.create(parentName, child));
			if (childSubCommand == null) continue;
			
			CommandNode<S> childNode = new CommandNode<>(
				command, childSubCommand
			);
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
	public synchronized CommandSearchResult<S> traverse(DelegateCommandContext<S> context) {
		CommandNode<S> root = findRoot(context);
		if (root == null) return new CommandSearchResult<>(null, CommandSearchResultState.NOT_FOUND);
		if (matchesContext(root.data, context))
			return new CommandSearchResult<>(root.data, CommandSearchResultState.FOUND);
		
		String lastArg = getLastRawArgument(context);
		if (lastArg != null && root.data.matches(lastArg)) {
			return new CommandSearchResult<>(
				root.data,
				CommandSearchResultState.FOUND_INCOMPLETE
			);
		} else if (root.data.hasChildren()) return searchInRoot(lastArg, root, context);
		
		return new CommandSearchResult<>(null, CommandSearchResultState.NOT_FOUND);
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
		CommandNode<S> rootNode = roots.get(nextLiteral.toLowerCase(Locale.getDefault()));
		if (rootNode != null) return rootNode;
		for (CommandNode<S> root : roots.values()) {
			if (ArgumentSyntaxUtility.aliasesIncludes(root.data.getAliases(), nextLiteral)) {
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
	private CommandSearchResult<S> searchInRoot(
		String lastArg,
		CommandNode<S> root,
		DelegateCommandContext<S> context
	) {
		
		for (CommandNode<S> nextNode : root.nextNodes) {
			SubCommandSyntax<S> previousParent = getPreviousParent(nextNode.data, context);
			if (matchesContext(nextNode.data, context)) return new CommandSearchResult<>(
				nextNode.data,
				CommandSearchResultState.FOUND
			);
			else if (lastArg != null && nextNode.data.matches(lastArg) &&
				!nextNode.data.isOrphan() && previousParent != null
				&& previousParent.matches(nextNode.data.getParent())
			) {
				
				return new CommandSearchResult<>(
					nextNode.data,
					CommandSearchResultState.FOUND_INCOMPLETE
				);
			} else if (nextNode.data.hasChildren()) {
				CommandSearchResult<S> deepSearch = searchInRoot(lastArg, nextNode, context);
				if (deepSearch.state != CommandSearchResultState.NOT_FOUND) return deepSearch;
			}
		}
		return new CommandSearchResult<>(null, CommandSearchResultState.NOT_FOUND);
	}
	
	public SubCommandSyntax<S> getSubCommand(SubCommandKey<S> key) {
		return subCommands.get(key);
	}
	
	public LinkedList<Argument<?>> getParentalArguments(SubCommandKey<S> key) {
		LinkedList<Argument<?>> arguments = subtree.getSubCommandArguments(key);
		return arguments != null ? arguments : new LinkedList<>();
	}
	
	/**
	 * Checks if the syntax matches the context input
	 *
	 * @param commandContext the input
	 * @return whether the syntax is suitable for the context used !
	 */
	private boolean matchesContext(SubCommandSyntax<S> subCommand, DelegateCommandContext<S> commandContext) {
		//getting the previous parent
		boolean matchesLength = matchesContextLength(subCommand, commandContext);
		if (subCommand.isOrphan()) {
			return matchesLength;
		}
		
		SubCommandSyntax<S> previousParent = getPreviousParent(subCommand, commandContext);
		return previousParent != null && previousParent.matches(subCommand.getParent()) && matchesLength;
	}
	
	private SubCommandSyntax<S> getPreviousParent(
		SubCommandSyntax<S> base,
		DelegateCommandContext<S> commandContext
	) {
		int subPositionInContext = commandContext.getRawArguments().indexOf(base.getName());
		if (subPositionInContext == -1) {
			for (int index = 0; index < commandContext.getRawArguments().size(); index++) {
				String arg = commandContext.getRawArgument(index);
				if (ArgumentSyntaxUtility.aliasesIncludes(base.getAliases(), arg)) {
					subPositionInContext = index;
					break;
				}
			}
		}
		
		SubCommandSyntax<S> parent = null;
		for (int index = subPositionInContext - 1; index >= 0; index--) {
			String rawArg = commandContext.getRawArgument(index);
			SubCommandSyntax<S> parentSub = searchForSub(rawArg);
			if (parentSub != null) {
				parent = parentSub;
				break;
			}
		}
		return parent;
	}
	
	private boolean matchesContextLength(
		SubCommandSyntax<S> subCommand,
		DelegateCommandContext<S> commandContext
	) {
		int subPosition = getSubCommandPosition(subCommand, commandContext);
		List<Argument<?>> arguments = subCommand.getArguments();
		
		boolean flagsUsed = usedFlagsInContext(commandContext);
		int flagsCount = subCommand.getFlags().count();
		int minSyntaxLength = (int) arguments.stream()
			.filter(arg -> !arg.isOptional())
			.count();
		
		int maxSyntaxLength = arguments.size() + (flagsUsed ? flagsCount : 0);
		
		int greedyIndex = indexOfFirst(arguments, (arg) -> arg.useRemainingSpace() || arg instanceof ArgumentStringArray);
		int rawArgsLength = commandContext.getRawArguments().size();
		
		if (greedyIndex != -1) {
			//if greedy was used
			int shiftedGreedyIndex = subPosition + greedyIndex + 1;
			int greedyRawLength = rawArgsLength - shiftedGreedyIndex;
			int beforeGreedyArgRawLength = (rawArgsLength - greedyRawLength);
			return (beforeGreedyArgRawLength + 1) >= (minSyntaxLength + beforeGreedyArgRawLength) &&
				(beforeGreedyArgRawLength + 1) <= (maxSyntaxLength + beforeGreedyArgRawLength);
		}
		
		int rawLength = rawArgsLength - subPosition - 1;
		
		return rawLength >= minSyntaxLength && rawLength <= maxSyntaxLength;
	}
	
	private int indexOfFirst(List<Argument<?>> arguments, Predicate<Argument<?>> predicate) {
		for (int i = 0; i < arguments.size(); i++) {
			var arg = arguments.get(i);
			if (arg == null) break;
			if (predicate.test(arg)) return i;
		}
		
		return -1;
	}
	
	private boolean usedFlagsInContext(DelegateCommandContext<S> context) {
		for (String arg : context.getRawArguments()) {
			if (ContextFlagRegistry.isRawArgumentFlag(arg)) return true;
		}
		return false;
	}
	
	private int getSubCommandPosition(SubCommandSyntax<S> syntax, DelegateCommandContext<S> context) {
		for (int i = 0; i < context.getRawArguments().size(); i++) {
			String raw = context.getRawArgument(i);
			if (syntax.matches(raw)) return i;
		}
		return -1;
	}
	
	public SubCommandSyntax<S> searchForSub(String name) {
		for (SubCommandSyntax<S> sub : subCommands.values()) {
			if (sub.matches(name)) return sub;
		}
		return null;
	}
	
	public enum CommandSearchResultState {
		NOT_FOUND, FOUND, FOUND_INCOMPLETE
	}
	
	/**
	 * A class that defines a subcommand node,
	 * each node has a reference to its children nodes,
	 * the children can be more than one, that's why
	 * a set is being used
	 *
	 * @param <S> the type of the sender
	 */
	public static class CommandNode<S> {
		
		final SubCommandSyntax<S> data;
		private final Set<CommandNode<S>> nextNodes;
		
		public CommandNode(Command<S> command, SubCommandSyntax<S> data) {
			this.data = data;
			this.nextNodes = new HashSet<>(command.syntaxes().size());
		}
		
		public void addNextNode(CommandNode<S> node) {
			nextNodes.add(node);
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other) return true;
			if (!(other instanceof CommandNode<?> otherNode)) return false;
			return data.equals(otherNode.data);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(data);
		}
	}
	
	public static class CommandSearchResult<S> {
		public CommandSyntax<S> data;
		public CommandSearchResultState state;
		
		public CommandSearchResult(CommandSyntax<S> data, CommandSearchResultState state) {
			this.data = data;
			this.state = state;
		}
	}
	
	public record SubCommandKey<S>(String parent, String name) {
		
		public static <S> SubCommandKey<S> create(String parent, String name) {
			return new SubCommandKey<>(parent, name);
		}
		
		public static <S> SubCommandKey<S> create(SubCommandSyntax<S> sub) {
			return new SubCommandKey<>(sub.getParent(), sub.getName());
		}
		
		public SubCommandKey<S> nextKey(String name) {
			if (name == null) return null;
			return SubCommandKey.create(this.name, name);
		}
		
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof SubCommandKey<?> otherKey)) return false;
			return name.equalsIgnoreCase(otherKey.name) && Objects.equals(parent, otherKey.parent);
		}
		
		@Override
		public String toString() {
			return parent + ":" + name;
		}
	}
}


