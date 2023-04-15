package dev.mqzen.commands.arguments;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ArgumentLong extends ArgumentNumber<Long> {

	ArgumentLong(@NotNull String id) {
		super(id, Long.class, Long::parseLong, Long::parseLong, Long::compare);
	}

	@Override
	public @NotNull List<Long> suggestions() {

		if (hasMax && hasMin) {
			List<Long> suggestions = new ArrayList<>();

			for (long i = 0; i <= max; i++) {
				suggestions.add(i);
			}

			return suggestions;
		}

		return Collections.singletonList(0L);
	}

	@Override
	public Class<?>[] alternativeTypes() {
		return new Class[]{long.class};
	}

}
