package io.github.mqzen.commands.base.syntax;

import io.github.mqzen.commands.base.context.Context;
import org.jetbrains.annotations.NotNull;

public interface CommandExecution<S> {

	void execute(@NotNull S sender, Context<S> commandContext);

}
