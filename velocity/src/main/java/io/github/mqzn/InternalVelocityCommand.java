package io.github.mqzn;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import io.github.mqzn.commands.base.Command;

import java.util.List;

class InternalVelocityCommand implements SimpleCommand {
	private final VelocityCommandManager commandManager;
	private final Command<CommandSource> command;

	InternalVelocityCommand(VelocityCommandManager commandManager, Command<CommandSource> command) {

		this.commandManager = commandManager;
		this.command = command;
	}

	/**
	 * Executes the command for the specified invocation.
	 *
	 * @param invocation the invocation context
	 */
	@Override
	public void execute(Invocation invocation) {
		String[] args = invocation.arguments();
		CommandSource sender = invocation.source();

		commandManager.executeCommand(command, sender, args);
	}

	/**
	 * Provides tab complete suggestions for the specified invocation.
	 *
	 * @param invocation the invocation context
	 * @return the tab complete suggestions
	 */
	@Override
	public List<String> suggest(Invocation invocation) {
		return commandManager.suggest(command, invocation.source(), invocation.arguments());
	}


}
