package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.base.Command;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public final class ArgumentStringArray extends AbstractArgument<String[]> {

	ArgumentStringArray(String id) {
		super(id, String[].class, false, true);
	}

	@Override
	public String[] parse(@NotNull Command<?> command, @NotNull String input) {
		return input.split(Pattern.quote(""));
	}

	@Override
	public @NotNull List<String[]> suggestions() {
		return Collections.emptyList();
	}


}
