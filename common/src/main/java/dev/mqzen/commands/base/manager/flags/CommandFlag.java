package dev.mqzen.commands.base.flags;

import dev.mqzen.commands.arguments.Argument;
import dev.mqzen.commands.base.syntax.CommandSyntax;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed interface CommandFlag permits CommandFlag.Builder.SimpleCommandFlag {

	@NotNull String name();

	@NotNull FlagMode mode();

	@Nullable CommandSyntax.Information info();

	@NotNull String[] aliases();

	@NotNull Argument<?> argument();

	enum FlagMode {

		SINGLE_ONLY,

		ALLOWS_MULTIPLE_FLAGS;

	}

	final class Builder {

		@NotNull
		private final String name;

		@NotNull
		private final Argument<?> argument;

		@NotNull
		private FlagMode mode = FlagMode.SINGLE_ONLY;

		@Nullable
		private String permission = null;

		@Nullable
		private String description = null;

		@NotNull
		private String[] aliases = new String[0];

		Builder(@NotNull String name, @NotNull Argument<?> argument) {
			this.name = name;
			this.argument = argument;
		}

		public @NotNull Builder withMode(@NotNull FlagMode mode) {
			this.mode = mode;
			return this;
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
			CommandSyntax.Information info = new CommandSyntax.Information(permission, description);
			return new SimpleCommandFlag(name, mode, info, argument, aliases);
		}


		record SimpleCommandFlag(@NotNull String name, @NotNull FlagMode mode, @Nullable CommandSyntax.Information info,
		                                 @NotNull Argument<?> argument, @NotNull String... aliases) implements CommandFlag {
		}

	}



}
