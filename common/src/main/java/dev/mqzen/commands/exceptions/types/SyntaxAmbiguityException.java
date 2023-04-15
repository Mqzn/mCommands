package dev.mqzen.commands.exceptions.types;

import dev.mqzen.commands.base.Command;
import dev.mqzen.commands.base.syntax.CommandSyntax;
import dev.mqzen.commands.exceptions.CommandException;

import java.util.Collection;
import java.util.stream.Collectors;

public final class SyntaxAmbiguityException extends CommandException {

	public <S> SyntaxAmbiguityException(Command<S> command,
	                                    Collection<CommandSyntax<S>> syntaxes) {

		super("Similar syntaxes detected (duplicate execution logic) : " + String.join("\n",
						syntaxes.stream().map(CommandSyntax::formatted)
										.collect(Collectors.toSet())), command);

	}

}
