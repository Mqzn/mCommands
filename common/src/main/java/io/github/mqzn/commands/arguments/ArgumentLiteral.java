package io.github.mqzn.commands.arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArgumentLiteral extends AbstractArgument<String> {

	ArgumentLiteral(String id) {
		super(id, String.class);
	}

	ArgumentLiteral(ArgumentData data) {
		super(data, String.class);
	}

	@Override
	public String parse(@NotNull String command, @NotNull String input) {
		return input;
	}

	@Override
	public @Nullable String defaultValue() {
		return id();
	}

	@Override
	public @NotNull Argument<String> setDefaultValue(@Nullable String value) {
		throw new UnsupportedOperationException("Argument is a literal, it cannot have a default value !");
	}

	@Override
	public boolean isOptional() {
		return false;
	}

	@Override
	public Class<?>[] alternativeTypes() {
		return super.alternativeTypes();
	}

}
