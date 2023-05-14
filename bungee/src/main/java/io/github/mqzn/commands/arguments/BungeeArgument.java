package io.github.mqzn.commands.arguments;

public interface BungeeArgument {
	
	static ArgumentOnlinePlayer onlinePlayer(String id) {
		return new ArgumentOnlinePlayer(id);
	}
	
}
