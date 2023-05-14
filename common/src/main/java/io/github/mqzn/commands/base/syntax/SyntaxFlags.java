package io.github.mqzn.commands.base.syntax;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@EqualsAndHashCode
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
		flags.add(flag);
	}
	
	public void removeFlag(String flag) {
		flags.remove(flag);
	}
	
	public boolean hasFlag(String flag) {
		return flags.contains(flag);
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
	
}
