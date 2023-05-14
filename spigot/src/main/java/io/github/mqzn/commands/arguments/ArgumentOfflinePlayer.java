package io.github.mqzn.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class ArgumentOfflinePlayer extends AbstractArgument<OfflinePlayer> {
	
	
	public ArgumentOfflinePlayer(@NotNull String id) {
		super(id, OfflinePlayer.class);
	}
	
	public ArgumentOfflinePlayer(@NotNull String id,
	                             boolean optional,
	                             boolean useRemainingSpace) {
		super(id, OfflinePlayer.class, optional, useRemainingSpace);
	}
	
	
	public ArgumentOfflinePlayer(@NotNull ArgumentData data) {
		super(data, OfflinePlayer.class);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public OfflinePlayer parse(@NotNull String command, @NotNull String input) {
		return Bukkit.getOfflinePlayer(input);
	}
	
	@Override
	public String toString(OfflinePlayer obj) {
		return obj.getName();
	}
}
