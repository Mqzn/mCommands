package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

public final class JCordCommandSyntaxBuilder<C> extends CommandSyntaxBuilder<User, C> {
	
	private JCordCommandSyntaxBuilder(@NotNull JCordCommandManager manager,
	                                  @NotNull Class<C> senderClass, @NotNull String label) {
		super(manager, senderClass, label);
	}
	
	public static <C> JCordCommandSyntaxBuilder<C> builder(@NotNull JCordCommandManager manager,
	                                                       @NotNull Class<C> senderClass, @NotNull String label) {
		return new JCordCommandSyntaxBuilder<>(manager, senderClass, label);
	}
	
	public static JCordCommandSyntaxBuilder<User> builder(@NotNull JCordCommandManager manager, @NotNull String label) {
		return builder(manager, User.class, label);
	}
	
}