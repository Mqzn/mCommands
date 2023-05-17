package io.github.mqzn.commands.base;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An interface that represents
 * a function to provide suggestions for
 * an argument for annotation parsing purposes
 *
 * @author Mqzen
 */
public interface SuggestionProvider {
	
	/**
	 * The suggestions to be used for the argument
	 *
	 * @return the suggestions
	 */
	@NotNull List<String> suggestions();
	
}
