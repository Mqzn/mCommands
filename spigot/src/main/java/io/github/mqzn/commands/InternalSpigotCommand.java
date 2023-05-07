package io.github.mqzn.commands;

import io.github.mqzn.commands.base.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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
	public boolean execute(CommandSender sender,
	                       String label,
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
	public Plugin getPlugin() {
		return manager.getBootstrap();
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		System.out.println("Arguments: " + Arrays.toString(args));
		return manager.suggest(command, sender, args);
	}

}
