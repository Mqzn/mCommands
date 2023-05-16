package io.github.mqzn;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import io.github.mqzn.commands.base.SenderWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;

final class VelocitySenderWrapper implements SenderWrapper<CommandSource> {
	
	public static final String VELOCITY_CONSOLE_SENDER_NAME = "CONSOLE";
	
	@Override
	public Class<CommandSource> senderType() {
		return CommandSource.class;
	}
	
	@Override
	public boolean isConsole(CommandSource sender) {
		return !(sender instanceof Player);
	}
	
	@Override
	public void sendMessage(CommandSource sender, String msg) {
		sender.sendMessage(Component.text(msg));
	}
	
	@Override
	public void sendMessage(CommandSource sender, TextComponent component) {
		sender.sendMessage(component);
	}
	
	@Override
	public boolean canBeSender(Class<?> type) {
		return CommandSource.class.isAssignableFrom(type);
	}
	
	@Override
	public boolean hasPermission(CommandSource sender, @Nullable String name) {
		return sender.hasPermission(name);
	}
	
	@Override
	public String senderName(CommandSource sender) {
		if (sender instanceof Player player) {
			return player.getUsername();
		}
		return VELOCITY_CONSOLE_SENDER_NAME;
	}
	
	
}
