package io.github.mqzn.commands.test;

import io.github.mqzn.commands.annotations.parser.AnnotationParser;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import io.github.mqzn.commands.test.annotations.TestAnnotatedCommand;
import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@TestOnly
public final class TestBootstrap {

	private final TestCommandManager commandManager;

	private final AnnotationParser<ClientSender> parser;
	private final ClientSender sender = new ClientSender("mqzen");

	public TestBootstrap() {
		commandManager = new TestCommandManager(this);
		commandManager.senderProviderRegistry().registerSenderProvider(ClientSender.class, (provider) -> provider);
		parser = new AnnotationParser<>(commandManager);
	}

	@Test
	public void firstTest() {


		/*var sub1 = SubCommandBuilder.<ClientSender, ClientSender>genericBuilder(ClientSender.class,"test","sub1")
						.argument(Argument.word("username"))
						.children("sub2")
						.execute((sender, context)-> {
							System.out.println("Executing context for sub1 !");
						})
						.build();


		var sub2 = SubCommandBuilder.<ClientSender, ClientSender>genericBuilder(ClientSender.class,"test","sub2")
						.argument(Argument.integer("num"))
						.parent("sub1")
						.children("sub3")
						.execute((sender, context)-> {
							System.out.println("Executing context for sub2 !");
						})
						.build();

		var sub3 = SubCommandBuilder.<ClientSender, ClientSender>genericBuilder(ClientSender.class,"test","sub3")
						.argument(Argument.word("address"))
						.parent("sub2")
						.execute((sender, context)-> {
							System.out.println("Executing context for sub3 !");
						})
						.build();

		var cmd = Command.builder(commandManager, "test")
						.syntax(sub1, sub2, sub3)
						.build();

		commandManager.registerCommand(cmd);*/
		parser.parse(new TestAnnotatedCommand());

		// /test sub1
		String[] args1 = new String[]{
						"sub1",
						"mqzen",
						"sub2",
						"1",
						"sub3",
						"egypt"
		};
		var cmd = commandManager.getCommand("testa");
		Assertions.assertNotNull(cmd);

		commandManager.executeCommand(cmd, sender, args1);

		for (var syntax : cmd.syntaxes()) {
			if (!(syntax instanceof SubCommandSyntax<ClientSender> subCmd)) continue;

			StringBuilder builder = new StringBuilder();
			for (var arg : subCmd.getArguments()) {
				builder.append(arg.id()).append(" ");
			}

			System.out.println("Arguments: " + builder);
		}

	}


}
