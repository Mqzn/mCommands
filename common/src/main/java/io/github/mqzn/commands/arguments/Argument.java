package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Optional;

/**
 * An interface to represent an argument as a command-syntax
 * parameter to be used in usages and context parsing
 *
 * @param <T> the data type of the argument parameter
 */
public interface Argument<T> {
	
	/**
	 * @param id the name of the argument
	 * @return creates a literal argument
	 */
	static ArgumentLiteral literal(String id) {
		return new ArgumentLiteral(id);
	}
	
	/**
	 * @param id the name of the argument
	 * @return creates a word argument
	 */
	static ArgumentWord word(String id) {
		return new ArgumentWord(id);
	}
	
	/**
	 * @param id the name of the argument
	 * @return creates a boolean(true/false) argument
	 */
	static ArgumentBoolean Boolean(String id) {
		return new ArgumentBoolean(id);
	}
	
	/**
	 * @param id the name of the argument
	 * @return creates an integer argument
	 */
	static ArgumentInteger integer(String id) {
		return new ArgumentInteger(id);
	}
	
	/**
	 * @param id the name of the argument
	 * @return creates a double argument
	 */
	static ArgumentDouble Double(String id) {
		return new ArgumentDouble(id);
	}
	
	/**
	 * @param id the name of the argument
	 * @return creates a float argument
	 */
	static ArgumentFloat Float(String id) {
		return new ArgumentFloat(id);
	}
	
	/**
	 * @param id the name of the argument
	 * @return creates a long argument
	 */
	static ArgumentLong Long(String id) {
		return new ArgumentLong(id);
	}
	
	/**
	 * @param id the name of the argument
	 * @return creates an array argument from a greedy/long string
	 */
	static ArgumentStringArray Array(String id) {
		return new ArgumentStringArray(id);
	}
	
	/**
	 * @param id the name of the argument
	 * @return creates an enum-related argument
	 */
	static <E extends Enum<E>> ArgumentEnum<E> Enum(String id, Class<E> enumClass) {
		return new ArgumentEnum<>(id, enumClass);
	}
	
	/**
	 * @param data the data of the argument
	 * @return creates a literal argument
	 * @see ArgumentData
	 */
	static ArgumentLiteral literal(@NotNull ArgumentData data) {
		return new ArgumentLiteral(data);
	}
	
	/**
	 * @param data the data of the argument
	 * @return creates a word/string argument
	 * @see ArgumentData
	 */
	static ArgumentWord word(@NotNull ArgumentData data) {
		return new ArgumentWord(data);
	}
	
	/**
	 * @param data the data of the argument
	 * @return creates a boolean(true/false) argument
	 * @see ArgumentData
	 */
	static ArgumentBoolean Boolean(@NotNull ArgumentData data) {
		return new ArgumentBoolean(data);
	}
	
	/**
	 * @param data the data of the argument
	 * @return creates an integer argument
	 * @see ArgumentData
	 */
	static ArgumentInteger integer(@NotNull ArgumentData data) {
		return new ArgumentInteger(data);
	}
	
	/**
	 * @param data the data of the argument
	 * @return creates a double argument
	 * @see ArgumentData
	 */
	static ArgumentDouble Double(@NotNull ArgumentData data) {
		return new ArgumentDouble(data);
	}
	
	/**
	 * @param data the data of the argument
	 * @return creates a float argument
	 * @see ArgumentData
	 */
	static ArgumentFloat Float(@NotNull ArgumentData data) {
		return new ArgumentFloat(data);
	}
	
	/**
	 * @param data the data of the argument
	 * @return creates a long argument
	 * @see ArgumentData
	 */
	static ArgumentLong Long(ArgumentData data) {
		return new ArgumentLong(data);
	}
	
	/**
	 * @param data the data of the argument
	 * @return creates an enum argument
	 * @see ArgumentData
	 */
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
	 * The description of an argument
	 *
	 * @return the description for usage
	 */
	Optional<@Nullable String> description();
	
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
	@SuppressWarnings("UnusedReturnValue")
	@NotNull Argument<T> setDefaultValue(T value);
	
	/**
	 * Parses the raw input into it's argument type
	 * The argument type must be registered and defined by the user
	 *
	 * @param sender  the sender of the command
	 * @param command the command
	 * @param input   the raw argument
	 * @param <S>     the sender type
	 * @return the parsed object from raw
	 * @throws ArgumentParseException when it fails to parse the raw argument
	 */
	<S> T parse(@UnknownNullability S sender,
	            @NotNull String command,
	            @NotNull String input) throws ArgumentParseException;
	
	/**
	 * @return whether this argument is optional or not
	 */
	default boolean isOptional() {
		return false;
	}
	
	/**
	 * Sets the argument to be optional or not
	 *
	 * @param optional whether it's gonna be optional or not
	 */
	void setOptional(boolean optional);
	
	/**
	 * Adds a suggestion object into the argument for TAB-completion
	 *
	 * @param suggestion the suggestion to add
	 * @return builder-pattern
	 */
	Argument<T> suggest(@NotNull T suggestion);
	
	/**
	 * Sets the description of the argument
	 *
	 * @param description the new description of the argument
	 * @return builder-pattern
	 */
	Argument<T> description(@Nullable String description);
	
	/**
	 * @return the suggestions of this argument
	 */
	@NotNull List<T> suggestions();
	
	/**
	 * Suggests multiple suggestions for this argument
	 *
	 * @param suggestions the suggestions to add
	 * @return builder-pattern
	 */
	@SuppressWarnings("unchecked")
	Argument<T> suggest(@NotNull T... suggestions);
	
	/**
	 * changes the type of argument's object to a string
	 *
	 * @param obj the object type of the argument
	 * @return object as string
	 */
	String toString(T obj);
	
	/**
	 * Alternative types that relate to similar data-type of the argument
	 * example-> boolean.class refers to Boolean.class
	 *
	 * @return the alternative data-types of this argument's data-types
	 */
	default Class<?>[] alternativeTypes() {
		return new Class[0];
	}
	
	/**
	 * A dynamic suggestion is a type of suggestion in TAB-completion
	 * in which, the suggestion go through modifications and changes of its value
	 * through the app's run-time, so requires fetching/loading it actively.
	 *
	 * @return whether this argument is based on dynamic suggestions or not
	 */
	default boolean isSuggestionDynamic() {
		return false;
	}
	
}
