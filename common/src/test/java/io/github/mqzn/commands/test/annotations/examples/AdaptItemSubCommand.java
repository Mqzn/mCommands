package io.github.mqzn.commands.test.annotations.examples;


import io.github.mqzn.commands.annotations.Syntax;
import io.github.mqzn.commands.annotations.subcommands.SubCommandInfo;

@SubCommandInfo(name = "item", children = AdaptItemExperienceSubCommand.class)
@Syntax(syntax = "")
public final class AdaptItemSubCommand {

}
