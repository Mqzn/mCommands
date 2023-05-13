package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.Command;
import io.github.mqzn.commands.annotations.subcommands.SubCommand;

@Command(name = "testa")
@SubCommand(value = TestSub1.class)
@SubCommand(value = TestSub2.class)
@SubCommand(value = TestSub3.class)
public class TestAnnotatedCommand {


}
