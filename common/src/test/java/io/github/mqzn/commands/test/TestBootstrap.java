package io.github.mqzn.commands.test;

import io.github.mqzn.commands.annotations.AnnotationParser;
import io.github.mqzn.commands.base.manager.flags.FlagInfo;
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
		commandManager.flagRegistry().registerFlag(FlagInfo.builder("silent")
			.aliases("s").build());
		commandManager.exceptionHandler().registerCallback(CustomException.class, (exception, commandSender, context) -> System.out.println("Handling exception: " + exception.getClass().getName()));
		//commandManager.senderProviderRegistry().registerSenderProvider(ClientSender.class, (provider) -> provider);
		parser = new AnnotationParser<>(commandManager);
	}
	
	@Test
	public void firstTest() {
		
		parser.parse(new TestAnnotatedCommand());
		String[] args = new String[]{
			"mqzen",
			"-s"
		};
		
		var cmd = commandManager.getCommand("punish");
		Assertions.assertNotNull(cmd);
		commandManager.executeCommand(cmd, sender, args);
	}
	
	@Test
	public void testCooldown() {
		parser.parse(new TestAnnotatedCommand());
		String[] args = new String[]{
			"disband"
		};
		
		var cmd = commandManager.getCommand("testa");
		Assertions.assertNotNull(cmd);
		
		commandManager.executeCommand(cmd, sender, args);
		commandManager.executeCommand(cmd, sender, args);
	}
	
	@Test
	public void customExceptionHandlingTest() {
		
		
		parser.parse(new TestAnnotatedCommand());
		String[] args = new String[]{
			"exceptionsub"
		};
		var cmd = commandManager.getCommand("testa");
		Assertions.assertNotNull(cmd);
		Assertions.assertDoesNotThrow(() -> commandManager.executeCommand(cmd, sender, args));
	}
	
	@Test
	public void executionTest() {
		Assertions.assertDoesNotThrow(() -> parser.parse(new TestAnnotatedCommand()));
		
		String[] args = new String[]{
			"execute"
		};
		
		var cmd = commandManager.getCommand("testa");
		Assertions.assertNotNull(cmd);
		
		commandManager.executeCommand(cmd, sender, args);
	}
	
	
}
