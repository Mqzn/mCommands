package io.github.mqzn.commands.base;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;

public interface SenderWrapper<S> {
	
	Class<S> senderType();
	
	boolean isConsole(S sender);
	
	void sendMessage(S sender, String msg);
	
	void sendMessage(S sender, TextComponent component);
	
	boolean canBeSender(Class<?> type);
	
	boolean hasPermission(S sender, @Nullable String name);
	
	String senderName(S sender);
}
