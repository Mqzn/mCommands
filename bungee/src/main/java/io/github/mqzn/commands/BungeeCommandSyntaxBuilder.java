package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class BungeeCommandSyntaxBuilder<C> extends CommandSyntaxBuilder<CommandSender, C> {

	private BungeeCommandSyntaxBuilder(@NotNull Class<C> senderClass, @NotNull String label) {
		super(senderClass, label);
	}

	public static <C> BungeeCommandSyntaxBuilder<C> builder(@NotNull Class<C> senderClass, @NotNull String label) {
		return new BungeeCommandSyntaxBuilder<>(senderClass, label);
	}

	public static BungeeCommandSyntaxBuilder<CommandSender> builder(@NotNull String label) {
		return builder(CommandSender.class, label);
	}

}
