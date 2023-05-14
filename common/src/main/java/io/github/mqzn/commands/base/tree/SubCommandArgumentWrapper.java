package io.github.mqzn.commands.base.tree;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;


public final class SubCommandArgumentWrapper<S> {
	
	@NotNull
	private final CommandTree<S> tree;
	
	@NotNull
	private final SubCommandSyntax<S> subCommand;
	
	@NotNull
	private final LinkedList<Argument<?>> totalArguments = new LinkedList<>();
	
	private SubCommandArgumentWrapper(@NotNull CommandTree<S> tree,
	                                  @NotNull SubCommandSyntax<S> subCommand) {
		this.tree = tree;
		this.subCommand = subCommand;
		collectArguments();
	}
	
	
	public static <S> SubCommandArgumentWrapper<S> wrap(@NotNull CommandTree<S> tree,
	                                                    @NotNull SubCommandSyntax<S> subCommand) {
		
		return new SubCommandArgumentWrapper<>(tree, subCommand);
	}
	
	private void collectArguments() {
		
		this.totalArguments.add(
			Argument.literal(subCommand.getName())
				.aliases(subCommand.getAliases().getArray())
		);
		
		this.totalArguments.addAll(subCommand.getArguments());
		
		String parent = subCommand.getParent();
		
		while (parent != null) {
			
			SubCommandSyntax<S> parentSubCommand = tree.getSubCommand(parent);
			if (parentSubCommand == null) {
				break;
			}
			
			
			for (var parentArg : parentSubCommand.getArguments()) {
				this.totalArguments.addFirst(parentArg);
			}
			
			this.totalArguments.addFirst(
				Argument.literal(parentSubCommand.getName())
					.aliases(parentSubCommand.getAliases().getArray())
			);
			
			parent = parentSubCommand.getParent();
		}
		
	}
	
	
	public SubCommandSyntax<S> get() {
		return subCommand;
	}
	
	public LinkedList<Argument<?>> parentalArguments() {
		return totalArguments;
	}
	
}
