package dev.mqzen.commands.base.requirements;

import dev.mqzen.commands.base.CommandContext;

public interface CommandRequirement<S> {


	boolean accepts(S sender, CommandContext<S> commandContext);

}
