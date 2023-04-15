package io.github.mqzen.commands.exceptions;

import io.github.mqzen.commands.base.Command;

public abstract class CommandException extends Exception {

	protected final Command<?> command;

	public <S> CommandException(String message,
	                            Command<S> command) {
		super(message);
		this.command = command;
	}


}
