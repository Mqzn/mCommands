package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.arguments.ArgumentNumber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
		
		@Nullable N end = argumentNumber.getMax();
		@Nullable N start = argumentNumber.getMin();
		
		if (start == null && end == null) {
			return;
		} else if (end == null) {
			argumentNumber.suggest(start);
			return;
		} else if (start == null) {
			argumentNumber.suggest(end);
			return;
		}
		
		while (comparator.lessThanOrEqual(start, end)) {
			argumentNumber.suggest(start);
			start = argumentNumber.increment(start);
		}
		
	}
	
	
}
