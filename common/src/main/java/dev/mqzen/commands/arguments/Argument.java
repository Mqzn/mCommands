package dev.mqzen.commands.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Argument<T> {

	/**
	 *
	 * The id of the Required argument
	 *
	 * @return the id of the Required argument
	 */
	String id();

	/**
	 *
	 * The type of the argument
	 *
	 * @return the class type of the argument
	 */
	Class<T> type();



	/**
	 * The default value of this argument
	 * if the default value is null, means it has no
	 * default values, so it's a REQUIRED argument !
	 *
	 * @return The default value of this argument
	 */
	@Nullable T defaultValue();

	/**
	 * Modifies the default value of the argument
	 *
	 * @param value the new default value
	 */
	void setDefaultValue(T value);

	T parse(@NotNull String input);

	default boolean isOptional() {
		return defaultValue() != null;
	}

	default Class<?>[] alternativeTypes() {
		return new Class[0];
	}

}
