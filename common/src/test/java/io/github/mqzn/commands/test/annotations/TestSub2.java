package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "sub2", aliases = "s2", parent = TestSub1.class, children = TestSub3.class)
@ExecutionMeta()
public final class TestSub2 {
	
	@Default
	public void defaultExecution(ClientSender sender) {
		
		System.out.println("Default execution for sub 2");
		
	}
	
}
