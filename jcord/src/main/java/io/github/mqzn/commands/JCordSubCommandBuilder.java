package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.SubCommandBuilder;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

public class JCordSubCommandBuilder<C> extends SubCommandBuilder<User, C> {
	protected JCordSubCommandBuilder(@NotNull JCordCommandManager manager, @NotNull Class<C> senderClass, @NotNull String label, @NotNull String name) {
		super(manager, senderClass, label, name);
	}
	
	public static <C> JCordSubCommandBuilder<C> builder(@NotNull JCordCommandManager manager, @NotNull Class<C> senderClass,
	                                                    @NotNull String label,
	                                                    @NotNull String name) {
		return new JCordSubCommandBuilder<>(manager, senderClass, label, name);
	}
	
	public static JCordSubCommandBuilder<User> builder(@NotNull JCordCommandManager manager, @NotNull String label, @NotNull String name) {
		return builder(manager, User.class, label, name);
	}
}
