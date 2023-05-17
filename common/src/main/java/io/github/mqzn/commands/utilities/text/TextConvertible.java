package io.github.mqzn.commands.utilities.text;

import io.github.mqzn.commands.base.manager.CommandManager;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface TextConvertible<S> {
	
	@NotNull TextComponent toText(@NotNull CommandManager<?, S> manager, @NotNull S sender);
	
}
