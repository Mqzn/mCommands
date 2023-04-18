package io.github.mqzn.commands.exceptions;

public class UnknownCommandSenderType extends RuntimeException {

	public UnknownCommandSenderType(Class<?> senderClass) {
		super("Unknown sender type : " + senderClass.getName());
	}

}
