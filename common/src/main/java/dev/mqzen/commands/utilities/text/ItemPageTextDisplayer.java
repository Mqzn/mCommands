package dev.mqzen.commands.utilities.text;


import dev.mqzen.commands.sender.SenderWrapper;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface ItemPageTextDisplayer<S, T extends TextConvertible<S>> {


	TextComponent displayPageItem(@NotNull S sender, @NotNull T convertible, int index);

	default void display(SenderWrapper<S> wrapper, @NotNull S sender, @NotNull TextPage<S, T> page) {

		int i = 1;
		for (T pageItem : page) {
			TextComponent toDisplay = displayPageItem(sender, pageItem, i);
			wrapper.sendMessage(sender, toDisplay);
			i++;
		}

	}

}
