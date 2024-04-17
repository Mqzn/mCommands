package io.github.mqzn.commands.help;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.Information;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import io.github.mqzn.commands.utilities.ArgumentSyntaxUtility;
import io.github.mqzn.commands.utilities.text.ItemPageTextDisplayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

/**
 * The class to display each syntax of a command
 * it defines how it displays a single syntax
 *
 * @see CommandSyntax
 * @see ItemPageTextDisplayer
 */
public class CommandSyntaxPageDisplayer<S> implements ItemPageTextDisplayer<S, CommandSyntax<S>> {
	
	@NotNull
	protected final CommandManager<?, S> manager;
	
	@NotNull
	protected final CommandHelpStyle<S> provider;
	protected final Command<S> command;
	
	public CommandSyntaxPageDisplayer(@NotNull CommandManager<?, S> manager,
	                                  @NotNull Command<S> command,
	                                  @NotNull CommandHelpStyle<S> provider) {
		this.manager = manager;
		this.command = command;
		this.provider = provider;
	}
	
	
	@Override
	public TextComponent displayPageItem(@NotNull S sender,
	                                     @NotNull CommandSyntax<S> convertible,
	                                     int index) {
		
		Information syntaxInfo = convertible.getInfo();
		
		TextComponent comp = convertible.toText(manager, sender);
		TextComponent description = (TextComponent) Component.text("-")
			.style(Style.style(NamedTextColor.GOLD))
			.appendSpace()
			.append(Component.text((syntaxInfo == null || syntaxInfo.description().isEmpty()
					|| syntaxInfo.description().isBlank() ? "N/A" : syntaxInfo.description()))
				.style(Style.style(NamedTextColor.WHITE)));
		
		
		TextComponent result = Component.text("+ ", NamedTextColor.BLUE)
			.append(comp.style(provider.syntaxStyle(convertible)));
		
		String format = ArgumentSyntaxUtility.format(manager, command.name(), CommandSyntax.getArguments(command.tree(), convertible));
		if (convertible instanceof SubCommandSyntax<S> sub && sub.hasChildren()) format = format + " help";
		result = result.clickEvent(ClickEvent.suggestCommand(format));
		
		return (TextComponent) result.appendSpace()
			.append(description);
	}
	
}
