package io.github.mqzn.commands.arguments;

import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class ArgumentLocation extends AbstractArgument<Location> {
	
	private final static String EXCEPTION_MESSAGE = "Invalid location `%s`, provide the location in the format: `x,y,z`";
	
	public ArgumentLocation(@NotNull String id) {
		super(id, Location.class);
	}
	
	public ArgumentLocation(@NotNull ArgumentData data) {
		super(data, Location.class);
	}
	
	@Override
	public <S> Location parse(@UnknownNullability S sender,
	                          @NotNull String command,
	                          @NotNull String input) throws ArgumentParseException {
		if (!(sender instanceof Player player)) {
			throw new ArgumentParseException("Location cannot be parsed," +
				" cannot find the world since the sender is not a player !", input, command);
		}
		
		String[] split = input.split(",");
		if (split.length != 3) {
			throw new ArgumentParseException(EXCEPTION_MESSAGE, input, command);
		}
		
		try {
			double x = Double.parseDouble(split[0]);
			double y = Double.parseDouble(split[1]);
			double z = Double.parseDouble(split[2]);
			
			return new Location(player.getWorld(), x, y, z);
		} catch (NumberFormatException ex) {
			throw new ArgumentParseException(EXCEPTION_MESSAGE, input, command);
		}
		
	}
	
	
}
