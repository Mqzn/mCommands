package io.github.mqzn.commands.base;

import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.context.Context;

public interface CommandRequirement<S> {


	boolean accepts(S sender, Context<S> commandContext);

	CaptionKey caption();

}
