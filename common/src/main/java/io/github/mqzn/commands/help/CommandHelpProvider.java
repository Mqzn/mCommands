package io.github.mqzn.commands.help;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.utilities.text.ItemPageTextDisplayer;
import org.jetbrains.annotations.NotNull;

public interface CommandHelpProvider<S> {
	
	CommandHelpStyle<S> menuStyle();
	
	default ItemPageTextDisplayer<S, CommandSyntax<S>> syntaxDisplayer(@NotNull CommandManager<?, S> manager,
	                                                                   @NotNull Command<S> command,
	                                                                   @NotNull CommandHelpStyle<S> provider) {
		return new CommandSyntaxPageDisplayer<>(manager, command, provider);
	}
	
}
