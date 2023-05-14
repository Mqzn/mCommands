package io.github.mqzn.commands;

import io.github.mqzn.commands.base.caption.Caption;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.caption.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.CommandSender;

public interface BungeeCaption {
	
	Caption<CommandSender> UNKNOWN_COMMAND = Caption.<CommandSender>builder(CaptionKey.UNKNOWN_COMMAND)
		.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR)
			.append(Component.text(String.format("Unknown Command In Syntax '%s'", context.rawFormat()))))
		.build();
	
	Caption<CommandSender> NO_PERMISSION = Caption.<CommandSender>builder(CaptionKey.NO_PERMISSION)
		.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR)
			.append(Component.text("You don't have the permission to do this !", NamedTextColor.GRAY)))
		.build();
	
	
	Caption<CommandSender> ONLY_PLAYER_EXECUTABLE = Caption.<CommandSender>builder(CaptionKey.ONLY_PLAYER_EXECUTABLE)
		.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR).append(Component.text("Only a player can execute this !", NamedTextColor.RED)))
		.build();
	
	Caption<CommandSender> INVALID_ARGUMENT = Caption.<CommandSender>builder(CaptionKey.INVALID_ARGUMENT)
		.withMessage((sender, context, ex) -> {
			String msg = ex == null ? "Invalid argument used" : ex.getMessage();
			return Message.prefixed(Message.INVALID_ARGUMENT_ERROR).append(Component.text(msg, NamedTextColor.DARK_GRAY));
		})
		.build();
	
	Caption<CommandSender> NO_HELP_TOPIC_AVAILABLE = Caption.<CommandSender>builder(CaptionKey.NO_HELP_TOPIC_AVAILABLE)
		.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR).append(Component.text("There's no help topic for this command '/" + context.commandUsed().name() + "'", NamedTextColor.RED)))
		.build();
}
