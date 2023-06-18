package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * The builder for syntax in spigot's platform
 *
 * @param <C> the sender type
 */
public final class SpigotCommandSyntaxBuilder<C> extends CommandSyntaxBuilder<CommandSender, C> {
	
	private SpigotCommandSyntaxBuilder(SpigotCommandManager commandManager,
	                                   @NotNull Class<C> senderClass, @NotNull String label) {
		super(commandManager, senderClass, label);
	}
	
	/**
	 * Creating the builder's instance
	 *
	 * @param senderClass  custom sender class (optional)
	 * @param commandLabel the command name
	 * @param <C>          the sender type
	 * @return the syntax builder
	 * @see CommandSyntaxBuilder
	 */
	public static <C> SpigotCommandSyntaxBuilder<C> builder(@NotNull SpigotCommandManager commandManager,
	                                                        @NotNull Class<C> senderClass,
	                                                        @NotNull String commandLabel) {
		return new SpigotCommandSyntaxBuilder<>(commandManager, senderClass, commandLabel);
	}
	
	/**
	 * Creating the builder's instance
	 *
	 * @param commandLabel the command name
	 * @return the syntax builder
	 * @see CommandSyntaxBuilder
	 */
	public static SpigotCommandSyntaxBuilder<CommandSender> builder(@NotNull SpigotCommandManager commandManager, @NotNull String commandLabel) {
		return builder(commandManager, CommandSender.class, commandLabel);
	}
	
}
