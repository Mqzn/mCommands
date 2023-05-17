package io.github.mqzn.commands.test;

import io.github.mqzn.commands.annotations.base.Arg;
import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

@SubCommandInfo(name = "testsub")
@ExecutionMeta(syntax = "<skillname> <amount> [player]")
public class TestSubCommand {
	
	@SubCommandExecution
	public void execute(CommandSender sender,
	                    @Arg(id = "skillname") String skillName,
	                    @Arg(id = "amount") int amount,
	                    @Arg(id = "player", optional = true) @Nullable OfflinePlayer player) {
		if (player == null) {
			sender.sendMessage("PLAYER IS NULL");
			return;
		}
		
		sender.sendMessage("Skill=" + skillName + ", amount = " + amount + ", player=" + player.getName());
	}
	
	@Default
	public void defaultExec(CommandSender sender) {
		sender.sendMessage("Default execution for testsub !");
	}
	
}
