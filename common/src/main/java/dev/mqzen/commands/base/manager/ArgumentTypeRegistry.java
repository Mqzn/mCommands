package dev.mqzen.commands.base.manager.registries;

import dev.mqzen.commands.arguments.Argument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ArgumentTypeRegistry {

	@NotNull
	private final Map<Class<?>, Function<String, Argument<?>>> argumentCreatorMapper = new HashMap<>();

	public ArgumentTypeRegistry() {

		//TODO create one for enum

		argumentCreatorMapper.put(Integer.class,Argument::integer);
		argumentCreatorMapper.put(int.class,Argument::integer);

		argumentCreatorMapper.put(Double.class, Argument::Double);
		argumentCreatorMapper.put(double.class, Argument::Double);

		argumentCreatorMapper.put(Float.class, Argument::Float);
		argumentCreatorMapper.put(float.class, Argument::Float);

		argumentCreatorMapper.put(Long.class, Argument::Long);
		argumentCreatorMapper.put(long.class, Argument::Long);

		argumentCreatorMapper.put(Boolean.class, Argument::Boolean);
		argumentCreatorMapper.put(boolean.class, Argument::Boolean);

		argumentCreatorMapper.put(String[].class, Argument::Array);

	}

	@Nullable
	public Argument<?> convertArgument(@NotNull String id, @NotNull Class<?> clazz) {
		var mapper = argumentCreatorMapper.get(clazz);
		if(mapper ==null){
			return null;
		}
		return mapper.apply(id);
	}

	public void registerArgumentConverter(Class<?> type, Function<String, Argument<?>> mapper) {
		argumentCreatorMapper.put(type, mapper);
	}

}
