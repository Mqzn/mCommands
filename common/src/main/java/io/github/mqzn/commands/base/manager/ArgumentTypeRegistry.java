package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.arguments.ArgumentData;
import io.github.mqzn.commands.arguments.ArgumentNumber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ArgumentTypeRegistry {

	@NotNull
	private final ArgumentNumberComparator argumentNumberComparator;

	@NotNull
	private final Map<Class<?>, Function<ArgumentData, Argument<?>>> argumentCreatorMapper = new HashMap<>();


	ArgumentTypeRegistry() {

		//TODO create one for enum
		this.argumentNumberComparator = new ArgumentNumberComparator();

		argumentCreatorMapper.put(Integer.class, Argument::integer);
		argumentCreatorMapper.put(int.class, Argument::integer);

		argumentCreatorMapper.put(Double.class, Argument::Double);
		argumentCreatorMapper.put(double.class, Argument::Double);

		argumentCreatorMapper.put(Float.class, Argument::Float);
		argumentCreatorMapper.put(float.class, Argument::Float);

		argumentCreatorMapper.put(Long.class, Argument::Long);
		argumentCreatorMapper.put(long.class, Argument::Long);

		argumentCreatorMapper.put(Boolean.class, Argument::Boolean);
		argumentCreatorMapper.put(boolean.class, Argument::Boolean);

		argumentCreatorMapper.put(String[].class, (data) -> Argument.Array(data.getId()));
	}


	@Nullable
	public Argument<?> convertArgument(@NotNull ArgumentData data, @NotNull Class<?> clazz) {
		var mapper = argumentCreatorMapper.get(clazz);
		if (mapper == null) {
			return null;
		}
		return mapper.apply(data);
	}

	public void registerArgumentConverter(Class<?> type, Function<ArgumentData, Argument<?>> mapper) {
		argumentCreatorMapper.put(type, mapper);
	}


	public <N extends Number> ArgumentNumberComparator.ArgumentComparator<N> getComparator(Class<? extends ArgumentNumber<N>> argNumClass) {
		return argumentNumberComparator.comparatorOfArg(argNumClass);
	}

}
