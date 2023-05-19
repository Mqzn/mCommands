package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.base.syntax.CommandAliases;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArgumentLiteral extends AbstractArgument<String> {
	
	@NotNull
	private CommandAliases commandAliases = CommandAliases.of();
	
	ArgumentLiteral(String id) {
		super(id, String.class);
		suggestions.add(id);
	}
	
	ArgumentLiteral(ArgumentData data) {
		super(data, String.class);
		suggestions.add(data.getId());
	}
	
	@Override
	public String parse(@NotNull String command, @NotNull String input) {
		return input;
	}
	
	@Override
	public @Nullable String defaultValue() {
		return id();
	}
	
	@Override
	public @NotNull Argument<String> setDefaultValue(@Nullable String value) {
		throw new UnsupportedOperationException("Argument is a literal, it cannot have a default value !");
	}
	
	@Override
	public boolean isOptional() {
		return false;
	}
	
	public ArgumentLiteral aliases(String... aliases) {
		this.commandAliases = CommandAliases.of(aliases);
		for (String aliase : aliases) {
			if (!suggestions.contains(aliase)) {
				suggestions.add(aliase);
			}
		}
		return this;
	}
	
	@Override
	public Class<?>[] alternativeTypes() {
		return super.alternativeTypes();
	}
	
	@Override
	public Argument<String> suggest(@NotNull String suggestion) {
		throw new UnsupportedOperationException("You cannot do that for a literal argument");
	}
	
	public @NotNull CommandAliases getAliases() {
		return commandAliases;
	}
	
	
}
