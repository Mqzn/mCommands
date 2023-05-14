package io.github.mqzn.commands;

import io.github.mqzn.commands.base.Command;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

final class InternalBungeeCommand extends net.md_5.bungee.api.plugin.Command implements TabExecutor {
	
	@NotNull
	private final BungeeCommandManager manager;
	
	@NotNull
	private final Command<CommandSender> command;
	
	public InternalBungeeCommand(@NotNull BungeeCommandManager manager, @NotNull Command<CommandSender> command) {
		super(command.name(), command.info().permission(), command.info().aliases());
		this.manager = manager;
		this.command = command;
	}
	
	@Override
	public void execute(CommandSender commandSender, String[] args) {
		manager.executeCommand(command, commandSender, args);
	}
	
	
	@Override
	public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
		return manager.suggest(command, commandSender, args);
	}
	
	
}
