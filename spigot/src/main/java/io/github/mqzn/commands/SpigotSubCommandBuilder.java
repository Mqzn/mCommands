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
 * @see CommandSyntaxBuilder
 * @see SubCommandSyntax
 * @see SubCommandBuilder
 *
 * @param <C> the sender type
 */
public final class SpigotSubCommandBuilder<C> extends SubCommandBuilder<CommandSender, C> {
	
	private SpigotSubCommandBuilder(@NotNull Class<C> senderClass, @NotNull String label, @NotNull String name) {
		super(senderClass, label, name);
	}
	
	/**
	 * Creating the builder's instance, that will be used to build the syntax
	 * of the subcommand
	 * @see CommandSyntaxBuilder
	 *
	 * @param senderClass custom sender class (optional)
	 * @param commandLabel the command name
	 * @param name the subcommand name
	 *
	 * @return the syntax builder
	 *
	 * @param <C> the sender type
	 */
	public static <C> SpigotSubCommandBuilder<C> builder(@NotNull Class<C> senderClass,
	                                                     @NotNull String commandLabel,
	                                                     @NotNull String name) {
		return new SpigotSubCommandBuilder<>(senderClass, commandLabel, name);
	}
	/**
	 * Creating the builder's instance, that will be used to build the syntax
	 * of the subcommand
	 * @see CommandSyntaxBuilder
	 *
	 * @param commandLabel the command name
	 * @param name the subcommand name
	 *
	 * @return the subcommand-syntax builder
	 */
	public static SpigotSubCommandBuilder<CommandSender> builder(@NotNull String commandLabel, @NotNull String name) {
		return builder(CommandSender.class, commandLabel, name);
	}
	
}