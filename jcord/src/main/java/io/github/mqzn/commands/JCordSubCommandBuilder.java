package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.SubCommandBuilder;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

public class JCordSubCommandBuilder<C> extends SubCommandBuilder<User, C> {
	protected JCordSubCommandBuilder(@NotNull Class<C> senderClass, @NotNull String label, @NotNull String name) {
		super(senderClass, label, name);
	}

	public static <C> JCordSubCommandBuilder<C> builder(@NotNull Class<C> senderClass,
	                                                    @NotNull String label,
	                                                    @NotNull String name) {
		return new JCordSubCommandBuilder<>(senderClass, label, name);
	}

	public static JCordSubCommandBuilder<User> builder(@NotNull String label, @NotNull String name) {
		return builder(User.class, label, name);
	}
}
