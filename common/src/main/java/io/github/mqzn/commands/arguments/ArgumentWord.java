package io.github.mqzn.commands.arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class ArgumentWord extends AbstractArgument<String> {
	
	ArgumentWord(String id) {
		super(id, String.class);
	}
	
	ArgumentWord(ArgumentData data) {
		super(data, String.class);
	}
	
	@Override
	public <S> String parse(@UnknownNullability S sender,
	                        @NotNull String command,
	                        @NotNull String input) {
		return input;
	}
	
}
