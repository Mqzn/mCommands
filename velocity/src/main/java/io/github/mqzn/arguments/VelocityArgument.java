package io.github.mqzn.arguments;

import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

public interface VelocityArgument {

	static ArgumentOnlinePlayer onlinePlayer(@NotNull ProxyServer server, String id) {
		return new ArgumentOnlinePlayer(server, id);
	}

}
