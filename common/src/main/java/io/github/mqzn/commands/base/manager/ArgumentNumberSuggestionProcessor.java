package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.arguments.ArgumentNumber;
import org.jetbrains.annotations.NotNull;

public final class ArgumentNumberSuggestionProcessor {
	
	@NotNull
	private final CommandManager<?, ?> manager;
	
	private ArgumentNumberSuggestionProcessor(@NotNull CommandManager<?, ?> manager) {
		this.manager = manager;
	}
	
	public static ArgumentNumberSuggestionProcessor create(@NotNull CommandManager<?, ?> manager) {
		return new ArgumentNumberSuggestionProcessor(manager);
	}
	
	@SuppressWarnings("unchecked")
	public <N extends Number> void provide(@NotNull ArgumentNumber<N> argumentNumber) {
		
		ArgumentNumberComparator.ArgumentComparator<N> comparator
			= manager.typeRegistry().getComparator((Class<? extends ArgumentNumber<N>>) argumentNumber.getClass());
		
		N end = argumentNumber.getMax();
		N start = argumentNumber.getMin();
		
		while (comparator.lessThanOrEqual(start, end)) {
			argumentNumber.suggest(start);
			start = argumentNumber.increment(start);
		}
		
	}
	
	
}
