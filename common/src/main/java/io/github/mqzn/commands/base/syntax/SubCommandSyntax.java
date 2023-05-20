package io.github.mqzn.commands.base.syntax;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.base.context.CommandContext;
import io.github.mqzn.commands.base.context.DelegateCommandContext;
import io.github.mqzn.commands.base.manager.CommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public final class SubCommandSyntax<S> extends CommandSyntax<S> {
	
	@NotNull
	private final String name;
	
	@NotNull
	private final CommandAliases commandAliases;
	
	@NotNull
	private final LinkedHashSet<String> children = new LinkedHashSet<>();
	
	@Nullable
	private final CommandExecution<S, ?> defaultExecution;
	
	@Nullable
	private String parent;
	
	@NotNull
	private List<Argument<?>> parentArguments = new ArrayList<>();
	
	<C> SubCommandSyntax(@NotNull Class<C> senderClass,
	                 @NotNull String commandLabel,
	                 @Nullable String parent,
	                 @NotNull String name,
	                 @NotNull CommandAliases commandAliases,
	                 @Nullable CommandExecution<S, C> execution,
	                 @NotNull SyntaxFlags flags,
	                 @NotNull List<Argument<?>> arguments,
	                 @Nullable CommandExecution<S, C> defaultExecution) {
		
		super(senderClass, commandLabel, execution, flags, arguments);
		this.name = name;
		this.parent = parent;
		this.commandAliases = commandAliases;
		this.defaultExecution = defaultExecution;
	}
	
	public void setParentArguments(@NotNull List<Argument<?>> parentArguments) {
		this.parentArguments = parentArguments;
	}
	
	
	public void addChild(SubCommandSyntax<S> subCommand) {
		addChild(subCommand.getName());
	}
	
	public void addChild(String subCommand) {
		children.add(subCommand);
	}
	
	public boolean hasChild(String name) {
		return children.contains(name);
	}
	
	public boolean hasChild(SubCommandSyntax<S> subCommandSyntax) {
		return hasChild(subCommandSyntax.getName());
	}
	
	public void removeChild(SubCommandSyntax<S> subCommandSyntax) {
		removeChild(subCommandSyntax.getName());
	}
	
	public void removeChild(String name) {
		children.remove(name);
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	public boolean isOrphan() {
		return parent == null;
	}
	
	@SuppressWarnings("unchecked")
	public <C> void defaultExecution(C sender, DelegateCommandContext<S> context) {
		if (defaultExecution == null) return;
		((CommandExecution<S, C>) defaultExecution).execute(sender, context);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <C> void execute(C sender, CommandContext<S> commandContext) {
		if (execution == null) {
			if (defaultExecution == null) {
				throw new IllegalStateException(
					String.format("Failed to execute subcommand `%s`, as it doesn't have an execution or even a default execution", name)
				);
			}
			
			((CommandExecution<S, C>) defaultExecution).execute(sender, commandContext);
		} else {
			((CommandExecution<S, C>) execution).execute(sender, commandContext);
		}
		
	}
	
	public boolean matches(String rawArgument) {
		return this.name.equalsIgnoreCase(rawArgument) || CommandSyntax.aliasesIncludes(commandAliases, rawArgument);
	}

	@Override
	public String toString() {
		return name;
	}
	
	public @NotNull String getName() {
		return name;
	}
	
	public @NotNull LinkedHashSet<String> getChildren() {
		return children;
	}
	
	public @Nullable String getParent() {
		return parent;
	}
	
	public void setParent(@Nullable String parentName) {
		this.parent = parentName;
	}
	
	public @NotNull CommandAliases getAliases() {
		return commandAliases;
	}
	
	public @NotNull List<Argument<?>> getCachedParentArguments() {
		return parentArguments;
	}
	
	@Override
	public @NotNull TextComponent toText(@NotNull CommandManager<?, S> manager, @NotNull S sender) {
		return Component.text(format(manager, commandLabel, parentArguments));
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SubCommandSyntax<?> that)) return false;
		if (!super.equals(o)) return false;
		
		return Objects.equals(name, that.name) && arguments.equals(that.arguments);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parent, name);
	}
	
}
