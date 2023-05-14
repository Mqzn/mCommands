package io.github.mqzn.commands.test;

import io.github.mqzn.commands.sender.SenderWrapper;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;

public final class ClientSenderWrapper implements SenderWrapper<ClientSender> {
	
	@Override
	public Class<ClientSender> senderType() {
		return ClientSender.class;
	}
	
	@Override
	public boolean isConsole(ClientSender sender) {
		return false;
	}
	
	@Override
	public void sendMessage(ClientSender sender, String msg) {
		System.out.println("Message to " + sender.name() + ": " + msg);
	}
	
	@Override
	public void sendMessage(ClientSender sender, TextComponent component) {
		sendMessage(sender, component.content());
	}
	
	@Override
	public boolean canBeSender(Class<?> type) {
		return type.getName().equals(senderType().getName());
	}
	
	@Override
	public boolean hasPermission(ClientSender sender, @Nullable String name) {
		return true;
	}
	
	@Override
	public String senderName(ClientSender sender) {
		return sender.name();
	}
	
	
}
