package dev.mqzen.commands.spigot.display;

import dev.mqzen.commands.base.syntax.CommandSyntax;
import dev.mqzen.commands.help.HelpMessageProvider;
import dev.mqzen.commands.utilities.text.ItemPageTextDisplayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * The class to display each syntax of a command
 * it defines how it displays a single syntax
 *
 * @see CommandSyntax
 * @see ItemPageTextDisplayer
 */
public class CommandSyntaxPageDisplayer implements ItemPageTextDisplayer<CommandSender, CommandSyntax<CommandSender>> {

	@NotNull
	private final HelpMessageProvider provider;

	public CommandSyntaxPageDisplayer(@NotNull HelpMessageProvider provider) {
		this.provider = provider;
	}

	@Override
	public TextComponent displayPageItem(@NotNull CommandSender sender,
	                                     @NotNull CommandSyntax<CommandSender> convertible,
	                                     int index) {

		TextComponent comp = convertible.toText(sender);
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
