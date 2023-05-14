package io.github.mqzn.commands.arguments;

public interface SpigotArgument {
	
	
	static ArgumentOfflinePlayer offlinePlayer(String id) {
		return new ArgumentOfflinePlayer(id);
	}
	
	static ArgumentOnlinePlayer onlinePlayer(String id) {
		return new ArgumentOnlinePlayer(id);
	}
	
}
