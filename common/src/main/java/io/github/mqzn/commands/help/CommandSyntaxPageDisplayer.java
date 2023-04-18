package io.github.mqzn.commands.help;

import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.utilities.text.ItemPageTextDisplayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

/**
 * The class to display each syntax of a command
 * it defines how it displays a single syntax
 *
 * @see CommandSyntax
 * @see ItemPageTextDisplayer
 */
public final class CommandSyntaxPageDisplayer<S> implements ItemPageTextDisplayer<S, CommandSyntax<S>> {

	private final CommandManager<?, S> manager;

	@NotNull
	private final CommandHelpProvider provider;

	public CommandSyntaxPageDisplayer(@NotNull CommandManager<?, S> manager,
	                                  @NotNull CommandHelpProvider provider) {
		this.manager = manager;
		this.provider = provider;
	}


	@Override
	public TextComponent displayPageItem(@NotNull S sender,
	                                     @NotNull CommandSyntax<S> convertible,
	                                     int index) {

		TextComponent comp = convertible.toText(manager, sender);
		TextComponent description = (TextComponent) Component.text("--")
						.style(Style.style(NamedTextColor.GOLD, TextDecoration.STRIKETHROUGH))
						.appendSpace()
						.append(Component.text((convertible.getInfo() == null ? "N/A" : convertible.getInfo().description()), NamedTextColor.WHITE));

		return (TextComponent) Component.text("+ ", NamedTextColor.BLUE)
						.append(comp.style(provider.syntaxStyle(convertible)))
						.appendSpace()
						.append(description);
	}

}
