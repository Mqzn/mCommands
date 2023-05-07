package io.github.mqzn.commands.arguments;

import org.jetbrains.annotations.NotNull;

public final class ArgumentWord extends AbstractArgument<String> {

	ArgumentWord(String id) {
		super(id, String.class);
	}

	ArgumentWord(ArgumentData data) {
		super(data, String.class);
	}

	@Override
	public String parse(@NotNull String command, @NotNull String input) {
		return input;
	}

}
