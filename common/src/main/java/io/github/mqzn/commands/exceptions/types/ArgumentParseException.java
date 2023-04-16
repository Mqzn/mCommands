package io.github.mqzn.commands.exceptions.types;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.exceptions.CommandException;
import org.jetbrains.annotations.NotNull;

public final class ArgumentParseException extends CommandException {

	private final String input;
	private final int errorCode;

	public ArgumentParseException(@NotNull String message,
	                              @NotNull String input,
	                              int errorCode,
	                              @NotNull Command<?> command) {
		super(message, command);
		this.input = input;
		this.errorCode = errorCode;
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

	/**
	 * Gets the error code of the exception.
	 * <p>
	 * The code is decided arbitrary by the argument,
	 * check the argument class to know the meaning of each one.
	 *
	 * @return the argument error code
	 */
	public int getErrorCode() {
		return errorCode;
	}
}
