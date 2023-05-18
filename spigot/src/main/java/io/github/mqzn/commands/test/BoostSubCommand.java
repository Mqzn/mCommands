package io.github.mqzn.commands.test;

import io.github.mqzn.commands.annotations.base.Arg;
import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@SubCommandInfo(name = "boost")
@ExecutionMeta(syntax = "<seconds> <multiplier> [player]")
public final class BoostSubCommand {
	
	@SubCommandExecution
	public void execute(CommandSender sender,
	                    @Arg(id = "seconds") int seconds,
	                    @Arg(id = "multiplier") double multiplier,
	                    @Arg(id = "player", optional = true) @Nullable Player player) {
		
		sender.sendMessage("seconds =" + seconds + ", multiplier = " + multiplier + ", player= " + (player == null ? "null" : player.getName()));
	}
	
	@Default
	public void defaultExec(CommandSender sender) {
		sender.sendMessage("Default execution for testsub !");
	}
	
}
