package io.github.mqzn.commands.test;

import io.github.mqzn.commands.annotations.AnnotationParser;
import io.github.mqzn.commands.test.annotations.TestAnnotatedCommand;
import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.regex.Pattern;

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
		
		Assertions.assertThrows(IllegalStateException.class, ()-> parser.parse(new TestAnnotatedCommand()));
		
		String[] args = new String[]{
			"disband"
		};
		
		var cmd = commandManager.getCommand("testa");
		Assertions.assertNull(cmd);
		
		//commandManager.executeCommand(cmd, sender, args);
	}
	
	@Test
	public void executionTest() {
		Assertions.assertDoesNotThrow(()-> parser.parse(new TestAnnotatedCommand()));
		
		String[] args = new String[]{
			"disband"
		};
		
		var cmd = commandManager.getCommand("testa");
		Assertions.assertNotNull(cmd);
		
		commandManager.executeCommand(cmd, sender, args);
	}
	
	@Test
	public void syntaxParsingTest() {
		
		String syntax = "disband ";
		
		String[] split = syntax.split(Pattern.quote(" "));
		System.out.println(Arrays.toString(split));
	}
	
	
}
