package io.github.mqzn.commands.help;

import io.github.mqzn.commands.base.syntax.CommandSyntax;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface CommandHelpStyle<S> {
	
	static <S> Builder<S> builder() {
		return new Builder<>();
	}
	
	/**
	 * The line style of the help topic
	 *
	 * @return how the line is displayed in the help topic
	 * e.g: "[style]========== Help Menu [style]============
	 * @see Style
	 */
	@NotNull Style lineStyle();
	
	/**
	 * The header of the help topic
	 *
	 * @param label the command label (name)
	 * @return the header of the help topic
	 */
	@NotNull TextComponent header(String label);
	
	/**
	 * The style of the syntax of a command
	 *
	 * @param syntax the syntax of a command
	 * @return The style of the syntax of a command
	 */
	@NotNull Style syntaxStyle(@NotNull CommandSyntax<S> syntax);
	
	final class Builder<S> {
		
		private Style lineStyle;
		private Function<CommandSyntax<S>, Style> syntaxStyleMapper;
		private Function<String, TextComponent> header;
		
		Builder() {
		}
		
		public Builder<S> lineStyle(Style lineStyle) {
			this.lineStyle = lineStyle;
			return this;
		}
		
		public Builder<S> syntaxStyle(Function<CommandSyntax<S>, Style> syntaxStyleMapper) {
			this.syntaxStyleMapper = syntaxStyleMapper;
			return this;
		}
		
		public Builder<S> header(Function<String, TextComponent> header) {
			this.header = header;
			return this;
		}
		
		public CommandHelpStyle<S> build() {
			return new ImmutableHelpStyle<>(lineStyle, syntaxStyleMapper, header);
		}
		
		
	}
	
	record ImmutableHelpStyle<S>(Style lineStyle,
	                             Function<CommandSyntax<S>, Style> syntaxStyle,
	                             Function<String, TextComponent> header) implements CommandHelpStyle<S> {
		
		/**
		 * The header of the help topic
		 *
		 * @param label the command label (name)
		 * @return the header of the help topic
		 */
		@Override
		public @NotNull TextComponent header(String label) {
			return header.apply(label);
		}
		
		/**
		 * The style of the syntax of a command
		 *
		 * @param syntax the syntax of a command
		 * @return The style of the syntax of a command
		 */
		@Override
		public @NotNull Style syntaxStyle(@NotNull CommandSyntax<S> syntax) {
			return syntaxStyle.apply(syntax);
		}
		
	}
	
}
