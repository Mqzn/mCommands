package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

public abstract class ArgumentNumber<T extends Number> extends AbstractArgument<T> {

	public static final int NOT_NUMBER_ERROR = 1;
	public static final int TOO_LOW_ERROR = 2;
	public static final int TOO_HIGH_ERROR = 3;

	protected final BiFunction<String, Integer, T> radixParser;

	@Getter
	protected final Function<String, T> parser;

	protected final Comparator<T> comparator;

	protected boolean hasMin, hasMax;

	protected T min, max;

	ArgumentNumber(@NotNull String id, Class<T> type,
	               Function<String, T> parser,
	               BiFunction<String, Integer, T> radixParser,
	               Comparator<T> comparator) {
		super(id, type);
		this.radixParser = radixParser;
		this.parser = parser;
		this.comparator = comparator;

	}

	@Override
	public @NotNull T parse(@NotNull Command<?> command, @NotNull String input) throws ArgumentParseException {
		try {
			final T value;
			final int radix = getRadix(input);
			if (radix == 10) {
				value = parser.apply(parseValue(input));
			} else {
				value = radixParser.apply(parseValue(input), radix);
			}

			// Check range
			if (hasMin && comparator.compare(value, min) < 0) {
				throw new ArgumentParseException(String.format("Input '%s' is lower than the minimum allowed value", input), input, TOO_LOW_ERROR, command);
			}
			if (hasMax && comparator.compare(value, max) > 0) {
				throw new ArgumentParseException(String.format("Input '%s' is higher than the maximum allowed value", input), input, TOO_HIGH_ERROR, command);
			}

			return value;
		} catch (NumberFormatException | NullPointerException e) {
			throw new ArgumentParseException(String.format("Input '%s' is not a number, or it's invalid for the given type", input), input, NOT_NUMBER_ERROR, command);
		}
	}


	@NotNull
	public ArgumentNumber<T> min(@NotNull T value) {
		this.min = value;
		this.hasMin = true;
		return this;
	}

	@NotNull
	public ArgumentNumber<T> max(@NotNull T value) {
		this.max = value;
		this.hasMax = true;

		return this;
	}

	@NotNull
	public ArgumentNumber<T> between(@NotNull T min, @NotNull T max) {
		this.min = min;
		this.max = max;
		this.hasMin = true;
		this.hasMax = true;
		return this;
	}

	/**
	 * Creates the byteflag based on the number's min/max existance.
	 *
	 * @return A byteflag for argument specification.
	 */
	public byte getNumberProperties() {
		byte result = 0;
		if (this.hasMin())
			result |= 0x1;
		if (this.hasMax())
			result |= 0x2;
		return result;
	}

	/**
	 * Gets if the argument has a minimum.
	 *
	 * @return true if the argument has a minimum
	 */
	public boolean hasMin() {
		return hasMin;
	}

	/**
	 * Gets the minimum value for this argument.
	 *
	 * @return the minimum of this argument
	 */
	@NotNull
	public T getMin() {
		return min;
	}

	/**
	 * Gets if the argument has a maximum.
	 *
	 * @return true if the argument has a maximum
	 */
	public boolean hasMax() {
		return hasMax;
	}

	/**
	 * Gets the maximum value for this argument.
	 *
	 * @return the maximum of this argument
	 */
	@NotNull
	public T getMax() {
		return max;
	}

	@NotNull
	protected String parseValue(@NotNull String value) {
		if (value.startsWith("0b")) {
			value = value.replaceFirst(Pattern.quote("0b"), "");
		} else if (value.startsWith("0x")) {
			value = value.replaceFirst(Pattern.quote("0x"), "");
		} else if (value.toLowerCase().contains("e")) {
			value = removeScientificNotation(value);
		}
		// TODO number suffix support (k,m,b,t)
		return value;
	}

	protected int getRadix(@NotNull String value) {
		if (value.startsWith("0b")) {
			return 2;
		} else if (value.startsWith("0x")) {
			return 16;
		}
		return 10;
	}

	@Nullable
	protected String removeScientificNotation(@NotNull String value) {
		try {
			return new BigDecimal(value).toPlainString();
		} catch (NumberFormatException e) {
			return null;
		}
	}


}