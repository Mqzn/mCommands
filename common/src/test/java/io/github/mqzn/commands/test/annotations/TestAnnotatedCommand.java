package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.*;
import io.github.mqzn.commands.base.context.CommandArgs;
import io.github.mqzn.commands.base.manager.CommandExecutionCoordinator;
import io.github.mqzn.commands.test.ClientSender;

import java.util.concurrent.TimeUnit;

@Command(name = "punish", executionType = CommandExecutionCoordinator.Type.SYNC)
@Cooldown(value = 1, unit = TimeUnit.MINUTES)
public class TestAnnotatedCommand {
	
	
	@ExecutionMeta(syntax = "<name>", senderType = ClientSender.class)
	public void execute(ClientSender sender, CommandArgs args,
	                    @Arg(id="name") String name,
	                    @Flag(name = "silent") boolean silent) {
		if(silent) {
			System.out.println("Silent running");
		}else {
			System.out.println("Silent not running");
		}
		System.out.println("Punishing " + name + " !");
	}
	
}
