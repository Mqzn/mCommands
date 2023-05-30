package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public final class ArgumentWorld  extends AbstractArgument<World> {
	
	public ArgumentWorld(@NotNull String id) {
		super(id, World.class);
	}
	
	public ArgumentWorld(@NotNull ArgumentData data) {
		super(data, World.class);
	}
	
	@Override
	public <S> World parse(@UnknownNullability S sender, @NotNull String command, @NotNull String input) throws ArgumentParseException {
			World world = Bukkit.getWorld(input);
			if(world == null) {
				throw new ArgumentParseException(
					String.format("Unknown world `%s` provided", input), input, command
				);
			}
			return world;
	}
	
	@Override
	public @NotNull List<World> suggestions() {
		return Bukkit.getWorlds();
	}
	
	@Override
	public boolean isSuggestionDynamic() {
		return true;
	}
	
	@Override
	public String toString(World obj) {
		return obj == null ? "null" : obj.getName();
	}
	
}
