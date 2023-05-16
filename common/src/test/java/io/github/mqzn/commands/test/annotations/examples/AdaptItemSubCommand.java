package io.github.mqzn.commands.test.annotations.examples;


import io.github.mqzn.commands.annotations.base.ExecutionMeta;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;

@SubCommandInfo(name = "item", children = AdaptItemExperienceSubCommand.class)
@ExecutionMeta()
public final class AdaptItemSubCommand {

}
