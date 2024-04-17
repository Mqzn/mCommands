package io.github.mqzn.commands.test;

import io.github.mqzn.commands.annotations.AnnotationParser;
import io.github.mqzn.commands.test.annotations.TestAnnotatedCommand;
import io.github.mqzn.commands.utilities.ArgumentSyntaxUtility;
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
			"disband"
		};
		
		var cmd = commandManager.getCommand("testa");
		Assertions.assertNotNull(cmd);
		for (var syntax : cmd.syntaxes()) {
			System.out.println(ArgumentSyntaxUtility.format(commandManager, cmd.name(), syntax.getArguments()));
		}
		System.out.println("Syntaxes loaded : " + cmd.syntaxes().size());
		
		commandManager.executeCommand(cmd, sender, args);
	}
	
	@Test
	public void executionTest() {
		Assertions.assertDoesNotThrow(() -> parser.parse(new TestAnnotatedCommand()));
		
		String[] args = new String[]{
			"clear"
		};
		
		var cmd = commandManager.getCommand("testa");
		Assertions.assertNotNull(cmd);
		
		commandManager.executeCommand(cmd, sender, args);
	}
	
	
}
