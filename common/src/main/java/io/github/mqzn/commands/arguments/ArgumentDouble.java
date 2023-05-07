package io.github.mqzn.commands.arguments;

import org.jetbrains.annotations.NotNull;

public final class ArgumentDouble extends ArgumentNumber<Double> {


	ArgumentDouble(@NotNull String id) {
		super(id, Double.class, Double::parseDouble,
						((s, radix) -> (double) Long.parseLong(s, radix)), Double::compare);
	}

	ArgumentDouble(@NotNull ArgumentData data) {
		super(data, Double.class, Double::parseDouble,
						((s, radix) -> (double) Long.parseLong(s, radix)), Double::compare);
	}

	@Override
	public Class<?>[] alternativeTypes() {
		return new Class[]{double.class};
	}


	@Override
	public Double increment(Double num) {
		return num + 1D;
	}

}
