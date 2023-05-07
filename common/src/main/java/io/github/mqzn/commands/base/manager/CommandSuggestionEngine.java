package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.arguments.ArgumentLiteral;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CommandSuggestionEngine<S> {

	@NotNull
	private final Set<SyntaxSuggestionContainer<S>> suggestionContainers = new HashSet<>();

	@NotNull
	private final Command<S> command;

	private CommandSuggestionEngine(@NotNull Command<S> command) {
		this.command = command;
		this.initialize();
	}

	public static <S> CommandSuggestionEngine<S> create(@NotNull Command<S> command) {
		return new CommandSuggestionEngine<>(command);
	}

	@SuppressWarnings("unchecked")
	private static @NotNull <T> List<String> collectArgumentSuggestions(Argument<?> arg) {
		ArrayList<String> argSuggestions = new ArrayList<>();

		if (arg instanceof ArgumentLiteral) {
			argSuggestions.add(arg.id());
			return argSuggestions;
		}

		Argument<T> argument = (Argument<T>) arg;

		argSuggestions.addAll(argument.suggestions().stream()
						.map(argument::toString)
						.toList());

		return argSuggestions;
	}

	private void initialize() {

		for (CommandSyntax<S> syntax : command.syntaxes())
			suggestionContainers.add(new SyntaxSuggestionContainer<>(syntax));

	}

	@NotNull Set<@NotNull SyntaxSuggestionContainer<S>> getSuggestions(@NotNull String[] args) {

		return suggestionContainers.stream()
						.filter((container) -> container.argSequenceMatches(args))
						.collect(Collectors.toSet());
	}

	static final class SyntaxSuggestionContainer<S> {

		@Getter
		private final CommandSyntax<S> syntax;

		@NotNull
		private final Map<Integer, List<String>> suggestions = new HashMap<>();

		@NotNull
		private final Set<Integer> dynamicArgs = new HashSet<>();

		@NotNull
		private final SyntaxSuggestionContainerKey<S> provider;

		SyntaxSuggestionContainer(@NotNull CommandSyntax<S> syntax) {
			this(syntax, SyntaxSuggestionContainerKey.from(syntax));
		}

		SyntaxSuggestionContainer(@NotNull CommandSyntax<S> syntax,
		                          @NotNull CommandSuggestionEngine.SyntaxSuggestionContainerKey<S> provider) {
			this.syntax = syntax;
			this.provider = provider;
			this.fetchArgumentSuggestions();
		}


		private void fetchArgumentSuggestions() {

			for (int arg = 0; arg < syntax.length(); arg++) {
				Argument<?> argument = syntax.getArgument(arg);
				if(argument == null) break;

				if (argument.isSuggestionDynamic()) {
					dynamicArgs.add(arg);
				}
				suggestions.put(arg, collectArgumentSuggestions(argument));
			}

		}

		private boolean isArgumentDynamic(int index) {
			return dynamicArgs.contains(index);
		}


		@Nullable
		@SuppressWarnings("unchecked")
		public <T> List<String> getArgumentSuggestions(int argIndex) {
			if (isArgumentDynamic(argIndex)) {
				Argument<T> argument = (Argument<T>) syntax.getArgument(argIndex);
				assert argument != null;
				return argument.suggestions().stream().map(argument::toString)
								.collect(Collectors.toList());
			}

			return suggestions.get(argIndex);
		}


		public boolean argSequenceMatches(@NotNull String[] args) {
			return provider.matches(args);
		}


	}

	@EqualsAndHashCode
	static final class SyntaxSuggestionContainerKey<S> {

		private final Map<Integer, Predicate<String[]>> rawPredicates;

		private SyntaxSuggestionContainerKey(CommandSyntax<S> syntax) {
			this.rawPredicates = new HashMap<>();
			this.init(syntax);
		}

		public static <S> SyntaxSuggestionContainerKey<S> from(@NotNull CommandSyntax<S> syntax) {
			return new SyntaxSuggestionContainerKey<>(syntax);
		}


		/**
		 * Creates a condition to accept a specific tab completion of a specific syntax
		 */
		private void init(CommandSyntax<S> syntax) {

			for (int index = 0; index < syntax.length(); index++) {

				Argument<?> argument = syntax.getArgument(index);
				if (!(argument instanceof ArgumentLiteral)) continue;

				int finalIndex = index;
				rawPredicates.put(index, (args) -> {
					String raw = args[finalIndex];
					if (raw == null || raw.isBlank() || raw.isEmpty()) return true;
					return raw.equalsIgnoreCase(argument.id());
				});

			}


		}


		public boolean matches(String[] args) {

			if (args.length <= 1) return true;

			for (int i = 0; i < args.length; i++) {

				var predicate = rawPredicates.get(i);
				if (predicate == null) continue;

				if (!predicate.test(args)) {
					return false;
				}

			}

			return true;
		}


	}


}
