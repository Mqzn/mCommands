package io.github.mqzen.commands.base.manager.flags;

import io.github.mqzen.commands.arguments.Argument;
import io.github.mqzen.commands.base.Information;
import io.github.mqzen.commands.base.context.CommandContext;
import io.github.mqzen.commands.base.context.Context;
import io.github.mqzen.commands.base.manager.CommandManager;
import io.github.mqzen.commands.base.manager.FlagRegistry;
import io.github.mqzen.commands.base.syntax.CommandSyntax;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContextFlagRegistry<S> {

	@NotNull
	public static final Pattern FLAG_PATTERN = Pattern.compile(FlagRegistry.FLAG_IDENTIFIER + "[a-z]+", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Getter
	private final CommandManager<?, S> manager;

	@NotNull
	@Getter
	private final Context<S> commandContext;

	@NotNull
	private final Map<String, CommandFlag> flagsUsed = new HashMap<>();


	private ContextFlagRegistry(@NotNull CommandManager<?, S> manager,
	                            @NotNull Context<S> commandContext) {
		this.manager = manager;
		this.commandContext = commandContext;
	}

	public static <S> @NotNull ContextFlagRegistry<S> create(
					@NotNull CommandManager<?, S> manager,
					@NotNull CommandContext<S> commandContext
	) {
		return new ContextFlagRegistry<>(manager, commandContext);
	}

	public static boolean isRawArgumentFlag(String rawArg) {
		Matcher matcher = FLAG_PATTERN.matcher(rawArg);
		return matcher.matches();
	}

	public FlagExtractionResult extractFlags(@NotNull S sender, @NotNull CommandSyntax<S> syntax) {

		for (int i = 0, r = 0; i < syntax.getArguments().size(); i++, r++) {

			Argument<?> argument = syntax.getArguments().get(i);
			if (argument.useRemainingSpace()) continue;

			String raw = commandContext.getRawArgument(r);
			if (raw == null)
				break;


			if (isRawArgumentFlag(raw)) {

				var extracted = extractFlagsUsed(sender, raw);
				boolean foundOneAtLeast = !extracted.isEmpty();

				if (!foundOneAtLeast) {
					manager.getSenderWrapper().sendMessage(sender, "The flag(s) used are unknown to the command flags registry !");
					return FlagExtractionResult.FAILED;
				}

				for (CommandFlag flag : extracted)
					this.flagsUsed.put(flag.name(), flag);

				i--;
			}

		}


		for (var flag : flagsUsed.values()) {

			Information flagInfo = flag.info();
			if (!syntax.getFlags().hasFlag(flag.name())) {
				manager.getSenderWrapper().sendMessage(sender, "The flag '" + FlagRegistry.FLAG_IDENTIFIER + flag.name() + "' is not allowed in this syntax");
				return FlagExtractionResult.FAILED;
			} else if (flagInfo != null && flagInfo.permission() != null && !manager.getSenderWrapper().hasPermission(sender, flagInfo.permission())) {
				manager.getSenderWrapper().sendMessage(sender, "No permission to use the flag '" + FlagRegistry.FLAG_IDENTIFIER + flag.name() + "'");
				return FlagExtractionResult.FAILED;
			}

		}

		if (flagsUsed.isEmpty()) return FlagExtractionResult.FOUND_NONE;
		else return FlagExtractionResult.SUCCESS;

	}

	@NotNull
	private Set<CommandFlag> extractFlagsUsed(@NotNull S sender, @NotNull String flagRaw) {
		final Set<CommandFlag> flags = new LinkedHashSet<>();

		//first check if it's single

		//there's more !
		//examples: -x , -xyz

		for (FlagInfo flagInfo : manager.flagRegistry().flags()) {

			boolean foundFlagAliase = false;
			for (String aliase : flagInfo.getAliases()) {
				if (flagRaw.contains(aliase)) {

					if (foundFlagAliase) {
						manager.getSenderWrapper().sendMessage(sender, "Warning: you used the same flag '" + flagRaw + "' twice !");
						break;
					}

					flags.add(CommandFlag.from(flagInfo));
					foundFlagAliase = true;
				}

			}

		}

		return flags;
	}

	public boolean isPresent(String flagName) {
		return getFlag(flagName) != null;
	}

	public @Nullable CommandFlag getFlag(String flagName) {
		return flagsUsed.get(flagName);
	}

	public int count() {
		return flagsUsed.size();
	}

	public enum FlagExtractionResult {

		SUCCESS,

		FOUND_NONE,

		FAILED;

	}

}
