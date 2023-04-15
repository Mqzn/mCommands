package dev.mqzen.commands.base.caption;

import dev.mqzen.commands.base.context.Context;
import dev.mqzen.commands.base.manager.CommandManager;
import dev.mqzen.commands.exceptions.CommandException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CaptionRegistry<S> {

	private final CommandManager<?, S> manager;

	@NotNull
	private final Map<CaptionKey, Caption<S>> captions;

	public CaptionRegistry(CommandManager<?, S> manager) {
		this.manager = manager;
		this.captions = new HashMap<>();
	}

	public void registerCaption(Caption<S> caption) {
		captions.put(caption.key(), caption);
	}

	public void unregisterCaption(Caption<S> caption) {
		captions.remove(caption.key());
	}

	@Nullable
	public Caption<S> getCaption(CaptionKey key) {
		return captions.get(key);
	}

	public <E extends CommandException> void sendCaption(S sender, Context<S> commandContext, @Nullable E exception, CaptionKey key) {
		var caption = getCaption(key);

		if (caption == null) return;
		sendCaption(sender, commandContext, exception, caption);
	}

	public void sendCaption(S sender, Context<S> commandContext, CaptionKey key) {
		this.sendCaption(sender, commandContext, null, key);
	}

	<E extends CommandException> void sendCaption(@NotNull S sender,
	                                              @NotNull Context<S> commandContext,
	                                              @Nullable E exception,
	                                              @NotNull Caption<S> caption) {
		var text = caption.message(sender, commandContext, exception);
		manager.getSenderWrapper().sendMessage(sender, text);
	}

}
