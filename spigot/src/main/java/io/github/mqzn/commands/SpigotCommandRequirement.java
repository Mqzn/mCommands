package io.github.mqzn.commands;

import io.github.mqzn.commands.base.CommandRequirement;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.context.Context;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A pre-requirement container made for the users to use the constants inside it
 * such as "ONLY_PLAYER_EXECUTABLE"
 */
public interface SpigotCommandRequirement extends CommandRequirement<CommandSender> {
	
	/**
	 * The requirement of making the command executable only by players
	 */
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
