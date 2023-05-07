package io.github.mqzn.arguments;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.mqzn.commands.arguments.AbstractArgument;
import io.github.mqzn.commands.arguments.ArgumentData;
import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ArgumentOnlinePlayer extends AbstractArgument<Player> {

	private final ProxyServer proxyServer;

	public ArgumentOnlinePlayer(@NotNull ProxyServer server,
	                            @NotNull String id) {
		super(id, Player.class);
		this.proxyServer = server;
	}

	public ArgumentOnlinePlayer(@NotNull ProxyServer server, @NotNull ArgumentData data) {
		super(data, Player.class);
		this.proxyServer = server;
	}

	@Override
	public Player parse(@NotNull String command, @NotNull String input) throws ArgumentParseException {

		Optional<Player> player = proxyServer.getPlayer(input);
		if (player.isEmpty()) {
			throw new ArgumentParseException(String.format("Player %s is offline or doesn't exist", input), input, command);
		}
		return player.get();
	}

	@Override
	public @NotNull List<Player> suggestions() {
		return new ArrayList<>(proxyServer.getAllPlayers());
	}

	@Override
	public boolean isSuggestionDynamic() {
		return true;
	}

	@Override
	public String toString(Player obj) {
		return obj.getUsername();
	}

}
