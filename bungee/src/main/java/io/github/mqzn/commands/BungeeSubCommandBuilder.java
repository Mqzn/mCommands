package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.SubCommandBuilder;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class BungeeSubCommandBuilder<C> extends SubCommandBuilder<CommandSender, C> {
	private BungeeSubCommandBuilder(@NotNull Class<C> senderClass, @NotNull String label, @NotNull String name) {
		super(senderClass, label, name);
	}

	public static <C> BungeeSubCommandBuilder<C> builder(@NotNull Class<C> senderClass,
	                                                     @NotNull String label,
	                                                     @NotNull String name) {
		return new BungeeSubCommandBuilder<>(senderClass, label, name);
	}

	public static BungeeSubCommandBuilder<CommandSender> builder(@NotNull String label, @NotNull String name) {
		return builder(CommandSender.class, label, name);
	}
}
