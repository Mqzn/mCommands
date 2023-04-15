package io.github.mqzen.commands.arguments;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ArgumentDouble extends ArgumentNumber<Double> {


	ArgumentDouble(@NotNull String id) {
		super(id, Double.class, Double::parseDouble,
						((s, radix) -> (double) Long.parseLong(s, radix)), Double::compare);
	}

	@Override
	public @NotNull List<Double> suggestions() {

		if (hasMax && hasMin) {
			List<Double> suggestions = new ArrayList<>();

			for (double i = 0; i <= max; i++) {
				suggestions.add(i);
			}

			return suggestions;
		}

		return Collections.singletonList(0D);
	}


	@Override
	public Class<?>[] alternativeTypes() {
		return new Class[]{double.class};
	}

}
