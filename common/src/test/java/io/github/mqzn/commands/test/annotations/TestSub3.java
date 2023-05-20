package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "sub3", parent = TestSub2.class)
@ExecutionMeta()
public class TestSub3 {
	
	@Default
	public void defaultExecution(ClientSender sender) {
		
		System.out.println("Default execution for sub 3");
		
	}
	
}
