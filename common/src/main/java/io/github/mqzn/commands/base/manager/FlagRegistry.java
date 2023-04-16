package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.base.manager.flags.FlagInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public final class FlagRegistry {

	@NotNull
	public final static String FLAG_IDENTIFIER = "-";
	@NotNull
	private static final AtomicBoolean flagRegistryCreated = new AtomicBoolean(false);
	@NotNull
	private final Map<String, FlagInfo> flags = new HashMap<>();

	private FlagRegistry() {
		flagRegistryCreated.set(true);
	}

	static FlagRegistry create() throws IllegalAccessException {

		if (flagRegistryCreated.get()) {
			throw new IllegalAccessException("Error trying to create another instance of flag registry !");
		}

		return new FlagRegistry();
	}

	public void registerFlag(@NotNull FlagInfo flag) {
		flags.put(flag.getName(), flag);
	}


	public void unregisterFlag(String flag) {
		flags.remove(flag);

		for (var f : flags.values()) {
			if (f.hasAliase(flag)) {
				flags.remove(flag);
				break;
			}

		}

	}

	public @Nullable FlagInfo getFlag(String flag) {
		for (FlagInfo f : flags.values()) {
			if (f.getName().equalsIgnoreCase(flag) || f.hasAliase(flag))
				return f;
		}

		return null;
	}


	public @NotNull Collection<FlagInfo> flags() {
		return flags.values();
	}

	public boolean flagExists(String flagAlias) {
		return getFlag(flagAlias) != null;
	}

}
