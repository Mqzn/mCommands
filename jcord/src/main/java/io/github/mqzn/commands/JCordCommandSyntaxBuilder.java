package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

public final class JCordCommandSyntaxBuilder<C> extends CommandSyntaxBuilder<User, C> {

	private JCordCommandSyntaxBuilder(@NotNull Class<C> senderClass, @NotNull String label) {
		super(senderClass, label);
	}

	public static <C> JCordCommandSyntaxBuilder<C> builder(@NotNull Class<C> senderClass, @NotNull String label) {
		return new JCordCommandSyntaxBuilder<>(senderClass, label);
	}

	public static JCordCommandSyntaxBuilder<User> builder(@NotNull String label) {
		return builder(User.class, label);
	}

}