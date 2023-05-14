package io.github.mqzn.commands.exceptions.types;

import io.github.mqzn.commands.exceptions.CommandException;
import org.jetbrains.annotations.NotNull;

public final class ArgumentParseException extends CommandException {
	
	private final String input;
	
	public ArgumentParseException(@NotNull String message,
	                              @NotNull String input,
	                              @NotNull String command) {
		super(message, command);
		this.input = input;
	}
	
	@Override
	public Throwable fillInStackTrace() {
		// Stacktrace is useless to the parser
		return this;
	}
	
	/**
	 * Gets the problematic command input.
	 *
	 * @return the command input which triggered the exception
	 */
	@NotNull
	public String getInput() {
		return input;
	}
	
}
