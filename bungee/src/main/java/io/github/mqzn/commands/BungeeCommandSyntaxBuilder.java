package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class BungeeCommandSyntaxBuilder<C> extends CommandSyntaxBuilder<CommandSender, C> {
	
	private BungeeCommandSyntaxBuilder(BungeeCommandManager commandManager,
	                                   @NotNull Class<C> senderClass, @NotNull String label) {
		super(commandManager, senderClass, label);
	}
	
	public static <C> BungeeCommandSyntaxBuilder<C> builder(BungeeCommandManager manager, @NotNull Class<C> senderClass, @NotNull String label) {
		return new BungeeCommandSyntaxBuilder<>(manager, senderClass, label);
	}
	
	public static BungeeCommandSyntaxBuilder<CommandSender> builder(BungeeCommandManager manager, @NotNull String label) {
		return builder(manager, CommandSender.class, label);
	}
	
}
