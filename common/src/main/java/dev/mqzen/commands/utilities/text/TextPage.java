package net.versemc.api.utilities.text;

import lombok.Data;
import lombok.NonNull;

import java.util.Iterator;
import java.util.List;

@Data(staticConstructor = "of")
public final class TextPage<T extends TextConvertible> implements Iterable<T> {

	private final int pageIndex, capacity;
	private final List<T> pageItems;

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
