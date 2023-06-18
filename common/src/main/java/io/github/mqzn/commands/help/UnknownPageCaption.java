package io.github.mqzn.commands.help;

import io.github.mqzn.commands.base.caption.Caption;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.caption.Message;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.exceptions.CommandException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public final class UnknownPageCaption<S> implements Caption<S> {
	
	
	@Override
	public @NotNull CaptionKey key() {
		return CaptionKey.UNKNOWN_HELP_PAGE;
	}
	
	@Override
	public @NotNull <E extends CommandException> TextComponent message(S sender, Context<S> context, E exception) {
		Integer page = context.getArgument("page");
		if (page == null) page = 1;
		return Message.prefixed(Message.EXECUTION_ERROR).append(Component.text("Unknown page '" + page + "' make sure it's within boundaries"));
	}
}
