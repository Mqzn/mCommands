package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Command;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommand;
import io.github.mqzn.commands.test.ClientSender;

@Command(name = "testa")
@SubCommand(value = TestSub1.class)
public class TestAnnotatedCommand {
	
	
	@ExecutionMeta(syntax = "clear")
	public void clear(ClientSender sender) {
		System.out.println("Clears shit");
	}
	
	
	@ExecutionMeta(syntax = "disband")
	public void disband(ClientSender sender) {
		System.out.println("Disbands shit !");
	}


}
