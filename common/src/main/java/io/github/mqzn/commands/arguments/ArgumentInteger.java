package io.github.mqzn.commands.arguments;

public final class ArgumentInteger extends ArgumentNumber<Integer> {

	ArgumentInteger(String id) {
		super(id, Integer.class,
						Integer::parseInt,
						Integer::parseInt,
						Integer::compare);
	}

	ArgumentInteger(ArgumentData data) {
		super(data, Integer.class,
						Integer::parseInt,
						Integer::parseInt,
						Integer::compare);
	}

	@Override
	public Class<?>[] alternativeTypes() {
		return new Class[]{int.class};
	}

	@Override
	public Integer increment(Integer num) {
		return num + 1;
	}
}
