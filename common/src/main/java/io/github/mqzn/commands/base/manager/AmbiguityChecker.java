package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.arguments.ArgumentLiteral;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class AmbiguityChecker<S> {

	@NotNull
	private final List<CommandSyntax<S>> syntaxes;

	private AmbiguityChecker(@NotNull List<CommandSyntax<S>> syntaxes) {
		this.syntaxes = syntaxes;
	}

	public static <S> AmbiguityChecker<S> of(@NotNull List<CommandSyntax<S>> syntaxes) {
		return new AmbiguityChecker<>(syntaxes);
	}

	public static <S> boolean hasLiteralArgs(@NotNull CommandSyntax<S> syntax) {

		if (syntax.isSubCommand()) return true;

		for (var arg : syntax.getArguments()) {
			if (arg instanceof ArgumentLiteral) {
				return true;
			}

		}

		return false;
	}

	public synchronized @NotNull List<CommandSyntax<S>> findAmbiguity() {

		for (CommandSyntax<S> syntax : syntaxes) {
			if (syntax.useSpace() && !hasLiteralArgs(syntax) && syntaxes.size() > 1) {
				return syntaxes;
			}
		}

		List<CommandSyntax<S>> ambigious = new ArrayList<>();

		for (int first = 0; first < syntaxes.size(); first++) {
			var firstSyntax = syntaxes.get(first);

			for (int second = 0; second < syntaxes.size(); second++) {

				if (first == second) continue;

				var secondSyntax = syntaxes.get(second);
				boolean areAmbigious = areAmbigious(firstSyntax, secondSyntax);

				if (areAmbigious) {
					ambigious.add(firstSyntax);
					ambigious.add(secondSyntax);
				}

			}

		}

		return ambigious;
	}

	private boolean areAmbigious(@NotNull CommandSyntax<S> s1, @NotNull CommandSyntax<S> s2) {

		// first condition is that 2 or more syntaxes without literal args and same length
		// second condition is just a duplicate syntax

		boolean firstCondition = (!hasLiteralArgs(s1) && !hasLiteralArgs(s2) && s1.length() == s2.length());
		boolean secondCondition = s1.equals(s2);

		return firstCondition || secondCondition;
	}


}
