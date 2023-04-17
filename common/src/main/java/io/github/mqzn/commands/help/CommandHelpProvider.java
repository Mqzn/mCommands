package io.github.mqzn.commands.help;

import io.github.mqzn.commands.base.syntax.CommandSyntax;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public interface CommandHelpProvider {

	/**
	 * The line style of the help topic
	 *
	 * @see Style
	 *
	 * @return how the line is displayed in the help topic
	 * e.g: "[style]========== Help Menu [style]============
	 *
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
	 *
	 * @return The style of the syntax of a command
	 * @param <S> the sender type param
	 */
	@NotNull <S> Style syntaxStyle(@NotNull CommandSyntax<S> syntax);

}
