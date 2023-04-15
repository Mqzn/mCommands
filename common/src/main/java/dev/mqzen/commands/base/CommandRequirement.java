package dev.mqzen.commands.base;

import dev.mqzen.commands.base.caption.CaptionKey;
import dev.mqzen.commands.base.context.Context;

public interface CommandRequirement<S> {


	boolean accepts(S sender, Context<S> commandContext);

	CaptionKey caption();

}
