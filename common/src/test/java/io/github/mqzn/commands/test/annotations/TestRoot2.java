package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Arg;
import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "testroot2")
@ExecutionMeta(syntax = "<num>")
public class TestRoot2 {
	
	@Default
	public void defaultExecution(ClientSender sender) {
		System.out.println("DEF FOR TESTROOT2");
	}
	
	@SubCommandExecution
	public void execute(ClientSender sender, @Arg(id = "num") int num) {
		System.out.println("EXECUTING TESTROOT2 , arg= " + num);
	}
	
}


