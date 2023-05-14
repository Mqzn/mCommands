package io.github.mqzn.commands.base.context;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.manager.flags.ContextFlagRegistry;
import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Context<S> {
	
	
	/**
	 * Fetches the number of flags used in the raw arguments
	 *
	 * @return the count of flags used in raw args
	 */
	int flagsUsed();
	
	/**
	 * Fetches the sender for this context
	 *
	 * @return the context command sender
	 */
	@NotNull S sender();
	
	/**
	 * The command found and used in the context
	 * made by the command sender
	 *
	 * @return the command used !
	 * @see Command
	 */
	@NotNull Command<S> commandUsed();
	
	/**
	 * The raw arguments used in the context
	 * made by the command sender
	 *
	 * @return The raw arguments
	 */
	@NotNull List<String> getRawArguments();
	
	/**
	 * The raw arguments formatted using the
	 * command used
	 *
	 * @return the raw format used in the context
	 * @see Command
	 * @see CommandContext
	 */
	@NotNull String rawFormat();
	
	/**
	 * Fetches the raw argument from the input in the
	 * constructor
	 *
	 * @param index the index of the raw argument to fetch
	 * @return the raw argument at a specific position
	 */
	@Nullable String getRawArgument(int index);
	
	
	/**
	 * Fetches the parsed argument value
	 * may return null if the value parsed is not valid
	 * some cases of failed argument parsing may be like this one:
	 * an integer argument with min of 1 and max of 10, however the input was "one"
	 * or may be "-1" which is not a valid value .
	 *
	 * @param id  the argument name/id
	 * @param <T> the type of arg value
	 * @return the parsed value of the argument
	 */
	@Nullable <T> T getArgument(String id);
	
	
	/**
	 * Fetches the parsed argument value
	 * may return null if the value parsed is not valid
	 * some cases of failed argument parsing may be like this one:
	 * an integer argument with min of 1 and max of 10, however the input was "one"
	 * or may be "-1" which is not a valid value .
	 *
	 * @param index the argument index/position
	 * @param <T>   the type of arg value
	 * @return the parsed value of the argument
	 */
	@Nullable <T> T getArgument(int index);
	
	
	/**
	 * Fetches the original required argument
	 * stated by the syntax executed
	 *
	 * @param index the index of the arg
	 * @return the original required argument
	 */
	@Nullable Argument<?> getRequiredArgument(int index);
	
	/**
	 * Parses the arguments into the used syntax
	 * this algorithm should provide good reasonable performance
	 */
	<T> void parse() throws ArgumentParseException;
	
	/**
	 * The number of parsed args
	 *
	 * @return the number of arguments parsed in the context
	 */
	int parsedArguments();
	
	/**
	 * The flags used in the command
	 *
	 * @return the flag registry for the flags used in the context
	 * of the command executed by the command sender
	 */
	@NotNull ContextFlagRegistry<S> flags();
	
	/**
	 * The length of the args used in the raw context
	 *
	 * @return The length of the args used in the raw context
	 */
	int length();
	
	default int getLastIndex() {
		return length() - 1;
	}
	
}
