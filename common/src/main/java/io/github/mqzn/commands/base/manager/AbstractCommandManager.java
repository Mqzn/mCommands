package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.arguments.ArgumentNumber;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.CommandRequirement;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.caption.CaptionRegistry;
import io.github.mqzn.commands.base.context.CommandContext;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.base.context.DelegateCommandContext;
import io.github.mqzn.commands.base.cooldown.CommandCooldown;
import io.github.mqzn.commands.base.cooldown.CooldownCaption;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import io.github.mqzn.commands.base.syntax.CommandTree;
import io.github.mqzn.commands.exceptions.CommandExceptionHandler;
import io.github.mqzn.commands.exceptions.UnknownCommandSenderType;
import io.github.mqzn.commands.exceptions.types.ArgumentParseException;
import io.github.mqzn.commands.exceptions.types.SyntaxAmbiguityException;
import io.github.mqzn.commands.help.CommandHelpProvider;
import io.github.mqzn.commands.help.CommandSyntaxPageDisplayer;
import io.github.mqzn.commands.base.SenderWrapper;
import io.github.mqzn.commands.utilities.TimeParser;
import io.github.mqzn.commands.utilities.text.PaginatedText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

/**
 * The class responsible for handling,
 * registering, and coordinating the execution of the
 * available commands
 *
 * @param <P> The bootstrap for the lib to work on
 * @param <S> The command sender type
 * @see CommandManager
 */
public abstract class AbstractCommandManager<P, S> implements CommandManager<P, S> {
	
	protected final Logger logger = Logger.getLogger("CommandManager-Logger");
	
	protected final P bootstrap;
	
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
	private final CommandExecutionCoordinator<S> coordinator;
	
	
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
	private CommandHelpProvider commandHelpProvider;
	
	public AbstractCommandManager(@NotNull P bootstrap,
	                              @NotNull SenderWrapper<S> wrapper, @NotNull CommandExecutionCoordinator.Type coordinator) {
		this.bootstrap = bootstrap;
		this.wrapper = wrapper;
		this.commands = new HashMap<>();
		this.coordinator = coordinator == CommandExecutionCoordinator.Type.ASYNC ? CommandExecutionCoordinator.async(this) : CommandExecutionCoordinator.sync(this);
		this.typeRegistry = new ArgumentTypeRegistry();
		try {
			this.flagRegistry = FlagRegistry.create();
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		this.suggestionProviderRegistry = new SuggestionProviderRegistry();
		this.captionRegistry = new CaptionRegistry<>(this);
		this.senderProviderRegistry = new SenderProviderRegistry<>();
		this.exceptionHandler = new CommandExceptionHandler<>(this);
		
		this.argumentNumberSuggestionProcessor = ArgumentNumberSuggestionProcessor.create(this);
		
	}
	
	public AbstractCommandManager(@NotNull P bootstrap, @NotNull SenderWrapper<S> wrapper) {
		this(bootstrap, wrapper, CommandExecutionCoordinator.Type.SYNC);
	}
	
	@Override
	public @NotNull P getBootstrap() {
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
	public @Nullable CommandHelpProvider helpProvider() {
		return commandHelpProvider;
	}
	
	@Override
	public void setHelpProvider(@Nullable CommandHelpProvider helpProvider) {
		this.commandHelpProvider = helpProvider;
	}
	
	@Override
	public void handleHelpProvider(@NotNull S sender,
	                               @NotNull Context<S> context,
	                               @NotNull String label,
	                               int page,
	                               @NotNull List<CommandSyntax<S>> commandSubCommands) {
		
		
		if (commandHelpProvider == null) {
			captionRegistry.sendCaption(sender, context, CaptionKey.NO_HELP_TOPIC_AVAILABLE);
			return;
		}
		
		var paginated = PaginatedText.<S, CommandSyntax<S>>create(commandHelpProvider, wrapper)
			.withDisplayer(new CommandSyntaxPageDisplayer<>(this, commandHelpProvider));
		
		commandSubCommands.forEach(paginated::add);
		
		paginated.paginate();
		paginated.displayPage(label, sender, page);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final <C> void executeCommand(
		final @NotNull Command<S> command,
		final @NotNull S sender,
		final @NotNull String[] args
	) {
		
		DelegateCommandContext<S> context = DelegateCommandContext.create(this, command, sender, args);
		
		if (!checkRequirements(command, sender, context)) return;
		
		if (command.hasCooldown()) {
			CommandCooldown cooldown = command.cooldown();
			String senderName = wrapper.senderName(sender);
			Long lastTimeCommandExecuted = cooldowns.get(wrapper.senderName(sender));
			
			if (lastTimeCommandExecuted == null || cooldownExpired(lastTimeCommandExecuted, cooldown)) {
				cooldowns.put(senderName, System.currentTimeMillis());
			} else {
				//send a caption telling the user that he's in a cool down
				
				//calculating remaining time
				TimeParser parser = TimeParser.parse(calculateRemainingTime(lastTimeCommandExecuted, cooldown));
				captionRegistry.sendCaption(sender, context, null, new CooldownCaption<>(parser));
				return;
			}
			
		}
		
		if (args.length == 0) {
			command.defaultExecution(sender, context);
			return;
		}
		
		CommandTree.TraversingResult<S> result = findSyntax(command, context);
		
		if (result.state == CommandTree.TraversingResultState.NOT_FOUND) {
			captionRegistry.sendCaption(sender, context, CaptionKey.UNKNOWN_COMMAND);
			return;
		} else if (result.state == CommandTree.TraversingResultState.FOUND_INCOMPLETE) {
			assert result.data != null && result.data.isSubCommand();
			SubCommandSyntax<S> subCmd = (SubCommandSyntax<S>) result.data;
			subCmd.defaultExecution(sender, context);
			return;
		}
		
		CommandSyntax<S> syntax = result.data;
		assert syntax != null;
		
		//log("Found syntax : " + syntaxUsed.formatted());
		
		CommandContext<S> commandContext = CommandContext.create(this, syntax, context);
		try {
			commandContext.parse();
		} catch (ArgumentParseException e) {
			return;
		}
		
		if (this.wrapper.canBeSender(syntax.getSenderClass())) {
			
			coordinator.coordinateExecution(sender, syntax, commandContext)
				.whenComplete((v, ex) -> log("%s has executed the command '%s'", wrapper.senderName(sender), commandContext.rawFormat()));
			
			return;
		}
		//custom sender detected
		//fetching custom sender type
		C customSender = (C) senderProviderRegistry.provideSender(sender, syntax.getSenderClass());
		
		//checking if custom sender is null, if so then it failed to find its type, so throwing an exception
		if (customSender == null) {
			throw new UnknownCommandSenderType(syntax.getSenderClass());
		}
		
		coordinator.coordinateExecution(customSender, syntax, commandContext)
			.whenComplete((v, ex) -> log("%s has executed the command '%s'", wrapper.senderName(sender), commandContext.rawFormat()));
		
	}
	
	
	@Override
	public CommandTree.TraversingResult<S> findSyntax(final @NotNull Command<S> command,
	                                                  final @NotNull DelegateCommandContext<S> commandContext) {
		
		for (CommandSyntax<S> syntax : command.syntaxes()) {
			if (!syntax.isSubCommand() && syntax.matchesContext(commandContext))
				return new CommandTree.TraversingResult<>(syntax, CommandTree.TraversingResultState.FOUND);
		}
		
		return command.tree().traverse(commandContext);
	}
	
	private boolean cooldownExpired(@NotNull Long lastTime, @NotNull CommandCooldown cooldown) {
		return System.currentTimeMillis() > lastTime + cooldown.toMillis();
	}
	
	private long calculateRemainingTime(@NotNull Long lastTime, @NotNull CommandCooldown commandCooldown) {
		long diff = (System.currentTimeMillis() - lastTime);
		
		return commandCooldown.toMillis() - diff;
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
		
		List<CommandSyntax<S>> check = this.findAmbiguity(command.syntaxes());
		
		if (!check.isEmpty()) {
			
			try {
				throw new SyntaxAmbiguityException(this, command, check);
			} catch (SyntaxAmbiguityException e) {
				e.printStackTrace();
				return;
			}
			
		}
		
		commands.put(command.name(), command);
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
		Set<CommandSuggestionEngine.SyntaxSuggestionContainer<S>> suggestionsContainer = suggestionEngine.getSuggestions(args);
		if (suggestionsContainer.isEmpty()) return Collections.emptyList();
		
		List<String> allSuggestions = new ArrayList<>();
		for (var container : suggestionsContainer) {
			var argSuggestions = container.getArgumentSuggestions(index);
			if (argSuggestions == null) continue;
			allSuggestions.addAll(argSuggestions);
		}
		
		return allSuggestions;
	}
	
	@Override
	public synchronized @NotNull List<CommandSyntax<S>> findAmbiguity(@NotNull List<CommandSyntax<S>> syntaxes) {
		AmbiguityChecker<S> ambiguityChecker = AmbiguityChecker.Companion.of(syntaxes);
		return ambiguityChecker.findAmbiguity();
	}
	
	@Override
	public <N extends Number> void setNumericArgumentSuggestions(@NotNull ArgumentNumber<N> argNum) {
		argumentNumberSuggestionProcessor.provide(argNum);
	}
	
	@Override
	public void log(String msg, Object... args) {
		logger.info(String.format(msg, args));
	}
	
}
