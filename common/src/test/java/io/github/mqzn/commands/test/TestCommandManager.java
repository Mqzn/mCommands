package io.github.mqzn.commands.test;

import io.github.mqzn.commands.base.manager.AbstractCommandManager;
import org.jetbrains.annotations.NotNull;

public final class TestCommandManager extends AbstractCommandManager<TestBootstrap, ClientSender> {
	
	
	public TestCommandManager(@NotNull TestBootstrap plugin) {
		super(plugin, new ClientSenderWrapper());
	}
	
	@Override
	public char commandPrefix() {
		return '/';
	}
	
}
