package dev.mqzen.commands.spigot.display;

import dev.mqzen.commands.base.syntax.CommandSyntax;
import dev.mqzen.commands.help.HelpMessageProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public final class TestHelpProvider implements HelpMessageProvider {

	@NotNull
	private final String label;

	public TestHelpProvider(@NotNull String label) {
		this.label = label;
	}

	@Override
	public @NotNull Style lineStyle() {
		return Style.style(NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH, TextDecoration.BOLD);
	}

	@Override
	public @NotNull TextComponent header() {
		return Component.text(label + "'s sub-commands");
	}

	@Override
	public @NotNull <S> Style syntaxStyle(@NotNull CommandSyntax<S> syntax) {
		return Style.style(NamedTextColor.YELLOW);
	}

}
