package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Arg;
import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "sub1", children = TestSub2.class, aliases = "s1")
@ExecutionMeta(
	syntax = "<arg1> <arg2> <arg3>"
)
public final class TestSub1 {
	
	@Default
	public void info(ClientSender sender) {
		System.out.println("Default execution for sub1");
	}
	
	
	@SubCommandExecution
	public void execute(ClientSender sender,
	                    @Arg(id = "arg1") String arg1,
	                    @Arg(id = "arg2") String arg2,
	                    @Arg(id = "arg3") String arg3) {
		
		System.out.printf("arg1=%s, arg2=%s, arg3=%s \n", arg1, arg2, arg3);
	}
	
}
