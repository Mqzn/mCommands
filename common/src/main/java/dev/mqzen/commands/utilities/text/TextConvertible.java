package net.versemc.api.utilities.text;

import lombok.NonNull;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface TextConvertible {

	@NonNull TextComponent toText(@NotNull CommandSender sender);

}
