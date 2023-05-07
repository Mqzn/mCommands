package io.github.mqzn;

import com.velocitypowered.api.command.CommandSource;
import io.github.mqzn.commands.base.caption.Caption;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.caption.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface VelocityCaption {


	Caption<CommandSource> UNKNOWN_COMMAND = Caption.<CommandSource>builder(CaptionKey.UNKNOWN_COMMAND)
					.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR)
									.append(Component.text(String.format("Unknown Command In Syntax '%s'", context.rawFormat()))))
					.build();

	Caption<CommandSource> NO_PERMISSION = Caption.<CommandSource>builder(CaptionKey.NO_PERMISSION)
					.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR)
									.append(Component.text("You don't have the permission to do this !", NamedTextColor.GRAY)))
					.build();


	Caption<CommandSource> ONLY_PLAYER_EXECUTABLE = Caption.<CommandSource>builder(CaptionKey.ONLY_PLAYER_EXECUTABLE)
					.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR).append(Component.text("Only a player can execute this !", NamedTextColor.RED)))
					.build();


	Caption<CommandSource> INVALID_ARGUMENT = Caption.<CommandSource>builder(CaptionKey.INVALID_ARGUMENT)
					.withMessage((sender, context, ex) -> {
						String msg = ex == null ? "Invalid argument used" : ex.getMessage();
						return Message.prefixed(Message.INVALID_ARGUMENT_ERROR).append(Component.text(msg, NamedTextColor.YELLOW));
					})
					.build();


	Caption<CommandSource> NO_HELP_TOPIC_AVAILABLE = Caption.<CommandSource>builder(CaptionKey.NO_HELP_TOPIC_AVAILABLE)
					.withMessage((sender, context, ex) -> Message.prefixed(Message.EXECUTION_ERROR).append(Component.text("There's no help topic for this command '/" + context.commandUsed().name() + "'", NamedTextColor.RED)))
					.build();

}
