package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import io.github.mqzn.commands.base.syntax.SubCommandBuilder;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Class that reperesents a builder for subcommands in
 * the platform "spigot"
 *
 * @param <C> the sender type
 * @see CommandSyntaxBuilder
 * @see SubCommandSyntax
 * @see SubCommandBuilder
 */
public final class SpigotSubCommandBuilder<C> extends SubCommandBuilder<CommandSender, C> {
	
	private SpigotSubCommandBuilder(@NotNull SpigotCommandManager commandManager, @NotNull Class<C> senderClass, @NotNull String label, @NotNull String name) {
		super(commandManager, senderClass, label, name);
	}
	
	/**
	 * Creating the builder's instance, that will be used to build the syntax
	 * of the subcommand
	 *
	 * @param senderClass  custom sender class (optional)
	 * @param commandLabel the command name
	 * @param name         the subcommand name
	 * @param <C>          the sender type
	 * @return the syntax builder
	 * @see CommandSyntaxBuilder
	 */
	public static <C> SpigotSubCommandBuilder<C> builder(@NotNull SpigotCommandManager commandManager,
	                                                     @NotNull Class<C> senderClass,
	                                                     @NotNull String commandLabel,
	                                                     @NotNull String name) {
		return new SpigotSubCommandBuilder<>(commandManager, senderClass, commandLabel, name);
	}
	
	/**
	 * Creating the builder's instance, that will be used to build the syntax
	 * of the subcommand
	 *
	 * @param commandLabel the command name
	 * @param name         the subcommand name
	 * @return the subcommand-syntax builder
	 * @see CommandSyntaxBuilder
	 */
	public static SpigotSubCommandBuilder<CommandSender> builder(@NotNull SpigotCommandManager commandManager,
	                                                             @NotNull String commandLabel, @NotNull String name) {
		return builder(commandManager, CommandSender.class, commandLabel, name);
	}
	
}