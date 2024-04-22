package io.github.mqzn.commands.exceptions;

import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import io.github.mqzn.commands.utilities.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class CommandExceptionHandler<S> {
	
	@NotNull
	private final Map<Class<? extends Throwable>, ExceptionCallback<S>> callBacks = new HashMap<>();
	
	public CommandExceptionHandler(@NotNull CommandManager<?, S> manager) {
		
		registerCallback(ArgumentParseException.class, ((exception, sender, commandContext) -> manager.captionRegistry().sendCaption(sender, commandContext, exception, CaptionKey.INVALID_ARGUMENT)));
	}
	
	public void registerCallback(@NotNull Class<? extends Throwable> exception, @NotNull ExceptionCallback<S> callback) {
		callBacks.put(exception, callback);
	}
	
	public void handleException(@NotNull Throwable exception,
	                            @NotNull S sender,
	                            @NotNull Context<S> commandContext) {
		
		var pair = this.getHandleData(exception);
		ExceptionCallback<S> handle = pair.getRight();
		if (handle != null)
			handle.callback(pair.getLeft(), sender, commandContext);
		else
			exception.printStackTrace();
	}
	
	private Pair<Throwable, ExceptionCallback<S>> getHandleData(@NotNull Throwable exception) {
		ExceptionCallback<S> handle = callBacks.get(exception.getClass());
		if (handle != null)
			return Pair.of(exception, handle);
		
		Throwable deepestCause = exception.getCause();
		if (deepestCause == null)
			return Pair.empty();
		
		Throwable lastCause = deepestCause;
		handle = callBacks.get(deepestCause.getClass());
		while (handle == null) {
			deepestCause = deepestCause.getCause();
			if (deepestCause == null) break;
			lastCause = deepestCause;
			handle = callBacks.get(deepestCause.getClass());
		}
		
		
		return Pair.of(lastCause, handle);
	}
	
	@FunctionalInterface
	public interface ExceptionCallback<S> {
		
		void callback(Throwable exception, S sender, Context<S> commandContext);
		
	}
	
}
