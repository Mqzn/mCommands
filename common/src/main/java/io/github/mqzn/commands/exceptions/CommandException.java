package io.github.mqzn.commands.exceptions;

public abstract class CommandException extends Exception {
	
	protected final String command;
	
	public CommandException(String message,
	                        String command) {
		super(message);
		this.command = command;
	}
	
	
}
