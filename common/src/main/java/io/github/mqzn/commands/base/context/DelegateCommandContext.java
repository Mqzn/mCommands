package io.github.mqzn.commands.base.context;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.manager.flags.ContextFlagRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DelegateCommandContext<S> implements Context<S> {
	
	private final static int CAPACITY_ARGUMENTS = 45; // 45-50
	
	@NotNull
	private final S sender;
	
	@NotNull
	private final Command<S> command;
	
	@NotNull
	private final String rawFormatted;
	
	@NotNull
	private final List<String> rawArguments = new ArrayList<>(CAPACITY_ARGUMENTS);
	
	private int flagsUsedInRaw = 0;
	
	private DelegateCommandContext(@NotNull CommandManager<?, S> manager,
	                               @NotNull Command<S> command,
	                               @NotNull S sender,
	                               @NotNull String[] rawInput) {
		
		this.command = command;
		this.sender = sender;
		this.rawArguments.addAll(Arrays.asList(rawInput));
		this.rawFormatted = manager.commandPrefix() + command.name() + " " + String.join(" ", rawInput);
		
		for (var arg : rawInput) if (ContextFlagRegistry.isRawArgumentFlag(arg)) flagsUsedInRaw++;
		
		
	}
	
	public static <S> @NotNull DelegateCommandContext<S> create(@NotNull CommandManager<?, S> manager,
	                                                            @NotNull Command<S> command,
	                                                            @NotNull S sender,
	                                                            @NotNull String[] rawInput) {
		
		return new DelegateCommandContext<>(manager, command, sender, rawInput);
	}
	
	
	/**
	 * Fetches the raw argument from the input in the
	 * constructor
	 *
	 * @param index the index of the raw argument to fetch
	 * @return the raw argument at a specific position
	 */
	@Override
	public @Nullable String getRawArgument(int index) {
		if (index < 0 || index >= rawArguments.size()) return null;
		return rawArguments.get(index);
	}
	
	/**
	 * Fetches the parsed argument value
	 * may return null if the value parsed is not valid
	 * some cases of failed argument parsing may be like this one:
	 * an integer argument with min of 1 and max of 10, however the input was "one"
	 * or may be "-1" which is not a valid value .
	 *
	 * @param id the argument name/id
	 * @return the parsed value of the argument
	 */
	@Override
	public <T> @Nullable T getArgument(String id) {
		throw new UnsupportedOperationException("This cannot be done using a delegate context !");
	}
	
	/**
	 * Fetches the parsed argument value
	 * may return null if the value parsed is not valid
	 * some cases of failed argument parsing may be like this one:
	 * an integer argument with min of 1 and max of 10, however the input was "one"
	 * or may be "-1" which is not a valid value .
	 *
	 * @param index the argument index/position
	 * @return the parsed value of the argument
	 */
	@Override
	public <T> @Nullable T getArgument(int index) {
		throw new UnsupportedOperationException("This cannot be done using a delegate context !");
	}
	
	/**
	 * Fetches the original required argument
	 * stated by the syntax executed
	 *
	 * @param index the index of the arg
	 * @return the original required argument
	 */
	@Override
	public @Nullable Argument<?> getRequiredArgument(int index) {
		throw new UnsupportedOperationException("This cannot be done using a delegate context !");
	}
	
	/**
	 * Parses the arguments into the used syntax
	 * this algorithm should provide good reasonable performance
	 */
	@Override
	public void parse() {
		throw new UnsupportedOperationException("This cannot be done using a delegate context !");
	}
	
	/**
	 * The flags used in the command
	 *
	 * @return the flag registry for the flags used in the context
	 * of the command executed by the command sender
	 */
	@Override
	public @NotNull ContextFlagRegistry<S> flags() {
		throw new UnsupportedOperationException("This cannot be done using a delegate context !");
	}
	
	/**
	 * The number of parsed args
	 *
	 * @return the number of arguments parsed in the context
	 */
	@Override
	public int parsedArguments() {
		return 0;
	}
	
	
	/**
	 * The raw arguments used in the context
	 * made by the command sender
	 *
	 * @return The raw arguments
	 */
	public @NotNull List<String> getRawArguments() {
		return rawArguments;
	}
	
	/**
	 * Fetches the number of flags used in the raw arguments
	 *
	 * @return the count of flags used in raw args
	 */
	@Override
	public int flagsUsed() {
		return flagsUsedInRaw;
	}
	
	
	/**
	 * Fetches the sender for this context
	 *
	 * @return the context command sender
	 */
	@Override
	public @NotNull S sender() {
		return sender;
	}
	
	/**
	 * The command found and used in the context
	 * made by the command sender
	 *
	 * @return the command used !
	 * @see Command
	 */
	@Override
	public @NotNull Command<S> commandUsed() {
		return command;
	}
	
	/**
	 * The raw arguments formatted using the
	 * command used
	 *
	 * @return the raw format used in the context
	 * @see Command
	 * @see CommandContext
	 */
	@Override
	public @NotNull String rawFormat() {
		return rawFormatted;
	}
	
	
	@Override
	public int length() {
		return rawArguments.size();
	}
	
}
