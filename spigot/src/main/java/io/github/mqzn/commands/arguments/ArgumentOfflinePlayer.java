package io.github.mqzn.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Offline-player argument class
 */
public final class ArgumentOfflinePlayer extends AbstractArgument<OfflinePlayer> {
	
	/**
	 * Creating argument using the id
	 * @param id the name/id of the argument
	 */
	public ArgumentOfflinePlayer(@NotNull String id) {
		super(id, OfflinePlayer.class);
	}
	
	/**
	 * Creating argument using the data
	 * @param data the data of the arguments
	 */
	public ArgumentOfflinePlayer(@NotNull ArgumentData data) {
		super(data, OfflinePlayer.class);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public <S> OfflinePlayer parse(@UnknownNullability S sender,
	                               @NotNull String command, @NotNull String input) {
		return Bukkit.getOfflinePlayer(input);
	}
	
	@Override
	public String toString(OfflinePlayer obj) {
		return obj.getName();
	}
}
