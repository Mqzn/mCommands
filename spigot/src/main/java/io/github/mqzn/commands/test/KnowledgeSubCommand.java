package io.github.mqzn.commands.test;

import io.github.mqzn.commands.annotations.base.*;
import io.github.mqzn.commands.annotations.subcommands.SubCommandExecution;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@SubCommandInfo(name = "knowledge")
@ExecutionMeta(syntax = "<skillname> <amount> [player]")
public class KnowledgeSubCommand {
	
	@SubCommandExecution
	public void execute(CommandSender sender,
	                    @Arg(id = "skillname") @Suggest(provider = SkillNameSuggestionProvider.class) String skillName,
	                    @Arg(id = "amount") @Range(min = "1", max = "15") int amount,
	                    @Arg(id = "player", optional = true) @Nullable Player player) {
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
