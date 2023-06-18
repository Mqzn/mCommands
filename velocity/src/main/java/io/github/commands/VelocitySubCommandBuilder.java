package io.github.commands;

import com.velocitypowered.api.command.CommandSource;
import io.github.mqzn.commands.base.syntax.CommandSyntaxBuilder;
import org.jetbrains.annotations.NotNull;

public final class VelocitySubCommandBuilder<C> extends CommandSyntaxBuilder<CommandSource, C> {
	
	private VelocitySubCommandBuilder(@NotNull VelocityCommandManager manager, @NotNull Class<C> senderClass, @NotNull String label) {
		super(manager, senderClass, label);
	}
	
	public static <C> VelocitySubCommandBuilder<C> builder(@NotNull VelocityCommandManager manager, @NotNull Class<C> senderClass,
	                                                       @NotNull String commandLabel) {
		return new VelocitySubCommandBuilder<>(manager, senderClass, commandLabel);
	}
	
	public static VelocitySubCommandBuilder<CommandSource> builder(@NotNull VelocityCommandManager manager, @NotNull String commandLabel) {
		return builder(manager, CommandSource.class, commandLabel);
	}
	
}