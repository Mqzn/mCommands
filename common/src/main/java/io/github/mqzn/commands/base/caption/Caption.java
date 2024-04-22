package io.github.mqzn.commands.base.caption;

import io.github.mqzn.commands.base.context.Context;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Caption<S> {
	
	
	static <S> @NotNull Builder<S> builder(CaptionKey key) {
		return new Builder<>(key);
	}
	
	@NotNull CaptionKey key();
	
	@NotNull TextComponent message(S sender, Context<S> context, Throwable exception);
	
	interface CaptionResult<S> {
		
		@NotNull TextComponent messageResult(@NotNull S sender,
		                                     @NotNull Context<S> context,
		                                     @Nullable Throwable exception);
		
	}
	
	final class Builder<S> {
		
		@NotNull
		private final CaptionKey key;
		
		@NotNull
		private CaptionResult<S> messageCreator = (cmd, s, ex) -> Component.empty();
		
		Builder(@NotNull CaptionKey key) {
			this.key = key;
		}
		
		public @NotNull Builder<S> withMessage(@NotNull CaptionResult<S> messageCreator) {
			this.messageCreator = messageCreator;
			return this;
		}
		
		
		public Caption<S> build() {
			return new ImmutableCaption<>(key, messageCreator);
		}
		
		record ImmutableCaption<S>(CaptionKey key, CaptionResult<S> messageCreator) implements Caption<S> {
			
			
			@Override
			public @NotNull TextComponent message(@NotNull S sender,
			                                      @NotNull Context<S> context,
			                                      @Nullable Throwable exception) {
				return messageCreator.messageResult(sender, context, exception);
			}
			
		}
		
		
	}
	
}
