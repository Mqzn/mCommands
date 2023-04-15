package dev.mqzen.commands;

import dev.mqzen.commands.base.CommandRequirement;
import dev.mqzen.commands.base.caption.CaptionKey;
import dev.mqzen.commands.base.context.Context;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum SpigotCommandRequirement implements CommandRequirement<CommandSender> {

	ONLY_PLAYER_EXECUTABLE() {
		@Override
		public boolean accepts(CommandSender sender, Context<CommandSender> commandContext) {
			return sender instanceof Player;
		}

		@Override
		public CaptionKey caption() {
			return CaptionKey.ONLY_PLAYER_EXECUTABLE;
		}
	};


}
