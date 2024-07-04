package io.github.mqzn.commands.base.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class SyntaxFlags implements Iterable<String> {
	
	@NotNull
	private final Set<String> flags = new HashSet<>();
	
	private SyntaxFlags(String... flags) {
		Collections.addAll(this.flags, flags);
	}
	
	public static SyntaxFlags of(String... flags) {
		return new SyntaxFlags(flags);
	}
	
	
	public void addFlag(String flag) {
		if(flag == null) return;
		flags.add(flag);
	}
	
	public void removeFlag(String flag) {
		flags.remove(flag);
	}
	
	public boolean hasFlag(String flag) {
		return flag != null && flags.contains(flag);
	}
	
	/**
	 * Returns an iterator over elements of type {@code T}.
	 *
	 * @return an Iterator.
	 */
	@NotNull
	@Override
	public Iterator<String> iterator() {
		return flags.iterator();
	}
	
	public int count() {
		return flags.size();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SyntaxFlags strings)) return false;
		return flags.equals(strings.flags);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(flags);
	}
	
}
