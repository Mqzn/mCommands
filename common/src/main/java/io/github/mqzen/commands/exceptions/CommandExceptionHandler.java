package io.github.mqzen.commands.exceptions;

import io.github.mqzen.commands.base.caption.CaptionKey;
import io.github.mqzen.commands.base.context.CommandContext;
import io.github.mqzen.commands.base.manager.CommandManager;
import io.github.mqzen.commands.exceptions.types.ArgumentParseException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class CommandExceptionHandler<S> {


	@NotNull
	@Getter
	private final CommandManager<?, S> manager;

	@NotNull
	private final Map<Class<? extends CommandException>, ExceptionCallback<?, S>> callBacks = new HashMap<>();

	public CommandExceptionHandler(@NotNull CommandManager<?, S> manager) {

		this.manager = manager;

		registerCallback(ArgumentParseException.class, ((exception, sender, commandContext) -> manager.captionRegistry().sendCaption(sender, commandContext, exception, CaptionKey.INVALID_ARGUMENT)));


	}

	public <E extends CommandException> void registerCallback(@NotNull Class<E> exception, @NotNull ExceptionCallback<E, S> callback) {
		callBacks.put(exception, callback);
	}

	@SuppressWarnings("unchecked")
	public <E extends CommandException> void handleException(@NotNull E exception,
	                                                         @NotNull S sender,
	                                                         @NotNull CommandContext<S> commandContext) {

		ExceptionCallback<E, S> handle = (ExceptionCallback<E, S>) callBacks.get(exception.getClass());
		if (handle != null)
			handle.callback(exception, sender, commandContext);
		else
			throw new RuntimeException(exception);

	}

	private interface ExceptionCallback<E extends CommandException, S> {


		void callback(E exception, S sender, CommandContext<S> commandContext);

	}

}
