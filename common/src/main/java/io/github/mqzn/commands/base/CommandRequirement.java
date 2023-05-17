package io.github.mqzn.commands.base;

import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.context.Context;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a requirement/criteria
 * that must be true for the command to be executed
 *
 * @param <S> the sender type
 */
public interface CommandRequirement<S> {
	
	/**
	 * Whether the criteria are true or not
	 *
	 * @param sender         the command sender
	 * @param commandContext the context used
	 * @return Whether the criteria are true or not
	 */
	boolean accepts(S sender, Context<S> commandContext);
	
	/**
	 * The caption that will be sent
	 * to the user if the criteria is false
	 *
	 * @return null if no caption to send,
	 * otherwise it will return the key of the caption to send
	 */
	@Nullable CaptionKey caption();
	
}
