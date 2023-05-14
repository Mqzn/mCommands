package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.Arg;
import io.github.mqzn.commands.annotations.Default;
import io.github.mqzn.commands.annotations.Syntax;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "testroot2")
@Syntax(syntax = "<num>")
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


