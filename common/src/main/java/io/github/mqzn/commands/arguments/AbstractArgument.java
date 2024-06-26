package io.github.mqzn.commands.arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This class represents an argument as a command-syntax
 * parameter to be used in usages and context parsing
 *
 * @param <T> the data type of the argument parameter
 */
public abstract class AbstractArgument<T> implements Argument<T> {
	
	@NotNull
	protected final List<T> suggestions = new ArrayList<>();
	
	@NotNull
	private final String id;
	
	@NotNull
	private final Class<T> type;
	
	private final boolean useRemainingSpace;
	
	@Nullable
	private String description = null;
	
	private boolean optional;
	
	@Nullable
	private T defaultValue = null;
	
	public AbstractArgument(@NotNull String id, @NotNull Class<T> type) {
		this(id, type, false, false);
	}
	
	public AbstractArgument(@NotNull String id, @NotNull Class<T> type, boolean optional, boolean useRemainingSpace) {
		this.id = id;
		this.type = type;
		this.optional = optional;
		this.useRemainingSpace = useRemainingSpace;
	}
	
	public AbstractArgument(String id, Class<T> type, boolean useRemainingSpace) {
		this(id, type, false, useRemainingSpace);
	}
	
	public AbstractArgument(@NotNull ArgumentData data, Class<T> type) {
		this(data.getId(), type, data.isOptional(), data.isUseRemainingSpace());
	}
	
	/**
	 * The id of the Required argument
	 *
	 * @return the id of the Required argument
	 */
	@Override
	public String id() {
		return id;
	}
	
	/**
	 * The description of an argument
	 *
	 * @return the description for usage
	 */
	@Override
	public Optional<@Nullable String> description() {
		return Optional.ofNullable(description);
	}
	
	/**
	 * The type of the argument
	 *
	 * @return the class type of the argument
	 */
	@Override
	public Class<T> type() {
		return type;
	}
	
	
	/**
	 * @return the default value of the argument if it's not set
	 * by default it's going to be null
	 */
	@Override
	public @Nullable T defaultValue() {
		return defaultValue;
	}
	
	/**
	 * @param value the new default value
	 * @return builder pattern
	 */
	@Override
	public @NotNull Argument<T> setDefaultValue(@Nullable T value) {
		this.defaultValue = value;
		return this;
	}
	
	/**
	 * @return whether this argument is optional or not
	 */
	@Override
	public boolean isOptional() {
		return optional;
	}
	
	/**
	 * Sets the argument to be optional or not
	 *
	 * @param optional whether it's going to be optional or not
	 */
	@Override
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	/**
	 * Sets it automatically as optional (true)
	 *
	 * @return builder-pattern
	 */
	public Argument<T> asOptional() {
		setOptional(true);
		return this;
	}
	
	/**
	 * Whether the argument can become greedy
	 * and use the remaining args till the end of the args
	 *
	 * @return Whether the argument can become greedy
	 * * and use the remaining args till the end of the args
	 */
	@Override
	public boolean useRemainingSpace() {
		return useRemainingSpace;
	}
	
	/**
	 * Sets the description of the argument
	 *
	 * @param description the new description of the argument
	 * @return builder-pattern
	 */
	@Override
	public Argument<T> description(@Nullable String description) {
		this.description = description;
		return this;
	}
	
	/**
	 * Adds a suggestion object into the argument for TAB-completion
	 *
	 * @param suggestion the suggestion to add
	 * @return builder-pattern
	 */
	@Override
	public Argument<T> suggest(@NotNull T suggestion) {
		suggestions.add(suggestion);
		return this;
	}
	
	/**
	 * @return the suggestions of this argument
	 */
	@Override
	public @NotNull List<T> suggestions() {
		return suggestions;
	}
	
	/**
	 * Suggests multiple suggestions for this argument
	 *
	 * @param suggestions the suggestions to add
	 * @return builder-pattern
	 */
	@Override
	@SafeVarargs
	public final Argument<T> suggest(@NotNull T... suggestions) {
		for (T suggestion : suggestions) {
			suggest(suggestion);
		}
		return this;
	}
	
	/**
	 * changes the type of argument's object to a string
	 *
	 * @param obj the object type of the argument
	 * @return object as string
	 */
	@Override
	public String toString(T obj) {
		return obj.toString();
	}
	
	@Override
	public String toString() {
		return id + ":" + type.getSimpleName();
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractArgument<?> that)) return false;
		return id.equals(that.id) && type.equals(that.type);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, type);
	}
}

	



