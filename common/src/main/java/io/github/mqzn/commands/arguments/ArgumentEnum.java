package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

public final class ArgumentEnum<E extends Enum<E>> extends AbstractArgument<E> {

	public final static int NOT_ENUM_VALUE_ERROR = 1;

	private final Class<E> enumClass;
	private final E[] values;
	private Format format = Format.DEFAULT;

	public ArgumentEnum(@NotNull String id, Class<E> enumClass) {
		super(id, enumClass);
		this.enumClass = enumClass;
		this.values = enumClass.getEnumConstants();
	}

	public ArgumentEnum(@NotNull ArgumentData data, Class<E> enumClass) {
		super(data, enumClass);
		this.enumClass = enumClass;
		this.values = enumClass.getEnumConstants();
	}

	public ArgumentEnum<E> setFormat(@NotNull Format format) {
		this.format = format;
		return this;
	}

	@NotNull
	@Override
	public E parse(@NotNull String command, @NotNull String input) throws ArgumentParseException {
		for (E value : this.values) {
			if (this.format.formatter.apply(value.name()).equals(input)) {
				return value;
			}
		}
		throw new ArgumentParseException("Not a " + this.enumClass.getSimpleName() + " value", input, command);
	}

	public List<String> entries() {
		return Arrays.stream(values).map(x -> format.formatter.apply(x.name())).toList();
	}

	@Override
	public String toString(E obj) {
		return obj.name();
	}

	public enum Format {
		DEFAULT(name -> name),
		LOWER_CASED(name -> name.toLowerCase(Locale.ROOT)),
		UPPER_CASED(name -> name.toUpperCase(Locale.ROOT));

		private final UnaryOperator<String> formatter;

		Format(@NotNull UnaryOperator<String> formatter) {
			this.formatter = formatter;
		}
	}
}