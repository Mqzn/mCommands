package io.github.mqzn.commands.test;

import io.github.mqzn.commands.annotations.base.Arg;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import io.github.mqzn.commands.test.subs.PAPSubCommand;
import org.bukkit.command.CommandSender;

@SubCommandInfo(
	name = "childpap",
	parent = PAPSubCommand.class
)
@ExecutionMeta(syntax = "<arg1>")
public class ChildPAPSub {
	
	@SubCommandExecution
	public void execute(CommandSender sender,
	                    @Arg(id = "arg1") int arg1) {
		sender.sendMessage("Arg1 = " + arg1);
	}
	
}
