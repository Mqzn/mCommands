package io.github.mqzn.commands;

import io.github.mqzn.commands.sender.SenderWrapper;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BungeeSenderWrapper implements SenderWrapper<CommandSender> {

	@NotNull
	private final BungeeAudiences audiences;

	public BungeeSenderWrapper(Plugin plugin) {
		audiences = BungeeAudiences.create(plugin);
	}

	@Override
	public Class<CommandSender> senderType() {
		return CommandSender.class;
	}

	@Override
	public boolean isConsole(CommandSender sender) {
		return !(sender instanceof ProxiedPlayer);
	}

	@Override
	public void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(net.md_5.bungee.api.chat.
						TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg)));
	}

	@Override
	public boolean canBeSender(Class<?> type) {
		return CommandSender.class.isAssignableFrom(type);
	}

	@Override
	public boolean hasPermission(CommandSender sender, @Nullable String name) {
		if (name == null || name.isEmpty())
			return true;

		return sender.hasPermission(name);
	}

	@Override
	public String senderName(CommandSender sender) {
		return sender.getName();
	}

	@Override
	public void sendMessage(CommandSender sender, TextComponent component) {
		audiences.sender(sender)
						.sendMessage(component);
	}

}
