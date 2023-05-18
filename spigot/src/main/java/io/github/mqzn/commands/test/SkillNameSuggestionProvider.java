package io.github.mqzn.commands.test;

import io.github.mqzn.commands.base.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class SkillNameSuggestionProvider implements SuggestionProvider {
	/**
	 * The suggestions to be used for the argument
	 *
	 * @return the suggestions
	 */
	@Override
	public @NotNull List<String> suggestions() {
		return List.of("all", "herbalism", "heatwaves", "peniswaves");
	}
	
}
