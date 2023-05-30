package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * The builder for syntax in spigot's platform
 * @param <C> the sender type
 */
public final class SpigotCommandSyntaxBuilder<C> extends CommandSyntaxBuilder<CommandSender, C> {
	
	private SpigotCommandSyntaxBuilder(@NotNull Class<C> senderClass, @NotNull String label) {
		super(senderClass, label);
	}
	
	/**
	 * Creating the builder's instance
	 * @see CommandSyntaxBuilder
	 *
	 * @param senderClass custom sender class (optional)
	 * @param commandLabel the command name
	 *
	 * @return the syntax builder
	 *
	 * @param <C> the sender type
	 */
	public static <C> SpigotCommandSyntaxBuilder<C> builder(@NotNull Class<C> senderClass,
	                                                        @NotNull String commandLabel) {
		return new SpigotCommandSyntaxBuilder<>(senderClass, commandLabel);
	}
	
	/**
	 * Creating the builder's instance
	 * @see CommandSyntaxBuilder
	 *
	 * @param commandLabel the command name
	 *
	 * @return the syntax builder
	 * */
	public static SpigotCommandSyntaxBuilder<CommandSender> builder(@NotNull String commandLabel) {
		return builder(CommandSender.class, commandLabel);
	}
	
}
