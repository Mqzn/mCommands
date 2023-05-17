package io.github.mqzn.commands.base.context;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.arguments.ArgumentLiteral;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.manager.flags.ContextFlagRegistry;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class CommandContext<S> implements Context<S> {
	
	@NotNull
	private final CommandManager<?, S> manager;
	
	@NotNull
	private final DelegateCommandContext<S> delegateContext;
	
	@NotNull
	private final Map<ArgumentKey, ParsedArgument<?>> parsedArguments = new HashMap<>();
	
	private final CommandSyntax<S> syntax;
	
	private final ContextFlagRegistry<S> contextFlagRegistry;
	
	
	private CommandContext(@NotNull CommandManager<?, S> manager,
	                       @NotNull CommandSyntax<S> syntax,
	                       @NotNull DelegateCommandContext<S> context) {
		
		this.manager = manager;
		this.syntax = syntax;
		this.delegateContext = context;
		contextFlagRegistry = ContextFlagRegistry.create(manager, this);
		
	}
	
	public static <S> CommandContext<S> create(@NotNull CommandManager<?, S> manager,
	                                           @NotNull CommandSyntax<S> syntax,
	                                           @NotNull DelegateCommandContext<S> context) {
		return new CommandContext<>(manager, syntax, context);
	}
	
	/**
	 * Parses the arguments into the used syntax
	 * this algorithm should provide good reasonable performance
	 *
	 * @param <T> the type of arguments that is being parsed
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> void parse() throws ArgumentParseException {
		
		@NotNull S sender = sender();
		if (syntax == null) {
			manager.captionRegistry()
				.sendCaption(sender,
					this, CaptionKey.UNKNOWN_COMMAND);
			return;
		}
		
		
		var result = contextFlagRegistry.extractFlags(sender, commandUsed(), syntax);
		if (result == ContextFlagRegistry.FlagExtractionResult.FAILED)
			return;
		
		List<Argument<?>> syntaxArgs = syntax.isSubCommand() ? this.commandUsed()
			.tree().getParentalArguments(((SubCommandSyntax<S>) syntax).getName()) : syntax.getArguments();
		
		for (int required = 0, rawIndex = 0; required < syntaxArgs.size(); required++) {
			Argument<T> argument = (Argument<T>) syntaxArgs.get(required);
			
			if (argument instanceof ArgumentLiteral) {
				rawIndex++;
				continue;
			}
			
			String rawArg = getRawArgument(rawIndex);
			
			if (rawArg != null && ContextFlagRegistry.isRawArgumentFlag(rawArg) && !argument.useRemainingSpace()) {
				rawArg = getRawArgument(++rawIndex);
			} else if (argument.useRemainingSpace()) {
				
				StringBuilder builder = new StringBuilder();
				for (int x = rawIndex; x < delegateContext.getRawArguments().size(); x++) {
					String raw = getRawArgument(x);
					builder.append(raw);
					if (x != delegateContext.getRawArguments().size() - 1) builder.append(" ");
				}
				
				rawArg = builder.toString();
				
			}
			
			T value = null;
			
			if (rawArg == null && argument.isOptional() && argument.defaultValue() != null) {
				value = argument.defaultValue();
			}
			
			if (rawArg != null) {
				try {
					if (argument.useRemainingSpace()) value = (T) rawArg;
					else value = argument.parse(delegateContext.commandUsed().name(), rawArg);
				} catch (ArgumentParseException ex) {
					manager.exceptionHandler().handleException(ex, sender, this);
					throw ex;
				}
				
			}
			
			ParsedArgument<T> parsedArgument = new ParsedArgument<>(argument, value, required, rawIndex, rawArg);
			parsedArguments.put(new ArgumentKey(argument.id(), required), parsedArgument);
			
			rawIndex++;
		}
		
		
	}
	
	/**
	 * The number of parsed args
	 *
	 * @return the number of arguments parsed in the context
	 */
	@Override
	public int parsedArguments() {
		return parsedArguments.size();
	}
	
	/**
	 * The flags used in the command
	 *
	 * @return the flag registry for the flags used in the context
	 * of the command executed by the command sender
	 */
	@Override
	public @NotNull ContextFlagRegistry<S> flags() {
		return contextFlagRegistry;
	}
	
	/**
	 * The length of the args used in the raw context
	 *
	 * @return The length of the args used in the raw context
	 */
	@Override
	public int length() {
		return syntax.length();
	}
	
	
	/**
	 * Fetches the number of flags used in the raw arguments
	 *
	 * @return the count of flags used in raw args
	 */
	@Override
	public int flagsUsed() {
		return delegateContext.flagsUsed();
	}
	
	/**
	 * Fetches the sender for this context
	 *
	 * @return the context command sender
	 */
	@Override
	public @NotNull S sender() {
		return delegateContext.sender();
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
		return delegateContext.commandUsed();
	}
	
	/**
	 * The raw arguments used in the context
	 * made by the command sender
	 *
	 * @return The raw arguments
	 */
	@Override
	public @NotNull List<String> getRawArguments() {
		return delegateContext.getRawArguments();
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
		return delegateContext.rawFormat();
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
		return delegateContext.getRawArgument(index);
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
		return getParsedArgument((key) -> key.id.equalsIgnoreCase(id));
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
		return getParsedArgument((key) -> key.requiredArgIndex == index);
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
		return syntax.getArgument(index);
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	private <T> T getParsedArgument(@NotNull Predicate<ArgumentKey> predicate) {
		
		for (ArgumentKey key : parsedArguments.keySet()) {
			if (!predicate.test(key)) continue;
			
			ParsedArgument<T> parsedArgument = (ParsedArgument<T>) parsedArguments.get(key);
			return parsedArgument.value();
		}
		
		return null;
	}
	
	private record ArgumentKey(String id, int requiredArgIndex) {
	
	}
	
	private record ParsedArgument<T>(@NotNull Argument<T> argToParse, @Nullable T value, int index, int rawIndex,
	                                 @Nullable String rawValue) {
		
	}
	
}
