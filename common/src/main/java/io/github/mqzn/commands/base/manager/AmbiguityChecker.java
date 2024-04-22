package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.arguments.ArgumentLiteral;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.syntax.CommandSyntax;

import java.util.ArrayList;
import java.util.List;

public class AmbiguityChecker<S> {
	private final Command<S> command;
	private final List<CommandSyntax<S>> syntaxes;
	
	private AmbiguityChecker(Command<S> command, List<CommandSyntax<S>> syntaxes) {
		this.command = command;
		this.syntaxes = syntaxes;
	}
	
	public static <S> AmbiguityChecker<S> of(Command<S> command) {
		return new AmbiguityChecker<>(command, command.syntaxes());
	}
	
	public static <S> boolean hasLiteralArgs(CommandSyntax<S> syntax) {
		if (syntax.isSubCommand()) return true;
		for (Object arg : syntax.getArguments()) {
			if (arg instanceof ArgumentLiteral) {
				return true;
			}
		}
		return false;
	}
	
	public List<CommandSyntax<S>> findAmbiguity() {
		for (CommandSyntax<S> syntax : syntaxes) {
			if (syntax.useSpace() && !hasLiteralArgs(syntax) && syntaxes.size() > 1) {
				return syntaxes;
			}
		}
		
		List<CommandSyntax<S>> ambiguous = new ArrayList<>();
		for (int first = 0; first < syntaxes.size(); first++) {
			CommandSyntax<S> firstSyntax = syntaxes.get(first);
			for (int second = 0; second < syntaxes.size(); second++) {
				if (first == second) continue;
				CommandSyntax<S> secondSyntax = syntaxes.get(second);
				if (areAmbiguous(firstSyntax, secondSyntax)) {
					ambiguous.add(firstSyntax);
					ambiguous.add(secondSyntax);
				}
			}
		}
		return ambiguous;
	}
	
	private boolean areAmbiguous(CommandSyntax<S> s1, CommandSyntax<S> s2) {
		int s1Length = CommandSyntax.getArguments(command.tree(), s1).size();
		int s2Length = CommandSyntax.getArguments(command.tree(), s2).size();
		
		return (!hasLiteralArgs(s1) && !hasLiteralArgs(s2) && s1Length == s2Length) ||
			(s1Length == s2Length && s1.equals(s2));
	}
}

