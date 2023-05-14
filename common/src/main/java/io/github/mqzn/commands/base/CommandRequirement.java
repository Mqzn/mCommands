package io.github.mqzn.commands.base;

import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.context.Context;
import org.jetbrains.annotations.Nullable;

public interface CommandRequirement<S> {
	
	
	boolean accepts(S sender, Context<S> commandContext);
	
	@Nullable CaptionKey caption();
	
}
