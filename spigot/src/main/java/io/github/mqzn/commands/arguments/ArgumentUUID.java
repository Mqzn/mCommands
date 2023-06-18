package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.UUID;

public final class ArgumentUUID extends AbstractArgument<UUID> {
	
	public ArgumentUUID(@NotNull String id) {
		super(id, UUID.class);
	}
	
	public ArgumentUUID(@NotNull ArgumentData data) {
		super(data, UUID.class);
	}
	
	@Override
	public <S> UUID parse(@UnknownNullability S sender, @NotNull String command, @NotNull String input) throws ArgumentParseException {
		try {
			return UUID.fromString(input);
		} catch (Exception ex) {
			throw new ArgumentParseException(
				String.format("The uuid `%s` is not valid", input), input, command
			);
		}
	}
}
