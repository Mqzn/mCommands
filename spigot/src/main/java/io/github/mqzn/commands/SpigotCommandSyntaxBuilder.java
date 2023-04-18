package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class SpigotCommandSyntaxBuilder<C> extends CommandSyntaxBuilder<CommandSender, C> {

	private SpigotCommandSyntaxBuilder(@NotNull Class<C> senderClass, @NotNull String label) {
		super(senderClass, label);
	}

	public static <C> SpigotCommandSyntaxBuilder<C> builder(@NotNull Class<C> senderClass,
	                                                        @NotNull String commandLabel) {
		return new SpigotCommandSyntaxBuilder<>(senderClass, commandLabel);
	}

	public static SpigotCommandSyntaxBuilder<CommandSender> builder(@NotNull String commandLabel) {
		return builder(CommandSender.class, commandLabel);
	}

}
