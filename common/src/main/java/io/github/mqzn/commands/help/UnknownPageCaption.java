package io.github.mqzn.commands.help;

import io.github.mqzn.commands.base.caption.Caption;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.caption.Message;
import io.github.mqzn.commands.base.context.Context;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public final class UnknownPageCaption<S> implements Caption<S> {
	
	
	@Override
	public @NotNull CaptionKey key() {
		return CaptionKey.UNKNOWN_HELP_PAGE;
	}
	
	@Override
	public @NotNull TextComponent message(S sender, Context<S> context, Throwable exception) {
		Integer page = context.getArgument("page");
		if (page == null) page = 1;
		return Message.prefixed(Message.EXECUTION_ERROR).append(Component.text("Unknown page '" + page + "' make sure it's within boundaries"));
	}
}
