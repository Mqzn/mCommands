package dev.mqzen.commands.base;

import dev.mqzen.commands.arguments.Argument;
import dev.mqzen.commands.arguments.ArgumentLiteral;
import dev.mqzen.commands.base.manager.CommandManager;
import dev.mqzen.commands.base.manager.flags.ContextFlagRegistry;
import dev.mqzen.commands.base.syntax.CommandSyntax;
import dev.mqzen.commands.exceptions.types.ArgumentParseException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Predicate;

public final class CommandContext<S> {

	private final static int CAPACITY_ARGUMENTS = 45; // 45-50

	@NotNull
	private final S sender;

	@NotNull
	private final CommandManager<?, S> manager;


	@NotNull @Getter
	private final Command<S> command;

	@NotNull @Getter
	private final String rawFormatted;

	@NotNull
	private final List<String> rawArguments = new ArrayList<>(CAPACITY_ARGUMENTS);

	@NotNull
	private final Map<ArgumentKey, ParsedArgument<?>> parsedArguments = new HashMap<>();

	@NotNull
	private final ContextFlagRegistry<S> contextFlagRegistry;

	private int flagsUsedInRaw = 0;

	private CommandContext(@NotNull CommandManager<?, S> manager,
												 @NotNull Command<S> command,
	                       @NotNull S sender, @NotNull String[] argumentsInput) {
		this.sender = sender;
		this.manager = manager;
		this.rawArguments.addAll(Arrays.asList(argumentsInput));
		this.command = command;
		this.rawFormatted = manager.commandStarter() + command.name() + " " + String.join(" ",argumentsInput);

		for(var arg : argumentsInput) if(ContextFlagRegistry.isRawArgumentFlag(arg)) flagsUsedInRaw++;

		contextFlagRegistry = ContextFlagRegistry.create(manager,this);

	}

	public static <S> CommandContext<S> create(@NotNull CommandManager<?, S> manager,
	                                           @NotNull Command<S> command,
	                                           @NotNull S sender,
	                                           @NotNull String[] argumentsInput) {
		return new CommandContext<>(manager, command, sender, argumentsInput);
	}

	// user <name> permission set <perm>
	// user -silent mqzen permission set hello

	@SuppressWarnings("unchecked")
	public <T> void linkToSyntax(@NotNull CommandSyntax<S> syntax) {
		var result = contextFlagRegistry.extractFlags(sender, syntax);
		if(result == ContextFlagRegistry.FlagExtractionResult.FAILED)
			return;

		for (int i = 0, rawIndex = 0; i < syntax.length(); i++) {
			Argument<T> required = (Argument<T>) syntax.getArguments().get(i);

			if(required instanceof ArgumentLiteral){
				rawIndex++;
				continue;
			}

			String rawArg = getRawArgument(rawIndex);

			if(rawArg != null && ContextFlagRegistry.isRawArgumentFlag(rawArg) && !required.useRemainingSpace()) {
				rawArg = getRawArgument(++rawIndex);
			}

			else if(required.useRemainingSpace()) {

				StringBuilder builder = new StringBuilder();
				for (int x = i; x < rawArguments.size(); x++) {
					String raw = getRawArgument(x);
					builder.append(raw);
					if(x != rawArguments.size()-1) builder.append(" ");
				}

				rawArg = builder.toString();

			}

			T value = null;

			if(rawArg == null && required.isOptional() && required.defaultValue() != null) {
				value = required.defaultValue();
			}

			if(rawArg != null) {
				try {
					value = required.parse(command, rawArg);
				}catch (ArgumentParseException ex) {
					manager.getSenderWrapper().sendMessage(sender, ex.getMessage());
					return;
				}

			}

			ParsedArgument<T> parsedArgument = new ParsedArgument<>(required, value, i, rawIndex, rawArg);
			parsedArguments.put(new ArgumentKey(required.id(), i), parsedArgument);

			rawIndex++;
		}


	}

	public @NotNull S getSender() {
		return sender;
	}

	public @NotNull List<String> getRawArguments() {
		return rawArguments;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	private <T> T getParsedArgument(@NotNull Predicate<ArgumentKey> predicate) {

		for(ArgumentKey key : parsedArguments.keySet()) {
			if(!predicate.test(key)) continue;

			ParsedArgument<T> parsedArgument = (ParsedArgument<T>) parsedArguments.get(key);
			return parsedArgument.getValue();
		}

		return null;
	}

	public <T> T getParsedArgument(String id) {
		return getParsedArgument((key)-> key.id.equals(id));
	}

	public <T> T getParsedArgument(int index) {
		return getParsedArgument((key)-> key.requiredArgIndex == index);
	}

	@Nullable
	public String getRawArgument(int index) {

		if(index < 0 || index >= rawArguments.size()) {
			return null;
		}

		return rawArguments.get(index);
	}

	public int getRequiredArgsCount() {
		return parsedArguments.size();
	}

	public ContextFlagRegistry<S> flags() {
		return contextFlagRegistry;
	}

	public int flagsUsedCount() {
		return flagsUsedInRaw;
	}

	public void debug() {

		for(ParsedArgument<?> argument : parsedArguments.values()) {
			System.out.println("Raw Index = " + argument.getRawIndex()
							+ ", Usual Index = " + argument.getIndex() + ", Raw Value= " + argument.getRawValue() + ", Arg required = " + argument.getArgToParse().toString());
		}

	}


	private record ArgumentKey(String id, int requiredArgIndex) {

	}

}
