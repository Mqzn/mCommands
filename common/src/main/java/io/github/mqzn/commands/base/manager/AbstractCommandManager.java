package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.arguments.ArgumentStringArray;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.CommandRequirement;
import io.github.mqzn.commands.base.SenderWrapper;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.caption.CaptionRegistry;
import io.github.mqzn.commands.base.context.CommandContext;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.base.context.DelegateCommandContext;
import io.github.mqzn.commands.base.cooldown.CommandCooldown;
import io.github.mqzn.commands.base.cooldown.CooldownCaption;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import io.github.mqzn.commands.base.syntax.tree.CommandTree;
import io.github.mqzn.commands.exceptions.CommandExceptionHandler;
import io.github.mqzn.commands.exceptions.UnknownCommandSenderType;
import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import io.github.mqzn.commands.exceptions.types.SyntaxAmbiguityException;
import io.github.mqzn.commands.help.CommandHelpProvider;
import io.github.mqzn.commands.help.UnknownPageCaption;
import io.github.mqzn.commands.utilities.ArgumentSyntaxUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The class responsible for handling,
 * registering, and coordinating the execution of the
 * available commands
 *
 * @param <B> The bootstrap for the lib to work on
 * @param <S> The command sender type
 * @see CommandManager
 */
public abstract class AbstractCommandManager<B, S> implements CommandManager<B, S> {
	
	protected final Logger logger = Logger.getLogger("CommandManager-Logger");
	
	protected final B bootstrap;
	
	@NotNull
	protected final ArgumentNumberSuggestionProcessor argumentNumberSuggestionProcessor;
	
	@NotNull
	protected final SenderWrapper<S> wrapper;
	
	@NotNull
	protected final CaptionRegistry<S> captionRegistry;
	
	@NotNull
	protected final SenderProviderRegistry<S> senderProviderRegistry;
	
	@NotNull
	private final CommandExceptionHandler<S> exceptionHandler;
	
	
	@NotNull
	private final Map<String, Command<S>> commands;
	
	@NotNull
	private final ArgumentTypeRegistry typeRegistry;
	
	@NotNull
	private final SuggestionProviderRegistry suggestionProviderRegistry;
	
	@NotNull
	private final FlagRegistry flagRegistry;
	
	@NotNull
	private final Map<String, Long> cooldowns = new HashMap<>();
	
	@Nullable
	private CommandHelpProvider<S> commandHelpProvider;
	
	public AbstractCommandManager(@NotNull B bootstrap,
	                              @NotNull SenderWrapper<S> wrapper) {
		this.bootstrap = bootstrap;
		this.wrapper = wrapper;
		this.commands = new HashMap<>();
		this.typeRegistry = new ArgumentTypeRegistry();
		try {
			this.flagRegistry = FlagRegistry.create();
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		this.suggestionProviderRegistry = new SuggestionProviderRegistry();
		
		this.captionRegistry = new CaptionRegistry<>(this);
		captionRegistry.registerCaption(new UnknownPageCaption<>());
		captionRegistry.registerCaption(new CooldownCaption<>());
		
		this.senderProviderRegistry = new SenderProviderRegistry<>();
		this.exceptionHandler = new CommandExceptionHandler<>(this);
		
		this.argumentNumberSuggestionProcessor = ArgumentNumberSuggestionProcessor.create(this);
		
	}
	
	
	@Override
	public @NotNull B getBootstrap() {
		return bootstrap;
	}
	
	@Override
	public @NotNull ArgumentTypeRegistry typeRegistry() {
		return typeRegistry;
	}
	
	@Override
	public @NotNull SuggestionProviderRegistry suggestionProviderRegistry() {
		return suggestionProviderRegistry;
	}
	
	@Override
	public @NotNull CommandExceptionHandler<S> exceptionHandler() {
		return exceptionHandler;
	}
	
	@Override
	public @Nullable CommandHelpProvider<S> helpProvider() {
		return commandHelpProvider;
	}
	
	@Override
	public void setHelpProvider(@Nullable CommandHelpProvider<S> helpProvider) {
		this.commandHelpProvider = helpProvider;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public final <C> void executeCommand(
		final @NotNull Command<S> command,
		final @NotNull S sender,
		final @NotNull String[] args
	) {
		
		DelegateCommandContext<S> context = DelegateCommandContext.create(this, command, sender, args);
		String cmdPermission = command.info().permission();
		
		if (cmdPermission != null && !cmdPermission.isEmpty() && !wrapper.hasPermission(sender, cmdPermission)) {
			captionRegistry.sendCaption(sender, context, CaptionKey.NO_PERMISSION);
			return;
		}
		
		if (!checkRequirements(command, sender, context)) return;
		
		if (command.hasCooldown()) {
			CommandCooldown cooldown = command.cooldown();
			String senderName = wrapper.senderName(sender);
			Long lastTimeCommandExecuted = cooldowns.get(wrapper.senderName(sender));
			
			if (lastTimeCommandExecuted == null || cooldownExpired(lastTimeCommandExecuted, cooldown)) {
				cooldowns.put(senderName, System.currentTimeMillis());
			} else {
				//send a caption telling the user that he's in a cool down
				captionRegistry.sendCaption(sender, context, CaptionKey.COMMAND_IN_COOLDOWN);
				return;
			}
			
		}
		
		if (args.length == 0) {
			command.defaultExecution(sender, context);
			return;
		}
		
		CommandTree.CommandSearchResult<S> result = findSyntax(command, context);
		
		if (result.state == CommandTree.CommandSearchResultState.NOT_FOUND) {
			CaptionKey key = args.length == 1 && args[0].equalsIgnoreCase("help") ? CaptionKey.NO_HELP_TOPIC_AVAILABLE : CaptionKey.UNKNOWN_COMMAND;
			captionRegistry.sendCaption(sender, context, key);
			return;
		} else if (result.state == CommandTree.CommandSearchResultState.FOUND_INCOMPLETE) {
			
			if (result.data == null) return;
			if (result.data instanceof SubCommandSyntax<S> subCmd) {
				var subInfo = subCmd.getInfo();
				
				if (subInfo != null && !subCmd.checkHasPermission(wrapper, sender))
					captionRegistry.sendCaption(sender, context, CaptionKey.NO_PERMISSION);
				else
					subCmd.defaultExecution(sender, context);
			}
			return;
		}
		
		CommandSyntax<S> syntax = result.data;
		assert syntax != null;
		
		CommandContext<S> commandContext = CommandContext.create(this, syntax, context);
		try {
			commandContext.parse();
		} catch (ArgumentParseException e) {
			e.printStackTrace();
			return;
		}
		
		if (!syntax.checkHasPermission(this.wrapper, sender)) {
			captionRegistry.sendCaption(sender, context, CaptionKey.NO_PERMISSION);
			return;
		}
		
		if (this.wrapper.canBeSender(syntax.getSenderClass())) {
			
			command.coordinator().coordinateExecution(sender, syntax, commandContext)
				.exceptionally((ex) -> {
					assert ex != null;
					exceptionHandler.handleException(ex, sender, commandContext);
					return null;
				})
				.whenComplete((executionResult, ex) -> log("(%s) %s has executed the command '%s'", executionResult.name(), wrapper.senderName(sender), commandContext.rawFormat()));
			
			return;
		}
		//custom sender detected
		//fetching custom sender type
		C customSender = (C) senderProviderRegistry.provideSender(sender, syntax.getSenderClass());
		
		//checking if custom sender is null, if so then it failed to find its type, so throwing an exception
		if (customSender == null) {
			throw new UnknownCommandSenderType(syntax.getSenderClass());
		}
		
		command.coordinator().coordinateExecution(customSender, syntax, commandContext)
			.exceptionally((ex) -> {
				assert ex != null;
				exceptionHandler.handleException(ex, sender, commandContext);
				return null;
			})
			.whenComplete((executionResult, ex) -> log("(%s) %s has executed the command '%s'", executionResult.name(), wrapper.senderName(sender), commandContext.rawFormat()));
		
	}
	
	@Override
	public @Nullable Long getCommandCooldown(String senderName) {
		return cooldowns.get(senderName);
	}
	
	@Override
	public CommandTree.CommandSearchResult<S> findSyntax(final @NotNull Command<S> command,
	                                                     final @NotNull DelegateCommandContext<S> commandContext) {
		
		for (CommandSyntax<S> syntax : command.syntaxes()) {
			if (!syntax.isSubCommand() && syntax.matchesContext(commandContext))
				return new CommandTree.CommandSearchResult<>(syntax, CommandTree.CommandSearchResultState.FOUND);
		}
		
		return command.tree().traverse(commandContext);
	}
	
	private boolean cooldownExpired(@NotNull Long lastTime, @NotNull CommandCooldown cooldown) {
		return System.currentTimeMillis() > lastTime + cooldown.toMillis();
	}
	
	
	private boolean checkRequirements(final @NotNull Command<S> command,
	                                  final @NotNull S sender,
	                                  final @NotNull Context<S> commandContext) {
		
		for (CommandRequirement<S> requirement : command.requirements()) {
			
			if (!requirement.accepts(sender, commandContext)) {
				CaptionKey key = requirement.caption();
				if (key != null)
					this.captionRegistry.sendCaption(sender, commandContext, key);
				
				return false;
			}
			
		}
		
		return true;
	}
	
	@Override
	public @NotNull SenderWrapper<S> getSenderWrapper() {
		return wrapper;
	}
	
	@Override
	public <C extends Command<S>> void registerCommand(C command) {
		
		synchronized (bootstrap) {
			
			List<CommandSyntax<S>> check = this.findAmbiguity(command);
			
			if (!check.isEmpty()) {
				
				try {
					throw new SyntaxAmbiguityException(this, command, check);
				} catch (SyntaxAmbiguityException e) {
					e.printStackTrace();
					return;
				}
				
			}
			
			for (var syntax : command.syntaxes()) {
				long numberOfGreedyArguments = syntax.getArguments().stream()
					.filter(arg -> arg.useRemainingSpace() || arg instanceof ArgumentStringArray)
					.count();
				if (numberOfGreedyArguments > 1) {
					try {
						throw new SyntaxAmbiguityException("Found more than one greedy argument in one syntax : " + ArgumentSyntaxUtility.format(this, command.name(), syntax.getArguments()), command);
					} catch (SyntaxAmbiguityException ex) {
						ex.printStackTrace();
					}
					return;
				}
			}
			
			commands.put(command.name(), command);
		}
		
	}
	
	@Override
	public void unregisterCommand(String name) {
		commands.remove(name);
	}
	
	@Override
	public @Nullable Command<S> getCommand(String name) {
		var cmd = commands.get(name);
		if (cmd != null) return cmd;
		
		for (var otherCmd : getCommands()) {
			var otherNames = otherCmd.info().aliases();
			
			for (String aliases : otherNames)
				if (name.equalsIgnoreCase(aliases)) return otherCmd;
			
		}
		
		return null;
	}
	
	@Override
	public @NotNull Collection<Command<S>> getCommands() {
		return commands.values();
	}
	
	@Override
	public @NotNull FlagRegistry flagRegistry() {
		return flagRegistry;
	}
	
	@Override
	public @NotNull CaptionRegistry<S> captionRegistry() {
		return captionRegistry;
	}
	
	@Override
	public @NotNull SenderProviderRegistry<S> senderProviderRegistry() {
		return senderProviderRegistry;
	}
	
	@Override
	public synchronized @NotNull List<String> suggest(Command<S> command, S sender, String[] args) {
		CommandSuggestionEngine<S> suggestionEngine = command.suggestions();
		
		int index = args.length - 1;
		Set<CommandSuggestionEngine<S>.SyntaxSuggestionContainer> suggestionsContainer = suggestionEngine.getSuggestions(args);
		if (suggestionsContainer.isEmpty()) return Collections.emptyList();
		
		List<String> allSuggestions = new ArrayList<>();
		for (var container : suggestionsContainer) {
			var argSuggestions = container.getArgumentSuggestions(index);
			if (argSuggestions == null) continue;
			allSuggestions.addAll(argSuggestions);
		}
		
		return allSuggestions.stream().distinct()
			.filter((suggestion) -> {
				SubCommandSyntax<S> subCmd = command.tree().searchForSub(suggestion);
				if (subCmd == null) return true;
				
				var info = subCmd.getInfo();
				if (info == null) return true;
				
				var perm = info.permission();
				
				return perm == null || perm.isEmpty() || wrapper.hasPermission(sender, perm);
			})
			.collect(Collectors.toList());
	}
	
	@Override
	public synchronized @NotNull List<CommandSyntax<S>> findAmbiguity(@NotNull Command<S> command) {
		AmbiguityChecker<S> ambiguityChecker = AmbiguityChecker.of(command);
		return ambiguityChecker.findAmbiguity();
	}
	
	@Override
	public @NotNull ArgumentNumberSuggestionProcessor numericArgumentSuggestionProcessor() {
		return argumentNumberSuggestionProcessor;
	}
	
	@Override
	public void log(String msg, Object... args) {
		logger.info(String.format(msg, args));
	}
	
}
