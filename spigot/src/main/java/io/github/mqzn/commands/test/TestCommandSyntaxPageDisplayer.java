package io.github.mqzn.commands.test;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.Information;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.utilities.ArgumentSyntaxUtility;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import io.github.mqzn.commands.help.CommandHelpStyle;
import io.github.mqzn.commands.help.CommandSyntaxPageDisplayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public final class TestCommandSyntaxPageDisplayer<S> extends CommandSyntaxPageDisplayer<S> {
	
	private final CommandManager<?, S> manager;
	private final Command<S> command;
	private final CommandHelpStyle<S> provider;
	
	public TestCommandSyntaxPageDisplayer(@NotNull CommandManager<?, S> manager,
	                                      @NotNull Command<S> command,
	                                      @NotNull CommandHelpStyle<S> provider) {
		super(manager, command, provider);
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
		TextComponent description = (TextComponent) Component.text("|")
			.style(Style.style(NamedTextColor.DARK_AQUA))
			.appendSpace()
			.append(Component.text((syntaxInfo == null || syntaxInfo.description().isEmpty()
					|| syntaxInfo.description().isBlank() ? "Unknown purpose" : syntaxInfo.description()))
				.style(Style.style(NamedTextColor.GRAY)));
		
		
		TextComponent result = Component.text("> ", NamedTextColor.AQUA)
			.append(comp.style(provider.syntaxStyle(convertible)));
		
		//SHOULD NOT BE CHANGED UNLESS IT'S NECESSARY
		String format = ArgumentSyntaxUtility.format(manager, command.name(), CommandSyntax.getArguments(command.tree(), convertible));
		if (convertible instanceof SubCommandSyntax<S> sub && sub.hasChildren()) format = format + " help";
		result = result.clickEvent(ClickEvent.suggestCommand(format));
		
		return (TextComponent) result.appendSpace()
			.append(description);
	}
	
}
