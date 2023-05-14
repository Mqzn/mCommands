package io.github.mqzn.commands.base.manager.flags;

import io.github.mqzn.commands.base.Information;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final class FlagInfo {
	
	
	@NotNull
	private final String name;
	
	@Nullable
	private final Information information;
	
	private final String[] aliases;
	
	
	private FlagInfo(@NotNull String name, @Nullable Information information, String... aliases) {
		this.name = name;
		this.information = information;
		this.aliases = aliases;
	}
	
	public static @NotNull Builder builder(String name) {
		return new Builder(name);
	}
	
	public boolean hasAliase(String aliase) {
		for (var a : aliases)
			if (a.equalsIgnoreCase(aliase)) return true;
		
		return false;
		
	}
	
	public static final class Builder {
		private final String name;
		
		@Nullable
		private Information information = null;
		
		private String[] aliases = new String[0];
		
		Builder(String name) {
			this.name = name;
		}
		
		public @NotNull Builder info(@Nullable Information information) {
			this.information = information;
			return this;
		}
		
		public @NotNull Builder aliases(@NotNull String... aliases) {
			this.aliases = aliases;
			return this;
		}
		
		
		public @NotNull FlagInfo build() {
			return new FlagInfo(name, information, aliases);
		}
		
	}
	
}
