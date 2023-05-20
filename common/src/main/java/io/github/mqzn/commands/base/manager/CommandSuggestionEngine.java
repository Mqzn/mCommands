package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.arguments.ArgumentLiteral;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CommandSuggestionEngine<S> {
	
	@NotNull
	private final Set<SyntaxSuggestionContainer> suggestionContainers = new HashSet<>();
	
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
		
		Argument<T> argument = (Argument<T>) arg;
		
		return new ArrayList<>(argument.suggestions().stream()
			.map(argument::toString)
			.toList());
	}
	
	private void initialize() {
		
		for (CommandSyntax<S> syntax : command.syntaxes())
			suggestionContainers.add(new SyntaxSuggestionContainer(syntax));
		
	}
	
	@NotNull Set<@NotNull SyntaxSuggestionContainer> getSuggestions(@NotNull String[] args) {
		
		return suggestionContainers.stream()
			.filter((container) -> container.argSequenceMatches(args))
			.collect(Collectors.toSet());
	}
	
	final class SyntaxSuggestionContainer {
		
		private final CommandSyntax<S> syntax;
		
		@NotNull
		private final Map<Integer, List<String>> suggestions = new HashMap<>();
		
		@NotNull
		private final Set<Integer> dynamicArgs = new HashSet<>();
		
		@NotNull
		private final SyntaxSuggestionContainerKey provider;
		
		SyntaxSuggestionContainer(@NotNull CommandSyntax<S> syntax) {
			this(syntax, new SyntaxSuggestionContainerKey(syntax));
		}
		
		SyntaxSuggestionContainer(@NotNull CommandSyntax<S> syntax,
		                          @NotNull SyntaxSuggestionContainerKey provider) {
			this.syntax = syntax;
			this.provider = provider;
			this.fetchArgumentSuggestions();
		}
		
		
		private void fetchArgumentSuggestions() {
			
			List<Argument<?>> arguments = (syntax instanceof SubCommandSyntax<S> sub) ?
				command.tree().getParentalArguments(sub.getName())
				: syntax.getArguments();
			
			for (int arg = 0; arg < arguments.size(); arg++) {
				
				Argument<?> argument = arguments.get(arg);
				
				if (argument == null) break;
				
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
	
	
	final class SyntaxSuggestionContainerKey {
		
		private final Map<Integer, Predicate<String[]>> rawPredicates;
		
		private SyntaxSuggestionContainerKey(CommandSyntax<S> syntax) {
			this.rawPredicates = new HashMap<>();
			this.init(syntax);
		}
		
		
		
		/**
		 * Creates a condition to accept a specific tab completion of a specific syntax
		 */
		private void init(CommandSyntax<S> syntax) {
			
			List<Argument<?>> arguments = (syntax instanceof SubCommandSyntax<S> sub) ? command.tree().getParentalArguments(sub.getName()) : syntax.getArguments();
			
			for (int index = 0; index < arguments.size(); index++) {
				
				Argument<?> argument = arguments.get(index);
				if (!(argument instanceof ArgumentLiteral literal)) continue;
				
				int finalIndex = index;
				rawPredicates.put(index, (args) -> {
					String raw = args[finalIndex];
					if (raw == null || raw.isBlank() || raw.isEmpty()) return true;
					return raw.equalsIgnoreCase(argument.id()) || CommandSyntax.aliasesIncludes(literal.getAliases(), raw);
				});
				
			}
			
			
		}
		
		
		public boolean matches(@NotNull String[] args) {
			
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