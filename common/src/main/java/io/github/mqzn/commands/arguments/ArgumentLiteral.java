package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.base.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public final class ArgumentLiteral extends AbstractArgument<String> {

	ArgumentLiteral(String id) {
		super(id, String.class);
	}

	ArgumentLiteral(ArgumentData data) {
		super(data, String.class);
	}

	@Override
	public String parse(@NotNull Command<?> command, @NotNull String input) {
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
	public @NotNull <S> List<String> suggestions() {
		return Collections.singletonList(this.id());
	}

	@Override
	public Class<?>[] alternativeTypes() {
		return super.alternativeTypes();
	}

}
