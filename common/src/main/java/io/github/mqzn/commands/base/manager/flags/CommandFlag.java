package io.github.mqzn.commands.base.manager.flags;

import io.github.mqzn.commands.base.Information;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed interface CommandFlag permits CommandFlag.Builder.SimpleCommandFlag {

	static @NotNull CommandFlag from(FlagInfo info) {
		Builder builder = CommandFlag.builder(info.getName())
						.withAliases(info.getAliases());

		if (info.getInformation() != null) {

			var flagInformation = info.getInformation();
			if (flagInformation.permission() != null)
				builder.withPermission(flagInformation.permission());

			if (flagInformation.description() != null)
				builder.withDescription(flagInformation.description());

		}

		return builder.build();
	}

	static @NotNull Builder builder(String flagName) {
		return new Builder(flagName);
	}

	@NotNull String name();

	@Nullable Information info();

	@NotNull String[] aliases();

	default boolean hasAliase(String aliase) {
		for (var a : aliases())
			if (a.equalsIgnoreCase(aliase)) return true;

		return false;
	}

	final class Builder {

		@NotNull
		private final String name;

		@Nullable
		private String permission = null;

		@Nullable
		private String description = null;

		@NotNull
		private String[] aliases = new String[0];

		Builder(@NotNull String name) {
			this.name = name;
		}


		public @NotNull Builder withAliases(@NotNull String... aliases) {
			this.aliases = aliases;
			return this;
		}

		public @NotNull Builder withPermission(@NotNull String permission) {
			this.permission = permission;
			return this;
		}

		public @NotNull Builder withDescription(@NotNull String description) {
			this.description = description;
			return this;
		}

		public CommandFlag build() {
			Information info = new Information(permission, description);
			return new SimpleCommandFlag(name, info, aliases);
		}


		record SimpleCommandFlag(@NotNull String name,
		                         @Nullable Information info,
		                         @NotNull String... aliases) implements CommandFlag {
		}

	}


}
