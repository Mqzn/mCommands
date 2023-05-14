package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.Arg;
import io.github.mqzn.commands.annotations.Default;
import io.github.mqzn.commands.annotations.Syntax;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "sub2", aliases = "s2", parent = TestSub1.class, children = TestSub3.class)
@Syntax(syntax = "<num>")
public final class TestSub2 {
	
	@Default
	public void defaultExecution(ClientSender sender) {
		
		System.out.println("Default execution for sub 2");
		
	}
	
	@SubCommandExecution
	public void execute(ClientSender sender,
	                    @Arg(id = "username") String userName,
	                    @Arg(id = "num") int num) {
		System.out.println("Executing sub 2 using user = " + userName + ", number= '" + num + "'");
	}
	
}
