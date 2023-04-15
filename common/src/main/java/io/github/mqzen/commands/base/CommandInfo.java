package io.github.mqzen.commands.base;

public record CommandInfo(String permission,
                          String description,
                          String... aliases) {


	public static final CommandInfo EMPTY_INFO = new CommandInfo(null, "");

}
