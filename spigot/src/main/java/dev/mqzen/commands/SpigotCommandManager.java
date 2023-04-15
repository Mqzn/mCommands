package dev.mqzen.commands;

import dev.mqzen.commands.base.Command;
import dev.mqzen.commands.base.manager.AbstractCommandManager;
import dev.mqzen.commands.base.manager.CommandExecutionCoordinator;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public final class SpigotCommandManager extends AbstractCommandManager<Plugin, CommandSender> {


	@NotNull
	private final Plugin plugin;

	@NotNull
	private final SimpleCommandMap cmdMap;

	public SpigotCommandManager(@NotNull Plugin plugin, @NotNull CommandExecutionCoordinator.Type coordinator) {
		super(plugin, new SpigotSenderWrapper(plugin), coordinator);
		this.plugin = plugin;
		try {
			Field commandMap = MinecraftServer.getServer().server.getClass().getDeclaredField("commandMap");
			commandMap.setAccessible(true);
			cmdMap = (SimpleCommandMap) commandMap.get(Bukkit.getServer());

		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}


		captionRegistry.registerCaption(SpigotCaption.INVALID_ARGUMENT);
		captionRegistry.registerCaption(SpigotCaption.UNKNOWN_COMMAND);
		captionRegistry.registerCaption(SpigotCaption.NO_PERMISSION);
		captionRegistry.registerCaption(SpigotCaption.ONLY_PLAYER_EXECUTABLE);
		captionRegistry.registerCaption(SpigotCaption.NO_HELP_TOPIC_AVAILABLE);
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
		cmdMap.register(command.name(), new SpigotCommand(this, command));
	}


	public void debugCommandSyntaxes(String cmd) {
		var command = getCommand(cmd);
		if (command == null) return;

		log("%s's syntaxes : ", cmd);
		for (var syntax : command.syntaxes()) {
			log("- %s", syntax.formatted());
		}

	}

}
