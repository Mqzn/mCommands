package io.github.mqzn.commands.arguments;

import org.jetbrains.annotations.NotNull;

public final class ArgumentFloat extends ArgumentNumber<Float> {
	
	ArgumentFloat(@NotNull String id) {
		super(id, Float.class, Float::parseFloat,
			((s, radix) -> (float) Long.parseLong(s, radix)), Float::compare);
	}
	
	ArgumentFloat(@NotNull ArgumentData data) {
		super(data, Float.class, Float::parseFloat,
			((s, radix) -> (float) Long.parseLong(s, radix)), Float::compare);
	}
	
	
	@Override
	public Class<?>[] alternativeTypes() {
		return new Class[]{float.class};
	}
	
	@Override
	public Float increment(Float num) {
		return num + 1F;
	}
	
}
