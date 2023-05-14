package io.github.mqzn.commands.base;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SuggestionProvider {
	
	@NotNull List<String> suggestions();
	
}
