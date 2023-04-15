package io.github.mqzen.commands.arguments;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ArgumentFloat extends ArgumentNumber<Float> {

	ArgumentFloat(@NotNull String id) {
		super(id, Float.class, Float::parseFloat,
						((s, radix) -> (float) Long.parseLong(s, radix)), Float::compare);
	}

	@Override
	public @NotNull List<Float> suggestions() {

		if (hasMax && hasMin) {
			List<Float> suggestions = new ArrayList<>();

			for (float i = 0; i <= max; i++) {
				suggestions.add(i);
			}

			return suggestions;
		}

		return Collections.singletonList(0f);
	}

	@Override
	public Class<?>[] alternativeTypes() {
		return new Class[]{float.class};
	}
}
