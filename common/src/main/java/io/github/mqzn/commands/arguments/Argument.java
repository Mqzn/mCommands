package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public interface Argument<T> {

	static ArgumentLiteral literal(String id) {
		return new ArgumentLiteral(id);
	}

	static ArgumentWord word(String id) {
		return new ArgumentWord(id);
	}

	static ArgumentBoolean Boolean(String id) {
		return new ArgumentBoolean(id);
	}

	static ArgumentInteger integer(String id) {
		return new ArgumentInteger(id);
	}

	static ArgumentDouble Double(String id) {
		return new ArgumentDouble(id);
	}

	static ArgumentFloat Float(String id) {
		return new ArgumentFloat(id);
	}

	static ArgumentLong Long(String id) {
		return new ArgumentLong(id);
	}

	static ArgumentStringArray Array(String id) {
		return new ArgumentStringArray(id);
	}

	static <E extends Enum<E>> ArgumentEnum<E> Enum(String id, Class<E> enumClass) {
		return new ArgumentEnum<>(id, enumClass);
	}


	static ArgumentLiteral literal(@NotNull ArgumentData data) {
		return new ArgumentLiteral(data);
	}

	static ArgumentWord word(@NotNull ArgumentData data) {
		return new ArgumentWord(data);
	}

	static ArgumentBoolean Boolean(@NotNull ArgumentData data) {
		return new ArgumentBoolean(data);
	}

	static ArgumentInteger integer(@NotNull ArgumentData data) {
		return new ArgumentInteger(data);
	}

	static ArgumentDouble Double(@NotNull ArgumentData data) {
		return new ArgumentDouble(data);
	}

	static ArgumentFloat Float(@NotNull ArgumentData data) {
		return new ArgumentFloat(data);
	}

	static ArgumentLong Long(ArgumentData id) {
		return new ArgumentLong(id);
	}

	static <E extends Enum<E>> ArgumentEnum<E> Enum(ArgumentData data, Class<E> enumClass) {
		return new ArgumentEnum<>(data, enumClass);
	}

	/**
	 * The id of the Required argument
	 *
	 * @return the id of the Required argument
	 */
	String id();

	/**
	 * The type of the argument
	 *
	 * @return the class type of the argument
	 */
	Class<T> type();

	/* types */

	/**
	 * Whether the argument can become greedy
	 * and use the remaining args till the end of the args
	 *
	 * @return Whether the argument can become greedy
	 * * and use the remaining args till the end of the args
	 */
	boolean useRemainingSpace();

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
	@NotNull Argument<T> setDefaultValue(T value);

	T parse(@NotNull Command<?> command, @NotNull String input) throws ArgumentParseException;

	default boolean isOptional() {
		return false;
	}

	void setOptional(boolean optional);

	Argument<T> suggest(@NotNull T suggestion);

	@NotNull <S> List<T> suggestions();

	default Class<?>[] alternativeTypes() {
		return new Class[0];
	}

}
