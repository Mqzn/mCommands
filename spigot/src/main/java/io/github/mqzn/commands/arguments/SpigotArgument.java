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
	 * @see OfflinePlayer
	 *
	 * @param id the argument's name/id
	 * @return the new offline player argument instance
	 */
	static ArgumentOfflinePlayer offlinePlayer(String id) {
		return new ArgumentOfflinePlayer(id);
	}
	
	/**
	 * creates a new online player
	 * argument in the syntax
	 *
	 * @see Player
	 *
	 * @param id the argument's name/id
	 * @return the new online player argument instance
	 */
	static ArgumentOnlinePlayer onlinePlayer(String id) {
		return new ArgumentOnlinePlayer(id);
	}
	
	/**
	 * creates a new UUID
	 * argument in the syntax
	 *
	 * @see UUID
	 *
	 * @param id the argument's name/id
	 * @return the new UUID argument instance
	 */
	static ArgumentUUID uuid(String id) {
		return new ArgumentUUID(id);
	}
	
	/**
	 * creates a new `World`
	 * argument in the syntax
	 *
	 * @see World
	 *
	 * @param id the argument's name/id
	 * @return the new world argument instance
	 */
	static ArgumentWorld world(String id) {
		return new ArgumentWorld(id);
	}
	
}
