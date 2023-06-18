package io.github.mqzn.commands.arguments;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Class for static access to the well known-arguments of spigot
 */
public interface SpigotArgument {
	
	/**
	 * creates a new offline player
	 * argument in the syntax
	 *
	 * @param id the argument's name/id
	 * @return the new offline player argument instance
	 * @see OfflinePlayer
	 */
	static ArgumentOfflinePlayer offlinePlayer(String id) {
		return new ArgumentOfflinePlayer(id);
	}
	
	/**
	 * creates a new online player
	 * argument in the syntax
	 *
	 * @param id the argument's name/id
	 * @return the new online player argument instance
	 * @see Player
	 */
	static ArgumentOnlinePlayer onlinePlayer(String id) {
		return new ArgumentOnlinePlayer(id);
	}
	
	/**
	 * creates a new UUID
	 * argument in the syntax
	 *
	 * @param id the argument's name/id
	 * @return the new UUID argument instance
	 * @see UUID
	 */
	static ArgumentUUID uuid(String id) {
		return new ArgumentUUID(id);
	}
	
	/**
	 * creates a new `World`
	 * argument in the syntax
	 *
	 * @param id the argument's name/id
	 * @return the new world argument instance
	 * @see World
	 */
	static ArgumentWorld world(String id) {
		return new ArgumentWorld(id);
	}
	
}
