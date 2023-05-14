package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.Arg;
import io.github.mqzn.commands.annotations.Default;
import io.github.mqzn.commands.annotations.Syntax;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "sub3", parent = TestSub2.class)
@Syntax(syntax = "<address>")
public class TestSub3 {
	
	@Default
	public void defaultExecution(ClientSender sender) {
		
		System.out.println("Default execution for sub 3");
		
	}
	
	@SubCommandExecution
	public void execute(ClientSender sender,
	                    @Arg(id = "username") String username,
	                    @Arg(id = "num") int num,
	                    @Arg(id = "address") String address) {
		System.out.printf("Executing, username = %s, num=%s, address=%s \n", username, num, address);
	}
	
}
