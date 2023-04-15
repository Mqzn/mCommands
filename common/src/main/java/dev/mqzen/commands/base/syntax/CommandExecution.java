package dev.mqzen.commands.base.syntax;

import dev.mqzen.commands.base.context.Context;
import org.jetbrains.annotations.NotNull;

public interface CommandExecution<S> {

	void execute(@NotNull S sender, Context<S> commandContext);

}
