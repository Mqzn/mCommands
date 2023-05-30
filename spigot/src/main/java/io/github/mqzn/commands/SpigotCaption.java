package io.github.mqzn.commands;

import io.github.mqzn.commands.base.caption.Caption;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.caption.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

/**
 * Interface whose only purpose
 * is to contain most common captions for spigot platform
 * as constants
 */
interface SpigotCaption {
	
	/**
	 * Sent when an unknown syntax is used
	 */
	Caption<CommandSender> UNKNOWN_COMMAND = Caption.<CommandSender>builder(CaptionKey.UNKNOWN_COMMAND)
		.withMessage((sender, context, ex) -> (TextComponent) Message.prefixed(Message.EXECUTION_ERROR)
			.append(Component.text(String.format("Unknown Command In Syntax '%s'", context.rawFormat()), NamedTextColor.YELLOW))
			.appendNewline()
			.append(Component.text("Try `/" + context.commandUsed().name() + " help &f`")))
		.build();
	
	/**
	 * Sent when the user has no permission
	 */
	Caption<CommandSender> NO_PERMISSION = Caption.<CommandSender>builder(CaptionKey.NO_PERMISSION)
		.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR)
			.append(Component.text("You don't have the permission to do this !", NamedTextColor.GRAY)))
		.build();
	
	/**
	 * Sent when the console is trying to execute a command
	 * which is only executable by a player
	 */
	Caption<CommandSender> ONLY_PLAYER_EXECUTABLE = Caption.<CommandSender>builder(CaptionKey.ONLY_PLAYER_EXECUTABLE)
		.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR).append(Component.text("Only a player can execute this !", NamedTextColor.RED)))
		.build();
	
	/**
	 * Sent when Invalid argument type used in the syntax
	 */
	Caption<CommandSender> INVALID_ARGUMENT = Caption.<CommandSender>builder(CaptionKey.INVALID_ARGUMENT)
		.withMessage((sender, context, ex) -> {
			String msg = ex == null ? "Invalid argument used" : ex.getMessage();
			return Message.prefixed(Message.INVALID_ARGUMENT_ERROR).append(Component.text(msg, NamedTextColor.YELLOW));
		})
		.build();
	
	/**
	 * Sent when trying to access a help topic of a command which doesn't exist
	 */
	Caption<CommandSender> NO_HELP_TOPIC_AVAILABLE = Caption.<CommandSender>builder(CaptionKey.NO_HELP_TOPIC_AVAILABLE)
		.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR).append(Component.text("There's no help topic for this command '/" + context.commandUsed().name() + "'", NamedTextColor.RED)))
		.build();
	
	
}
