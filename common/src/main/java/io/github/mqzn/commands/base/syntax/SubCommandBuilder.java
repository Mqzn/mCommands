package io.github.mqzn.commands.base.syntax;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.base.Information;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class SubCommandBuilder<S, C> extends CommandSyntaxBuilder<S, C> {
	
	@NotNull
	private final String name;
	@NotNull
	private final LinkedHashSet<String> children = new LinkedHashSet<>();
	@Nullable
	private Aliases aliases = Aliases.of(new String[0]);
	@Nullable
	private String parent = null;
	
	@Nullable
	private CommandExecution<S, C> defaultExecution = null;
	
	protected SubCommandBuilder(@NotNull Class<C> senderClass,
	                            @NotNull String label,
	                            @NotNull String name) {
		super(senderClass, label);
		this.name = name;
	}
	
	public static <S, C> SubCommandBuilder<S, C> genericBuilder(@NotNull Class<C> senderClass,
	                                                            @NotNull String label,
	                                                            @NotNull String name) {
		return new SubCommandBuilder<>(senderClass, label, name);
	}
	
	public SubCommandBuilder<S, C> aliases(String... aliases) {
		this.aliases = Aliases.of(aliases);
		return this;
	}
	
	public SubCommandBuilder<S, C> children(String... children) {
		this.children.addAll(Arrays.asList(children));
		return this;
	}
	
	public SubCommandBuilder<S, C> defaultExecution(CommandExecution<S, C> defaultExecution) {
		this.defaultExecution = defaultExecution;
		return this;
	}
	
	@Override
	public SubCommandBuilder<S, C> info(@Nullable Information info) {
		return (SubCommandBuilder<S, C>) super.info(info);
	}
	
	@Override
	public SubCommandBuilder<S, C> flags(String... flags) {
		return (SubCommandBuilder<S, C>) super.flags(flags);
	}
	
	@Override
	public SubCommandBuilder<S, C> flags(SyntaxFlags flags) {
		return (SubCommandBuilder<S, C>) super.flags(flags);
	}
	
	@Override
	public SubCommandBuilder<S, C> senderType(@Nullable Class<C> senderClass) {
		return (SubCommandBuilder<S, C>) super.senderType(senderClass);
	}
	
	@Override
	public SubCommandBuilder<S, C> argument(@NotNull Argument<?> argument) {
		if (arguments.contains(argument)) return this;
		return (SubCommandBuilder<S, C>) super.argument(argument);
	}
	
	@Override
	public SubCommandBuilder<S, C> execute(@NotNull CommandExecution<S, C> execution) {
		return (SubCommandBuilder<S, C>) super.execute(execution);
	}
	
	public SubCommandBuilder<S, C> parent(@Nullable String parent) {
		this.parent = parent;
		return this;
	}
	
	@Override
	public SubCommandSyntax<S> build() {
		assert aliases != null;
		assert execution != null;
		assert senderClass != null;
		
		SubCommandSyntax<S> subCommandSyntax = new SubCommandSyntax<>(senderClass, commandLabel, parent, name,
			aliases, execution, flags, arguments, defaultExecution);
		
		subCommandSyntax.setInfo(info);
		
		for (var child : children)
			subCommandSyntax.addChild(child);
		
		return subCommandSyntax;
	}
	
}
