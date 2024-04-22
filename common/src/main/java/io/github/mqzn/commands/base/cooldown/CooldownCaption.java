package io.github.mqzn.commands.base.cooldown;

import io.github.mqzn.commands.base.caption.Caption;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.caption.Message;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.utilities.TimeParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class CooldownCaption<S> implements Caption<S> {
	
	public static String formatUnit(TimeUnit unit) {
		var unitName = unit.name().toLowerCase(Locale.getDefault());
		unitName = unitName.substring(0, unitName.length() - 1);
		return unitName + "(s)";
	}
	
	public static long calculateRemainingTime(long lastTime, CommandCooldown commandCooldown) {
		var diff = System.currentTimeMillis() - lastTime;
		return commandCooldown.toMillis() - diff;
	}
	
	@Override
	public @NotNull CaptionKey key() {
		return CaptionKey.COMMAND_IN_COOLDOWN;
	}
	
	@Override
	public @NotNull TextComponent message(S sender, Context<S> context, Throwable exception) {
		var command = context.commandUsed();
		var manager = command.manager();
		var cooldown = command.cooldown();
		var lastTimeCommandExecuted = manager.getCommandCooldown(manager.getSenderWrapper().senderName(sender));
		if (lastTimeCommandExecuted == null) lastTimeCommandExecuted = 0L;
		// Send a caption telling the user that he's in a cooldown
		// Calculating remaining time
		var parser = TimeParser.parse(calculateRemainingTime(lastTimeCommandExecuted, cooldown));
		var timeData = parser.highestLogicalUnitValue();
		
		return cooldownMessage(timeData.getLeft(), timeData.getRight(), context);
	}
	
	public TextComponent cooldownMessage(long time, TimeUnit unit, Context<S> context) {
		return Message.prefixed(Message.EXECUTION_ERROR)
			.append(
				Component.text(
					String.format(
						"Command '" + context.commandUsed().name()
							+ "' is in cooldown for %d %s", time, formatUnit(unit)
					), NamedTextColor.YELLOW
				)
			);
	}
}


