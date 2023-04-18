package io.github.mqzn.commands.base.cooldown;

import io.github.mqzn.commands.base.caption.Caption;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.caption.Message;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.exceptions.CommandException;
import io.github.mqzn.commands.utilities.Pair;
import io.github.mqzn.commands.utilities.TimeParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class CooldownCaption<S> implements Caption<S> {


	@NotNull
	private final Pair<Long, TimeUnit> timeData;

	public CooldownCaption(TimeParser parser) {
		this.timeData = parser.highestLogicalUnitValue();
	}

	private static String formatUnit(TimeUnit unit) {
		String unitName = unit.name().toLowerCase();
		unitName = unitName.substring(0, unitName.length() - 1);
		return unitName + "(s)";
	}

	@Override
	public @NotNull CaptionKey key() {
		return CaptionKey.COMMAND_IN_COOLDOWN;
	}

	@Override
	public @NotNull <E extends CommandException> TextComponent message(S sender, Context<S> context, E exception) {

		return Message.prefixed(Message.EXECUTION_ERROR)
						.append(Component.text(String.format("Command '" + context.commandUsed().name()
										+ "' is in cooldown for %s %s", timeData.getLeft(), formatUnit(timeData.getRight())), NamedTextColor.YELLOW));
	}

}
