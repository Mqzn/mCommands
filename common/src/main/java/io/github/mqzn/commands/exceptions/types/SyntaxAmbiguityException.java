package io.github.mqzn.commands.exceptions.types;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.exceptions.CommandException;

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
