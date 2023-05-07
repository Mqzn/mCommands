package io.github.mqzn;

import com.velocitypowered.api.command.CommandSource;
import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import org.jetbrains.annotations.NotNull;

public final class VelocityCommandSyntaxBuilder<C> extends CommandSyntaxBuilder<CommandSource, C> {

	private VelocityCommandSyntaxBuilder(@NotNull Class<C> senderClass, @NotNull String label) {
		super(senderClass, label);
	}

	public static <C> VelocityCommandSyntaxBuilder<C> builder(@NotNull Class<C> senderClass,
	                                                          @NotNull String commandLabel) {
		return new VelocityCommandSyntaxBuilder<>(senderClass, commandLabel);
	}

	public static VelocityCommandSyntaxBuilder<CommandSource> builder(@NotNull String commandLabel) {
		return builder(CommandSource.class, commandLabel);
	}

}