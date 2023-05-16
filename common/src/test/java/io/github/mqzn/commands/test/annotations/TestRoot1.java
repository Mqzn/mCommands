package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Arg;
import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "testroot1")
@ExecutionMeta(syntax = "<user>")
public class TestRoot1 {
	
	@Default
	public void defaultExecution(ClientSender sender) {
		System.out.println("DEF FOR TESTROOT1");
	}
	
	@SubCommandExecution
	public void execute(ClientSender sender, @Arg(id = "user") String user) {
		System.out.println("EXECUTING TESTROOT1, arg= " + user);
	}
	
}
