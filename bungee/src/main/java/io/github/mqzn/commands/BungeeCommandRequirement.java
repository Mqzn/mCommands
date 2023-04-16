package io.github.mqzn.commands;

import io.github.mqzn.commands.base.CommandRequirement;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.context.Context;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public enum BungeeCommandRequirement implements CommandRequirement<CommandSender> {
	ONLY_PLAYER_EXECUTABLE() {
		@Override
		public boolean accepts(CommandSender sender, Context<CommandSender> commandContext) {
			return !(sender instanceof ProxiedPlayer);
		}

		@Override
		public CaptionKey caption() {
			return CaptionKey.ONLY_PLAYER_EXECUTABLE;
		}

	};


}
