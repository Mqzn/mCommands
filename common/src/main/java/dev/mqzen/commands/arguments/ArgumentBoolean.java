package dev.mqzen.commands.arguments;

import dev.mqzen.commands.base.Command;
import dev.mqzen.commands.exceptions.types.ArgumentParseException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class ArgumentBoolean extends AbstractArgument<Boolean> {
	ArgumentBoolean(String id) {
		super(id, Boolean.class);
	}

	@Override
	public Boolean parse(@NotNull Command<?> command, @NotNull String input) throws ArgumentParseException {

		if (!input.equalsIgnoreCase("true") && !input.equalsIgnoreCase("false") && !input.equalsIgnoreCase("no") && !input.equalsIgnoreCase("yes"))
			throw new ArgumentParseException("Argument '" + id() + "' should be boolean, the input '" + input + "' is not a valid boolean", input, 3, command);

		return input.equalsIgnoreCase("true")
						|| input.equalsIgnoreCase("yes");
	}

	@Override
	public @NotNull List<Boolean> suggestions() {
		return Arrays.asList(true, false);
	}

	@Override
	public Class<?>[] alternativeTypes() {
		return new Class[]{boolean.class};
	}

}
