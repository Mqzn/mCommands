package io.github.mqzn.commands;

import io.github.mqzn.commands.arguments.ArgumentOfflinePlayer;
import io.github.mqzn.commands.arguments.ArgumentOnlinePlayer;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.manager.AbstractCommandManager;
import io.github.mqzn.commands.base.manager.CommandExecutionCoordinator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@ApiStatus.Internal
public final class SpigotCommandManager extends AbstractCommandManager<Plugin, CommandSender> {
	
	
	@NotNull
	private final Plugin plugin;
	
	@NotNull
	private final SimpleCommandMap cmdMap;
	
	public SpigotCommandManager(@NotNull Plugin plugin, @NotNull CommandExecutionCoordinator.Type coordinator) {
		super(plugin, new SpigotSenderWrapper(plugin), coordinator);
		this.plugin = plugin;
		try {
			Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMap.setAccessible(true);
			cmdMap = (SimpleCommandMap) commandMap.get(Bukkit.getServer());
			
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		this.registerCaptions();
		this.registerTypes();
	}
	
	
	public SpigotCommandManager(@NotNull Plugin plugin) {
		this(plugin, CommandExecutionCoordinator.Type.SYNC);
	}
	
	
	@Override
	public @NotNull Plugin getBootstrap() {
		return plugin;
	}
	
	@Override
	public char commandStarter() {
		return '/';
	}
	
	@Override
	public <C extends Command<CommandSender>> void registerCommand(C command) {
		super.registerCommand(command);
		cmdMap.register(command.name(), new InternalSpigotCommand(this, command));
	}
	
	private void registerCaptions() {
		captionRegistry.registerCaption(SpigotCaption.INVALID_ARGUMENT);
		captionRegistry.registerCaption(SpigotCaption.UNKNOWN_COMMAND);
		captionRegistry.registerCaption(SpigotCaption.NO_PERMISSION);
		captionRegistry.registerCaption(SpigotCaption.ONLY_PLAYER_EXECUTABLE);
		captionRegistry.registerCaption(SpigotCaption.NO_HELP_TOPIC_AVAILABLE);
	}
	
	
	private void registerTypes() {
		typeRegistry().registerArgumentConverter(OfflinePlayer.class, ArgumentOfflinePlayer::new);
		typeRegistry().registerArgumentConverter(Player.class, ArgumentOnlinePlayer::new);
	}
	
}
