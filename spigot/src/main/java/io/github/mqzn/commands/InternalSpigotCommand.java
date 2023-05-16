package io.github.mqzn.commands;

import io.github.mqzn.commands.base.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@ApiStatus.Internal
final class InternalSpigotCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {
	
	@NotNull
	private final SpigotCommandManager manager;
	
	@NotNull
	private final Command<CommandSender> command;
	
	
	InternalSpigotCommand(@NotNull SpigotCommandManager manager,
	                      @NotNull Command<CommandSender> command) {
		super(command.name(), command.info().description(), "",
			Arrays.asList(command.info().aliases()));
		this.manager = manager;
		this.command = command;
	}
	
	@Override
	public boolean execute(@NotNull CommandSender sender,
	                       @NotNull String label,
	                       String[] raw) {
		
		try {
			manager.executeCommand(command, sender, raw);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
	}
	
	@Override
	public @NotNull Plugin getPlugin() {
		return manager.getBootstrap();
	}
	
	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender,
	                                         @NotNull String alias,
	                                         String[] args) throws IllegalArgumentException {
		return manager.suggest(command, sender, args);
	}
	
}
