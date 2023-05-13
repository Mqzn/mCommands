package io.github.mqzn.commands.base.syntax;

import io.github.mqzn.commands.arguments.Argument;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public final class SubCommandSyntax<S> extends CommandSyntax<S> {

	@NotNull
	@Getter
	private final String name;
	@NotNull
	@Getter
	private final Aliases aliases;
	@Getter
	private final LinkedHashSet<String> children = new LinkedHashSet<>();
	@Nullable
	@Getter
	private String parent;

	<C> SubCommandSyntax(@NotNull Class<C> senderClass,
	                     @NotNull String commandLabel,
	                     @Nullable String parent,
	                     @NotNull String name,
	                     @NotNull Aliases aliases,
	                     @NotNull CommandExecution<S, C> execution,
	                     @NotNull SyntaxFlags flags,
	                     @NotNull List<Argument<?>> arguments) {

		super(senderClass, commandLabel, execution, flags, arguments);
		this.name = name;
		this.parent = parent;
		this.aliases = aliases;
	}

	public void setParent(@Nullable String parentName) {
		this.parent = parentName;
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

	@Override
	public String toString() {
		return name;
	}
}
