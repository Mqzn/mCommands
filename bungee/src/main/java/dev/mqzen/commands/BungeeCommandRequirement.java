package dev.mqzen.commands;

import dev.mqzen.commands.base.CommandRequirement;
import dev.mqzen.commands.base.caption.CaptionKey;
import dev.mqzen.commands.base.context.Context;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.command.ConsoleCommandSender;

public enum BungeeCommandRequirement implements CommandRequirement<CommandSender> {
	ONLY_PLAYER_EXECUTABLE() {
		@Override
		public boolean accepts(CommandSender sender, Context<CommandSender> commandContext) {
			return sender instanceof ConsoleCommandSender;
		}

		@Override
		public CaptionKey caption() {
			return CaptionKey.ONLY_PLAYER_EXECUTABLE;
		}

	};


}
