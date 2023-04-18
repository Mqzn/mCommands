package io.github.mqzn.commands.utilities.text;

import io.github.mqzn.commands.base.manager.CommandManager;
import lombok.NonNull;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface TextConvertible<S> {

	@NonNull TextComponent toText(@NotNull CommandManager<?, S> manager, @NotNull S sender);

}
