package net.versemc.api.utilities.text;


import lombok.NonNull;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface ItemPageTextDisplayer<T extends TextConvertible> {

	TextComponent displayPageItem(@NotNull CommandSender sender, @NotNull T convertible, int index);

	default void display(@NonNull CommandSender sender, @NonNull TextPage<T> page) {
		int i = 1;
		for (T pageItem : page) {
			TextComponent toDisplay = displayPageItem(sender, pageItem, i);
			sender.sendMessage(toDisplay);
			i++;
		}
	}

}
