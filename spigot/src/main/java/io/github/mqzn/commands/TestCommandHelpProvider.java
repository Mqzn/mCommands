package io.github.mqzn.commands;

import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.help.CommandHelpProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public final class TestCommandHelpProvider implements CommandHelpProvider {

	/**
	 * The line style of the help topic
	 *
	 * @see Style
	 *
	 * @return how the line is displayed in the help topic
	 * e.g: "[style]========== Help Menu [style]============
	 *
	 */
	@Override
	public @NotNull Style lineStyle() {
		return Style.style(NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH, TextDecoration.BOLD);
	}

	/**
	 * The header of the help topic
	 *
	 * @param label the command label (name)
	 * @return the header of the help topic
	 */
	@Override
	public @NotNull TextComponent header(String label) {
		return Component.text(label + "'s sub-commands");
	}


	/**
	 * The style of the syntax of a command
	 *
	 * @param syntax the syntax of a command
	 *
	 * @return The style of the syntax of a command
	 * @param <S> the sender type param
	 */
	@Override
	public @NotNull <S> Style syntaxStyle(@NotNull CommandSyntax<S> syntax) {
		return Style.style(NamedTextColor.YELLOW);
	}

}
