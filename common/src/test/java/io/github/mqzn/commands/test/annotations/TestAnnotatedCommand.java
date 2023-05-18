package io.github.mqzn.commands.test.annotations;

import io.github.mqzn.commands.annotations.base.Command;
import io.github.mqzn.commands.annotations.base.CommandsGroup;
import io.github.mqzn.commands.annotations.base.Default;
import io.github.mqzn.commands.annotations.subcommands.SubCommand;
import io.github.mqzn.commands.test.ClientSender;

@CommandsGroup
@Command(name = "testa")
@SubCommand(value = TestSub1.class)
public class TestAnnotatedCommand {

	
	@CommandsGroup
	@Command(name = "testinner1")
	public static class TestInnerCommand {
		
		@Default
		public void exec(ClientSender sender) {
			System.out.println("Executing default for " + this.getClass().getSimpleName());
		}
		
		@CommandsGroup
		@Command(name = "testinner2")
		public static class TestInner2Command {
			
			@Default
			public void exec(ClientSender sender) {
				System.out.println("Executing default for " + this.getClass().getSimpleName());
			}
			
			@Command(name = "testinner3")
			public static class TestInner3Command {
				
				@Default
				public void exec(ClientSender sender) {
					System.out.println("Executing default for " + this.getClass().getSimpleName());
				}
			}
			
		}
		
	}
	

}
