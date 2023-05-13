package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.Arg;
import io.github.mqzn.commands.annotations.Syntax;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;

@SubCommandInfo(name = "sub1", children = TestSub2.class, aliases = "s1")
@Syntax(syntax = "<username>")
public final class TestSub1 {

	@SubCommandExecution
	public void execute(ClientSender sender,
	                    @Arg(id = "username") String username) {
		System.out.println("Executing sub 1 for user '" + username + "'");
	}

}
