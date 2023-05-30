package io.github.mqzn.commands.arguments;


import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

/**
 * Online-player argument class
 */
public final class ArgumentOnlinePlayer extends AbstractArgument<Player> {
	
	
	/**
	 * Creating argument using the id
	 * @param id the name/id of the argument
	 */
	public ArgumentOnlinePlayer(
		@NotNull String id) {
		super(id, Player.class);
	}
	
	/**
	 * Creating argument using the data
	 * @param data the data of the arguments
	 */
	public ArgumentOnlinePlayer(@NotNull ArgumentData data) {
		super(data, Player.class);
	}
	
	@Override
	public <S> Player parse(@UnknownNullability S sender, @NotNull String command, @NotNull String input) throws ArgumentParseException {
		
		Player player = Bukkit.getPlayer(input);
		if (player == null || !player.isOnline()) {
			throw new ArgumentParseException(
				String.format("Player %s is offline or doesn't exist", input), input, command);
		}
		return player;
	}
	
	@Override
	public @NotNull List<Player> suggestions() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}
	
	@Override
	public boolean isSuggestionDynamic() {
		return true;
	}
	
	@Override
	public String toString(Player obj) {
		return obj.getName();
	}
	
}
