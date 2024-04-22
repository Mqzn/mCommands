package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Command;
import io.github.mqzn.commands.annotations.base.Cooldown;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.base.context.CommandArgs;
import io.github.mqzn.commands.base.manager.CommandExecutionCoordinator;
import io.github.mqzn.commands.test.ClientSender;
import io.github.mqzn.commands.test.CustomException;

import java.util.concurrent.TimeUnit;

@Command(name = "testa", executionType = CommandExecutionCoordinator.Type.ASYNC)
@Cooldown(value = 1, unit = TimeUnit.MINUTES)
public class TestAnnotatedCommand {
	
	
	@ExecutionMeta(syntax = "clear", senderType = ClientSender.class)
	public void clear(ClientSender sender, CommandArgs args) {
		System.out.println("Clears shit");
	}
	
	
	@ExecutionMeta(syntax = "disband", senderType = ClientSender.class)
	public void disband(ClientSender sender, CommandArgs args) {
		System.out.println("Disbands shit !");
	}
	
	@ExecutionMeta(syntax = "exceptionsub", senderType = ClientSender.class)
	public void exceptionSub(ClientSender sender, CommandArgs args) throws CustomException {
		System.out.println("Executing exception sub !");
		throw new CustomException("Custom exception message");
	}
	
}
