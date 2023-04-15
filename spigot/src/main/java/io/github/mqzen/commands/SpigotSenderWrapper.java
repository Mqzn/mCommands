package io.github.mqzen.commands;

import io.github.mqzen.commands.sender.SenderWrapper;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpigotSenderWrapper implements SenderWrapper<CommandSender> {

	@NotNull
	@Getter
	private final BukkitAudiences audience;


	public SpigotSenderWrapper(Plugin plugin) {
		audience = BukkitAudiences.create(plugin);
	}

	@Override
	public Class<CommandSender> senderType() {
		return CommandSender.class;
	}

	@Override
	public boolean isConsole(CommandSender sender) {
		return !(sender instanceof Player);
	}

	@Override
	public void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	@Override
	public void sendMessage(CommandSender sender, TextComponent component) {
		audience.sender(sender)
						.sendMessage(component);
	}

	@Override
	public boolean canBeSender(Class<?> type) {
		return CommandSender.class.isAssignableFrom(type);
	}

	@Override
	public boolean hasPermission(CommandSender sender, @Nullable String name) {
		if (name == null || name.isEmpty()) return true;
		return sender.hasPermission(name);
	}

	@Override
	public String senderName(CommandSender sender) {
		return sender.getName();
	}

}
