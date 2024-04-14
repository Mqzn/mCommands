package io.github.mqzn.commands.test.buggyexamples;

import io.github.mqzn.commands.annotations.base.Arg;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.base.Greedy;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.ClientSender;
import org.jetbrains.annotations.NotNull;

@ExecutionMeta(syntax = "<str> <str2>")
@SubCommandInfo(name = "greedy")
public class GreedyChildSub {
	
	@SubCommandExecution
	public void execute(@NotNull ClientSender sender,
	                    @Arg(id = "str") @Greedy String str,
	                    @Arg(id = "str2") @Greedy String str2) {
		sender.sendMessage("child extremely greedy arg -> " + str + str2);
	}
}
