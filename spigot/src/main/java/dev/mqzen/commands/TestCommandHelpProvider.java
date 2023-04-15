package dev.mqzen.commands;

import dev.mqzen.commands.base.syntax.CommandSyntax;
import dev.mqzen.commands.help.CommandHelpProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public final class TestCommandHelpProvider implements CommandHelpProvider {


	public TestCommandHelpProvider() {

	}

	@Override
	public @NotNull Style lineStyle() {
		return Style.style(NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH, TextDecoration.BOLD);
	}


	@Override
	public @NotNull TextComponent header(String label) {
		return Component.text(label + "'s sub-commands");
	}

	@Override
	public @NotNull <S> Style syntaxStyle(@NotNull CommandSyntax<S> syntax) {
		return Style.style(NamedTextColor.YELLOW);
	}

}
