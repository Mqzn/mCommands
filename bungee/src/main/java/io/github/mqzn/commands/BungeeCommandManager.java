package io.github.mqzn.commands;

import io.github.mqzn.commands.arguments.ArgumentOnlinePlayer;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.manager.AbstractCommandManager;
import io.github.mqzn.commands.base.manager.CommandExecutionCoordinator;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class BungeeCommandManager extends AbstractCommandManager<Plugin, CommandSender> {
	
	
	public BungeeCommandManager(@NotNull Plugin plugin,
	                            @NotNull CommandExecutionCoordinator.@NotNull Type coordinator) {
		super(plugin, new BungeeSenderWrapper(plugin), coordinator);
		
		captionRegistry.registerCaption(BungeeCaption.UNKNOWN_COMMAND);
		captionRegistry.registerCaption(BungeeCaption.INVALID_ARGUMENT);
		captionRegistry.registerCaption(BungeeCaption.NO_PERMISSION);
		captionRegistry.registerCaption(BungeeCaption.ONLY_PLAYER_EXECUTABLE);
		captionRegistry.registerCaption(BungeeCaption.NO_HELP_TOPIC_AVAILABLE);
		
		typeRegistry().registerArgumentConverter(ProxiedPlayer.class, ArgumentOnlinePlayer::new);
	}
	
	public BungeeCommandManager(@NotNull Plugin plugin) {
		this(plugin, CommandExecutionCoordinator.Type.SYNC);
	}
	
	@Override
	public char commandPrefix() {
		return '/';
	}
	
	@Override
	public <C extends Command<CommandSender>> void registerCommand(C command) {
		super.registerCommand(command);
		bootstrap.getProxy().getPluginManager().registerCommand(bootstrap, new InternalBungeeCommand(this, command));
	}
	
	
}
