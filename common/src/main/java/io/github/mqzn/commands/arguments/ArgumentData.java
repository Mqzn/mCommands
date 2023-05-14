package io.github.mqzn.commands.arguments;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public final class ArgumentData {
	
	@NotNull
	private final String id;
	
	private boolean optional;
	
	private boolean useRemainingSpace;
	
	private ArgumentData(@NotNull String id, boolean optional, boolean useRemainingSpace) {
		this.id = id;
		this.optional = optional;
		this.useRemainingSpace = useRemainingSpace;
	}
	
	private ArgumentData(@NotNull String id) {
		this(id, false, false);
	}
	
	public static ArgumentData of(@NotNull String id, boolean optional, boolean useRemainingSpace) {
		return new ArgumentData(id, optional, useRemainingSpace);
	}
	
	public static ArgumentData of(@NotNull String id) {
		return new ArgumentData(id);
	}
	
}