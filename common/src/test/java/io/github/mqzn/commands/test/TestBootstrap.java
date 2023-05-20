package io.github.mqzn.commands.test;

import io.github.mqzn.commands.annotations.AnnotationParser;
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
		//commandManager.senderProviderRegistry().registerSenderProvider(ClientSender.class, (provider) -> provider);
		parser = new AnnotationParser<>(commandManager);
	}
	
	@Test
	public void firstTest() {
		
		parser.parse(new TestAnnotatedCommand());
		
		String[] args = new String[]{
			"sub1",
			"a1",
			"a2",
			"a3",
			"sub2"
		};
		
		var cmd = commandManager.getCommand("testa");
		Assertions.assertNotNull(cmd);
		
		commandManager.executeCommand(cmd, sender, args);
	}
	
	
}
