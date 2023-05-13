package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ArgumentOnlinePlayer extends AbstractArgument<ProxiedPlayer> {


	public ArgumentOnlinePlayer(@NotNull String id) {
		super(id, ProxiedPlayer.class);
	}

	public ArgumentOnlinePlayer(@NotNull String id, boolean optional, boolean useRemainingSpace) {
		super(id, ProxiedPlayer.class, optional, useRemainingSpace);
	}

	public ArgumentOnlinePlayer(String id, boolean useRemainingSpace) {
		super(id, ProxiedPlayer.class, useRemainingSpace);
	}

	public ArgumentOnlinePlayer(@NotNull ArgumentData data) {
		super(data, ProxiedPlayer.class);
	}

	@Override
	public ProxiedPlayer parse(@NotNull String command, @NotNull String input) throws ArgumentParseException {

		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(input);
		if (player == null || !player.isConnected()) {
			throw new ArgumentParseException(String.format("Player %s is offline or doesn't exist", input), input, command);
		}

		return player;
	}

	@Override
	public @NotNull List<ProxiedPlayer> suggestions() {
		return new ArrayList<>(ProxyServer.getInstance().getPlayers());
	}

	@Override
	public boolean isSuggestionDynamic() {
		return true;
	}

	@Override
	public String toString(ProxiedPlayer obj) {
		return obj.getName();
	}
}
