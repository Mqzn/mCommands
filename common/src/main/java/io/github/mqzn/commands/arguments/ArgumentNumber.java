package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A class that represents any numeric argument
 *
 * @param <T> the type of the number
 */
public abstract class ArgumentNumber<T extends Number> extends AbstractArgument<T> {
	
	protected final BiFunction<String, Integer, T> radixParser;
	
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
	
	ArgumentNumber(@NotNull ArgumentData data, Class<T> type,
	               Function<String, T> parser,
	               BiFunction<String, Integer, T> radixParser,
	               Comparator<T> comparator) {
		super(data, type);
		this.radixParser = radixParser;
		this.parser = parser;
		this.comparator = comparator;
	}
	
	@Override
	public @NotNull T parse(@NotNull String command, @NotNull String input) throws ArgumentParseException {
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
				throw new ArgumentParseException(String.format("Input '%s' is lower than the minimum allowed value", input), input, command);
			}
			if (hasMax && comparator.compare(value, max) > 0) {
				throw new ArgumentParseException(String.format("Input '%s' is higher than the maximum allowed value", input), input, command);
			}
			
			return value;
		} catch (NumberFormatException | NullPointerException e) {
			throw new ArgumentParseException(String.format("Input '%s' is not a number, or it's invalid for the given type", input), input, command);
		}
	}
	
	
	@NotNull
	public ArgumentNumber<T> min(@NotNull T value) {
		this.min = value;
		this.hasMin = true;
		return this;
	}
	
	@NotNull
	@SuppressWarnings("UnusedReturnValue")
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
		assert value != null;
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
	
	
	public abstract T increment(T num);
	
	public Function<String, T> getParser() {
		return parser;
	}
	
}