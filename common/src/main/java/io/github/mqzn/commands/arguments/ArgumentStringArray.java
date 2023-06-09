package io.github.mqzn.commands.arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Pattern;

public final class ArgumentStringArray extends AbstractArgument<String[]> {
	
	ArgumentStringArray(String id) {
		super(id, String[].class, false, true);
	}
	
	@Override
	public <S> String[] parse(@UnknownNullability S sender,
	                          @NotNull String command,
	                          @NotNull String input) {
		return input.split(Pattern.quote(" "));
	}
	
	@Override
	public String toString(String[] obj) {
		return String.join(" ", obj);
	}
}
