package io.github.mqzen.commands.utilities.text;

import lombok.NonNull;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface TextConvertible<S> {

	@NonNull TextComponent toText(@NotNull S sender);

}
