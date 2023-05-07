package io.github.mqzn.commands.arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractArgument<T> implements Argument<T> {

	@NotNull
	protected final List<T> suggestions = new ArrayList<>();

	@NotNull
	private final String id;

	@NotNull
	private final Class<T> type;

	private final boolean useRemainingSpace;

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
	 * The type of the argument
	 *
	 * @return the class type of the argument
	 */
	@Override
	public Class<T> type() {
		return type;
	}


	@Override
	public @Nullable T defaultValue() {
		return defaultValue;
	}

	@Override
	public @NotNull Argument<T> setDefaultValue(@Nullable T value) {
		this.defaultValue = value;
		return this;
	}

	@Override
	public boolean isOptional() {
		return optional;
	}


	@Override
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public Argument<T> asOptional(boolean optional) {
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

	@Override
	public Argument<T> suggest(@NotNull T suggestion) {
		suggestions.add(suggestion);
		return this;
	}


	@Override
	public @NotNull List<T> suggestions() {
		return suggestions;
	}

	@Override
	public String toString(T obj) {
		return obj.toString();
	}

}

	



