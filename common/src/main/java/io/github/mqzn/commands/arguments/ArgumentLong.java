package io.github.mqzn.commands.arguments;

import org.jetbrains.annotations.NotNull;

public final class ArgumentLong extends ArgumentNumber<Long> {
	
	ArgumentLong(@NotNull String id) {
		super(id, Long.class, Long::parseLong, Long::parseLong, Long::compare);
	}
	
	ArgumentLong(@NotNull ArgumentData data) {
		super(data, Long.class, Long::parseLong, Long::parseLong, Long::compare);
	}
	
	
	@Override
	public Class<?>[] alternativeTypes() {
		return new Class[]{long.class};
	}
	
	@Override
	public Long increment(Long num) {
		return num + 1;
	}
}
