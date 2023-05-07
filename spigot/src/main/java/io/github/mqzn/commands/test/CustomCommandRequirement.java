package io.github.mqzn.commands.test;

import io.github.mqzn.commands.SpigotCommandRequirement;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.context.Context;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class CustomCommandRequirement implements SpigotCommandRequirement {

	@Override
	public boolean accepts(CommandSender sender, Context<CommandSender> commandContext) {
		return sender.getName().equalsIgnoreCase("Mqzen"); // your condition;
	}

	/**
	 * @return the key of the caption message to send if the condition/requirement is not true !
	 * setting this to null will cause no message to be sent !
	 */
	@Override
	public @Nullable CaptionKey caption() {
		return null;
	}

}
