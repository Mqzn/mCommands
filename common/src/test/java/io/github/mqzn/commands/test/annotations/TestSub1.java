package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "sub1", children = TestSub2.class, aliases = "s1")
@ExecutionMeta()
public final class TestSub1 {
	
	@Default
	public void info(ClientSender sender) {
		System.out.println("Default shit");
	}
	
	
}
