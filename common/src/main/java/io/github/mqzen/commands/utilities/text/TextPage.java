package io.github.mqzen.commands.utilities.text;

import lombok.NonNull;

import java.util.Iterator;
import java.util.List;

public record TextPage<S, T extends TextConvertible<S>>(int pageIndex, int capacity,
                                                        List<T> pageItems) implements Iterable<T> {

	public void add(T obj) {
		if (pageItems.size() + 1 > capacity) return;
		pageItems.add(obj);
	}

	public void remove(T obj) {
		pageItems.remove(obj);
	}

	public void addAll(List<T> otherItems) {
		otherItems.forEach(this::add);
	}

	@NonNull
	@Override
	public Iterator<T> iterator() {
		return pageItems.iterator();
	}


}
