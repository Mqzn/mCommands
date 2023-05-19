package io.github.mqzn.commands.base.syntax;


public final class CommandAliases {
	
	private final String[] array;
	
	CommandAliases(String[] array) {
		this.array = array;
	}
	
	public static CommandAliases of(String... arr) {
		return new CommandAliases(arr);
	}
	
	public String[] getArray() {
		return array;
	}
}
