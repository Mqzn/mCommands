package io.github.mqzn.commands.base.syntax;

import io.github.mqzn.commands.base.context.Context;
import org.jetbrains.annotations.NotNull;

public interface CommandExecution<S> {

	void execute(@NotNull S sender, Context<S> commandContext);

}
