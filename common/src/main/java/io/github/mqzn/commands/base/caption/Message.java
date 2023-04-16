package io.github.mqzn.commands.base.caption;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public interface Message {

	TextComponent PREFIX = Component.text("[", NamedTextColor.DARK_GRAY)
					.append(Component.text("!", NamedTextColor.DARK_RED))
					.append(Component.text("]", NamedTextColor.DARK_GRAY))
					.append(Component.space());

	TextComponent EXECUTION_ERROR = Component.text("Execution Error: ", NamedTextColor.RED);

	TextComponent INVALID_ARGUMENT_ERROR = Component.text("Invalid Argument: ", NamedTextColor.RED);


	static TextComponent prefixed(TextComponent component) {
		return PREFIX.append(component);
	}

}
