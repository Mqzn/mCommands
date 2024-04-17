package io.github.mqzn.commands.base.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CommandArgs implements Iterable<String> {
	
	private final List<String> args = new ArrayList<>();
	
	private <S> CommandArgs(Context<S> context) {
		args.addAll(context.getRawArguments());
	}
	
	public static <S> CommandArgs create(Context<S> context) {
		return new CommandArgs(context);
	}
	
	@Nullable
	public String getRaw(int index) {
		if (!verifyIndex(index)) return null;
		return args.get(index);
	}
	
	@Nullable
	public String getAfterRaw(String raw) {
		int index = args.indexOf(raw);
		if (!verifyIndex(index)) return null;
		return args.get(index);
	}
	
	@Override
	public String toString() {
		return "Raw-arguments => " + String.join(" ", this);
	}
	
	/**
	 * Returns an iterator over elements of type {@code T}.
	 *
	 * @return an Iterator.
	 */
	@NotNull
	@Override
	public Iterator<String> iterator() {
		return args.iterator();
	}
	
	private boolean verifyIndex(int index) {
		return index >= 0 && index < args.size();
	}
}
