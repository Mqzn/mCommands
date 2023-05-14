package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.SubCommandBuilder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class SpigotSubCommandBuilder<C> extends SubCommandBuilder<CommandSender, C> {
	private SpigotSubCommandBuilder(@NotNull Class<C> senderClass, @NotNull String label, @NotNull String name) {
		super(senderClass, label, name);
	}
	
	public static <C> SpigotSubCommandBuilder<C> builder(@NotNull Class<C> senderClass,
	                                                     @NotNull String label,
	                                                     @NotNull String name) {
		return new SpigotSubCommandBuilder<>(senderClass, label, name);
	}
	
	public static SpigotSubCommandBuilder<CommandSender> builder(@NotNull String label, @NotNull String name) {
		return builder(CommandSender.class, label, name);
	}
}