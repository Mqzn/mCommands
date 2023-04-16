package io.github.mqzn.commands.arguments;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ArgumentInteger extends ArgumentNumber<Integer> {

	ArgumentInteger(String id) {
		super(id, Integer.class,
						Integer::parseInt,
						Integer::parseInt,
						Integer::compare);
	}

	@Override
	public @NotNull List<Integer> suggestions() {

		if (hasMax && hasMin) {
			List<Integer> suggestions = new ArrayList<>();

			for (int i = 0; i <= max; i++) {
				suggestions.add(i);
			}

			return suggestions;
		}

		return Collections.singletonList(0);
	}

	@Override
	public Class<?>[] alternativeTypes() {
		return new Class[]{int.class};
	}

}
