package io.github.mqzn.commands.test.annotations.examples;

import io.github.mqzn.commands.annotations.Syntax;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;

@SubCommandInfo(name = "experience", parent = AdaptItemSubCommand.class)
@Syntax(syntax = "<skillName> <amount> [player]")
public final class AdaptItemExperienceSubCommand {

	/*@SubCommandExecution
	public void execute(CommandSender sender,
	                    @Arg(id= "skillamount") @Suggest({"Agility", "Herbalism", "[all]", "[random]"}) String skillName,
	                    @Arg(id= "amount") int amount,
	                    @Arg(id= "player", optional = true) @Nullable Player player) {

		for (Skill<?> skill : SkillRegistry.skills.sortV()) {
			if (item.equals(skill.getName())) {
				FConst.success("Giving " + skill.getName() + " orb").send(sender);
				if (sender instanceof Player p) {
					giveExpOrb(p, skill, xp);
				} else {
					FConst.error("You must be a player to use this command, or you can include a player argument!").send(sender);
				}
				return;
			}
		}

	}*/
	
	
}
