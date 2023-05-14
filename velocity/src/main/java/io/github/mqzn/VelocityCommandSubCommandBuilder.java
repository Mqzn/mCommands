package io.github.mqzn;

import com.velocitypowered.api.command.CommandSource;
import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import org.jetbrains.annotations.NotNull;

public final class VelocityCommandSubCommandBuilder<C> extends CommandSyntaxBuilder<CommandSource, C> {
	
	private VelocityCommandSubCommandBuilder(@NotNull Class<C> senderClass, @NotNull String label) {
		super(senderClass, label);
	}
	
	public static <C> VelocityCommandSubCommandBuilder<C> builder(@NotNull Class<C> senderClass,
	                                                              @NotNull String commandLabel) {
		return new VelocityCommandSubCommandBuilder<>(senderClass, commandLabel);
	}
	
	public static VelocityCommandSubCommandBuilder<CommandSource> builder(@NotNull String commandLabel) {
		return builder(CommandSource.class, commandLabel);
	}
	
}