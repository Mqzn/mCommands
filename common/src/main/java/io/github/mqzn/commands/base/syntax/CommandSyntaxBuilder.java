package io.github.mqzn.commands.base.syntax;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.base.Information;
import io.github.mqzn.commands.base.manager.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandSyntaxBuilder<S, C> {
	@NotNull
	protected final CommandManager<?, S> manager;
	
	@NotNull
	protected final String commandLabel;
	
	@NotNull
	protected final List<Argument<?>> arguments = new ArrayList<>();
	
	@Nullable
	protected Class<C> senderClass;
	
	@Nullable
	protected CommandExecution<S, C> execution;
	
	@NotNull
	protected SyntaxFlags flags = SyntaxFlags.of();
	
	@Nullable
	protected Information info = null;
	
	protected CommandSyntaxBuilder(@NotNull CommandManager<?, S> manager,
	                               @NotNull Class<C> senderClass,
	                               @NotNull String label) {
		this.manager = manager;
		this.senderClass = senderClass;
		this.commandLabel = label;
	}
	
	public static <S, C> CommandSyntaxBuilder<S, C> genericBuilder(@NotNull CommandManager<?, S> manager,
	                                                               @NotNull Class<C> senderClass,
	                                                               @NotNull String label) {
		return new CommandSyntaxBuilder<>(manager, senderClass, label);
	}
	
	public CommandSyntaxBuilder<S, C> info(@Nullable Information info) {
		this.info = info;
		return this;
	}
	
	public CommandSyntaxBuilder<S, C> flags(String... flags) {
		this.flags = SyntaxFlags.of(flags);
		return this;
	}
	
	public CommandSyntaxBuilder<S, C> flags(SyntaxFlags flags) {
		this.flags = flags;
		return this;
	}
	
	public CommandSyntaxBuilder<S, C> senderType(@Nullable Class<C> senderClass) {
		this.senderClass = senderClass;
		return this;
	}
	
	public CommandSyntaxBuilder<S, C> argument(@NotNull Argument<?> argument) {
		if (!arguments.contains(argument)) {
			arguments.add(argument);
		}
		return this;
	}
	
	public CommandSyntaxBuilder<S, C> execute(@NotNull CommandExecution<S, C> execution) {
		this.execution = execution;
		return this;
	}
	
	public CommandSyntax<S> build() {
		assert senderClass != null;
		CommandSyntax<S> syntax = new CommandSyntax<>(manager, senderClass, commandLabel,
			execution == null ? (s, c) -> {
			} : execution, flags, arguments);
		syntax.setInfo(info);
		return syntax;
	}
	
}
