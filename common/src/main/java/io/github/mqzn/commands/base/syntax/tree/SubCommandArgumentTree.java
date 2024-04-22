package io.github.mqzn.commands.base.syntax.tree;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;

import java.util.*;

/**
 * This class represents the tree of subcommands arguments relations
 * it's very beneficial in cases of complex inheritance between subcommands;
 * while loading/parsing a single subcommand , it's essential to know 2 sets of arguments, which are
 * the arguments of the subcommand being loaded specifically AND the all arguments related to the subcommand being loaded.
 * All arguments related to the subcommand are loaded using the parents of this subcommand
 *
 * @author Mqzen
 * @see CommandTree
 */
@SuppressWarnings("unused")
public final class SubCommandArgumentTree<S> {
	private final Command<S> command;
	private final CommandTree<S> tree;
	private final Map<CommandTree.SubCommandKey<S>, Map<CommandTree.SubCommandKey<S>, Pathway>> data = new HashMap<>();
	
	private SubCommandArgumentTree(Command<S> command, CommandTree<S> tree) {
		this.command = command;
		this.tree = tree;
		initPathways();
		loadParentalPathway();
	}
	
	public static <S> SubCommandArgumentTree<S> wrap(Command<S> command, CommandTree<S> tree) {
		return new SubCommandArgumentTree<>(command, tree);
	}
	
	private void initPathways() {
		for (CommandTree.CommandNode<S> root : tree.getRoots().values())
			loadRootArguments(root, root);
	}
	
	private void loadRootArguments(CommandTree.CommandNode<S> root, CommandTree.CommandNode<S> node) {
		CommandTree.SubCommandKey<S> rootKey = new CommandTree.SubCommandKey<>(root.data.getParent(), root.data.getName());
		
		data.compute(rootKey, (key, oldMap) -> {
			if (oldMap == null) {
				LinkedList<Argument<?>> list = new LinkedList<>();
				list.addLast(Argument.literal(node.data.getName()).aliases(node.data.getAliases().getArray()));
				for (Argument<?> arg : node.data.getArguments()) list.addLast(arg);
				
				Map<CommandTree.SubCommandKey<S>, Pathway> newMap = new HashMap<>();
				newMap.put(node.data.key(), new Pathway(list));
				return newMap;
			} else {
				CommandTree.SubCommandKey<S> nodeKey = node.data.key();
				if (!oldMap.containsKey(nodeKey)) {
					LinkedList<Argument<?>> list = new LinkedList<>();
					list.addLast(Argument.literal(node.data.getName()).aliases(node.data.getAliases().getArray()));
					for (Argument<?> arg : node.data.getArguments()) list.addLast(arg);
					oldMap.put(nodeKey, new Pathway(list));
				} else {
					Pathway oldPathway = oldMap.get(nodeKey);
					oldPathway.addArg(Argument.literal(node.data.getName()).aliases(node.data.getAliases().getArray()));
					for (Argument<?> arg : node.data.getArguments()) oldPathway.addArg(arg);
				}
				return oldMap;
			}
		});
		
		for (String child : node.data.getChildren()) {
			CommandTree.SubCommandKey<S> childKey = CommandTree.SubCommandKey.create(node.data.getName(), child);
			SubCommandSyntax<S> childSub = tree.getSubCommand(childKey);
			if (childSub == null)
				throw new IllegalStateException("Unknown child sub command `" + childKey.name() + "` make sure you registered this subcommand in the main class ");
			loadRootArguments(root, new CommandTree.CommandNode<>(command, childSub));
		}
	}
	
	private void loadParentalPathway() {
		for (Map<CommandTree.SubCommandKey<S>, Pathway> nodePathwayMapping : data.values()) {
			for (Map.Entry<CommandTree.SubCommandKey<S>, Pathway> entry : nodePathwayMapping.entrySet()) {
				CommandTree.SubCommandKey<S> key = entry.getKey();
				Pathway pathway = entry.getValue();
				SubCommandSyntax<S> subCommand = tree.getSubCommand(key);
				if (subCommand == null || subCommand.isOrphan()) continue;
				
				String parentName = key.parent();
				SubCommandSyntax<S> parentSub = tree.searchForSub(parentName);
				while (parentSub != null) {
					List<Argument<?>> reversed = new ArrayList<>(parentSub.getArguments());
					Collections.reverse(reversed);
					for (Argument<?> arg : reversed) pathway.addFirstArg(arg);
					
					pathway.addFirstArg(Argument.literal(parentName).aliases(parentSub.getAliases().getArray()));
					
					parentName = parentSub.getParent();
					if (parentName == null) break;
					parentSub = tree.searchForSub(parentName);
				}
			}
		}
	}
	
	public LinkedList<Argument<?>> getSubCommandArguments(CommandTree.SubCommandKey<S> key) {
		for (Map<CommandTree.SubCommandKey<S>, Pathway> mapping : data.values()) {
			Pathway pathway = mapping.get(key);
			if (pathway != null) return pathway.arguments;
		}
		return null;
	}
	
	private record Pathway(LinkedList<Argument<?>> arguments) {
		
		void addFirstArg(Argument<?> arg) {
			arguments.addFirst(arg);
		}
		
		void addArg(Argument<?> arg) {
			arguments.addLast(arg);
		}
		
		void removeArg(Argument<?> arg) {
			arguments.remove(arg);
		}
		
		Pathway addPathway(Pathway pathway) {
			for (Argument<?> arg : pathway.arguments) addArg(arg);
			return this;
		}
		
		@Override
		public String toString() {
			return arguments.toString();
		}
	}
}

