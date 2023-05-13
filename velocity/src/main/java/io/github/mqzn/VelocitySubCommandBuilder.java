package io.github.mqzn;

import com.velocitypowered.api.command.CommandSource;
import io.github.mqzn.commands.base.syntax.SubCommandBuilder;
import org.jetbrains.annotations.NotNull;

public final class VelocitySubCommandBuilder<C> extends SubCommandBuilder<CommandSource, C> {

	private VelocitySubCommandBuilder(@NotNull Class<C> senderClass,
	                                  @NotNull String label,
	                                  @NotNull String name) {
		super(senderClass, label, name);
	}

	public static <C> VelocitySubCommandBuilder<C> builder(@NotNull Class<C> senderClass,
	                                                       @NotNull String label,
	                                                       @NotNull String name) {
		return new VelocitySubCommandBuilder<>(senderClass, label, name);
	}

	public static VelocitySubCommandBuilder<CommandSource> builder(@NotNull String label,
	                                                               @NotNull String name) {
		return builder(CommandSource.class, label, name);
	}
}