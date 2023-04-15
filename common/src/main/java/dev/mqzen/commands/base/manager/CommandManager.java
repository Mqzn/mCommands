package dev.mqzen.commands.base;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface CommandManager<S> {

	void registerCommand(String name);

	void unregisterCommand(String name);

	@Nullable Command<S> getCommand(String name);

	Set<Command<S>> getCommands();
}
