package dev.mqzen.commands.spigot.requirements;

import dev.mqzen.commands.base.CommandContext;
import dev.mqzen.commands.base.CommandRequirement;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public enum SpigotCommandRequirement implements CommandRequirement<CommandSender> {

	ONLY_PLAYER_EXECUTABLE() {
		@Override
		public boolean accepts(CommandSender sender, CommandContext<CommandSender> commandContext) {
			return sender instanceof Player;
		}

		@Override
		public BiConsumer<CommandSender, CommandContext<CommandSender>> callBack() {
			return (sender, context)-> sender.sendMessage(ChatColor.RED + "Only players can do this !");
		}
	};


}
