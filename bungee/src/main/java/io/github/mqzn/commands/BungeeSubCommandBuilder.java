package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.SubCommandBuilder;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class BungeeSubCommandBuilder<C> extends SubCommandBuilder<CommandSender, C> {
	private BungeeSubCommandBuilder(BungeeCommandManager manager, @NotNull Class<C> senderClass, @NotNull String label, @NotNull String name) {
		super(manager, senderClass, label, name);
	}
	
	/**
	 * @param manager     the bungee command-manager
	 * @param senderClass the type of the sender to use
	 * @param label       the command label
	 * @param name        the name of the subcommand
	 * @param <C>         the type of the sender to use while building the subcommand's syntax
	 * @return the builder of subcommands in the bungee-cord platform
	 */
	public static <C> BungeeSubCommandBuilder<C> builder(BungeeCommandManager manager,
	                                                     @NotNull Class<C> senderClass,
	                                                     @NotNull String label,
	                                                     @NotNull String name) {
		return new BungeeSubCommandBuilder<>(manager, senderClass, label, name);
	}
	
	public static BungeeSubCommandBuilder<CommandSender> builder(BungeeCommandManager manager, @NotNull String label, @NotNull String name) {
		return builder(manager, CommandSender.class, label, name);
	}
}
