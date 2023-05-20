package io.github.mqzn.commands.exceptions.types;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.exceptions.CommandException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public final class SyntaxAmbiguityException extends CommandException {
	
	public <S> SyntaxAmbiguityException(@NotNull CommandManager<?, S> manager,
	                                    Command<S> command,
	                                    Collection<CommandSyntax<S>> syntaxes) {
		
		super("Similar syntaxes detected (duplicate execution logic) : " + String.join("\n",
			syntaxes.stream().map((syntax) -> CommandSyntax.format(manager, command.name(), CommandSyntax.getArguments(command.tree(), syntax)))
				.collect(Collectors.toSet())), command.name());
		
	}
	
}
