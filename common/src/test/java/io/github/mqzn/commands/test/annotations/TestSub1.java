package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Arg;
import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "sub1", aliases = "s1")
@ExecutionMeta(
	syntax = "<arg1>"
)
public final class TestSub1 {
	
	@Default
	public void info(ClientSender sender) {
		System.out.println("Default execution for sub1");
	}
	
	
	@SubCommandExecution
	public void execute(ClientSender sender,
	                    @Arg(id = "arg1") String arg1) {
		
		System.out.printf("arg1=%s \n", arg1);
	}
	
}
