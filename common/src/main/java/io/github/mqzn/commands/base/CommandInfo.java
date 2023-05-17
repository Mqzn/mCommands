package io.github.mqzn.commands.base;

/**
 * A record that holds some information to it
 * such as the permission of the command, it's description
 * and it's aliases !
 *
 * @param permission  the permission of the command
 * @param description the description explaining the purpose of the command
 * @param aliases     the aliases of the command
 */
public record CommandInfo(String permission,
                          String description,
                          String... aliases) {
	
	
	public static final CommandInfo EMPTY_INFO = new CommandInfo(null, "");
	
}
