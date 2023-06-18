package io.github.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import io.github.mqzn.commands.base.CommandRequirement;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.context.Context;

public interface VelocityCommandRequirement extends CommandRequirement<CommandSource> {
	
	VelocityCommandRequirement ONLY_PLAYER_EXECUTABLE = new VelocityCommandRequirement() {
		@Override
		public boolean accepts(CommandSource sender, Context<CommandSource> commandContext) {
			return sender instanceof Player;
		}
		
		@Override
		public CaptionKey caption() {
			return CaptionKey.ONLY_PLAYER_EXECUTABLE;
		}
	};
	
	
}
