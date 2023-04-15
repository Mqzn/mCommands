package dev.mqzen.commands.base.manager.registries;

import dev.mqzen.commands.base.flags.CommandFlag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class FlagRegistry {

	@NotNull
	private final Map<String, CommandFlag> flags = new HashMap<>();

}
