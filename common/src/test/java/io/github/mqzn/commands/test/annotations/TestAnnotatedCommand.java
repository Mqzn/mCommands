package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Command;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.base.context.CommandArgs;
import io.github.mqzn.commands.base.manager.CommandExecutionCoordinator;
import io.github.mqzn.commands.test.ClientSender;

@Command(name = "testa", executionType = CommandExecutionCoordinator.Type.ASYNC)
public class TestAnnotatedCommand {
	
	
	@ExecutionMeta(syntax = "clear", senderType = ClientSender.class)
	public void clear(ClientSender sender, CommandArgs args) {
		System.out.println("Clears shit");
	}
	
	
	@ExecutionMeta(syntax = "disband", senderType = ClientSender.class)
	public void disband(ClientSender sender, CommandArgs args) {
		System.out.println("Disbands shit !");
	}
	
	
}
