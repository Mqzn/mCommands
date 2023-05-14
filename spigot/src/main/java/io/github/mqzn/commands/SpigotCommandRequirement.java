package io.github.mqzn.commands;

import io.github.mqzn.commands.base.CommandRequirement;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.context.Context;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface SpigotCommandRequirement extends CommandRequirement<CommandSender> {
	
	SpigotCommandRequirement ONLY_PLAYER_EXECUTABLE = new SpigotCommandRequirement() {
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
