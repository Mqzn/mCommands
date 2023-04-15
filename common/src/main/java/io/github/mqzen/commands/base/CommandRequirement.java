package io.github.mqzen.commands.base;

import io.github.mqzen.commands.base.caption.CaptionKey;
import io.github.mqzen.commands.base.context.Context;

public interface CommandRequirement<S> {


	boolean accepts(S sender, Context<S> commandContext);

	CaptionKey caption();

}
