package io.github.mqzn.commands.test.buggyexamples;

import io.github.mqzn.commands.annotations.base.Command;
import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.subcommands.SubCommand;
import io.github.mqzn.commands.test.ClientSender;
import org.jetbrains.annotations.NotNull;

@Command(name = "parent")
@SubCommand(ChildCmd.class)
@SubCommand(GreedyChildSub.class)
public class ParentCmd {
    @Default
    public void defaultExecution(@NotNull ClientSender sender) {
        sender.sendMessage("hi this is the parent");
    }
}