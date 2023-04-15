package io.github.mqzen.commands.base.manager;

import io.github.mqzen.commands.arguments.Argument;
import io.github.mqzen.commands.arguments.ArgumentLiteral;
import io.github.mqzen.commands.base.Command;
import io.github.mqzen.commands.base.CommandRequirement;
import io.github.mqzen.commands.base.caption.CaptionKey;
import io.github.mqzen.commands.base.caption.CaptionRegistry;
import io.github.mqzen.commands.base.context.CommandContext;
import io.github.mqzen.commands.base.context.Context;
import io.github.mqzen.commands.base.context.DelegateCommandContext;
import io.github.mqzen.commands.base.manager.flags.ContextFlagRegistry;
import io.github.mqzen.commands.base.syntax.CommandSyntax;
import io.github.mqzen.commands.exceptions.CommandExceptionHandler;
import io.github.mqzen.commands.exceptions.types.SyntaxAmbiguityException;
import io.github.mqzen.commands.help.CommandHelpProvider;
import io.github.mqzen.commands.help.CommandSyntaxPageDisplayer;
import io.github.mqzen.commands.sender.SenderWrapper;
import io.github.mqzen.commands.utilities.text.PaginatedText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public abstract class AbstractCommandManager<P, S> implements CommandManager<P, S> {

	protected final Logger logger = Logger.getLogger("CommandManager-Logger");

	protected final P plugin;
	@NotNull
	protected final SenderWrapper<S> wrapper;
	@NotNull
	protected final CaptionRegistry<S> captionRegistry;
	@NotNull
	private final CommandExceptionHandler<S> exceptionHandler;
	@NotNull
	private final CommandExecutionCoordinator<S> coordinator;
	@NotNull
	private final Map<String, Command<S>> commands;
	@NotNull
	private final ArgumentTypeRegistry typeRegistry;
	@NotNull
	private final FlagRegistry flagRegistry;
	@Nullable
	private CommandHelpProvider commandHelpProvider;


	public AbstractCommandManager(@NotNull P plugin,
	                              @NotNull SenderWrapper<S> wrapper, @NotNull CommandExecutionCoordinator.Type coordinator) {
		this.plugin = plugin;
		this.wrapper = wrapper;
		this.commands = new HashMap<>();
		this.coordinator = coordinator == CommandExecutionCoordinator.Type.ASYNC ? CommandExecutionCoordinator.async(this) : CommandExecutionCoordinator.sync(this);
		this.typeRegistry = new ArgumentTypeRegistry();
		try {
			this.flagRegistry = FlagRegistry.create();
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		this.captionRegistry = new CaptionRegistry<>(this);
		this.exceptionHandler = new CommandExceptionHandler<>(this);
	}

	public AbstractCommandManager(@NotNull P plugin, @NotNull SenderWrapper<S> wrapper) {
		this(plugin, wrapper, CommandExecutionCoordinator.Type.SYNC);
	}

	@Override
	public @NotNull P getBootstrap() {
		return plugin;
	}

	@Override
	public @NotNull ArgumentTypeRegistry typeRegistry() {
		return typeRegistry;
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
	                               @NotNull List<CommandSyntax<S>> commandSyntaxes) {


		if (commandHelpProvider == null) {
			captionRegistry.sendCaption(sender, context, CaptionKey.NO_HELP_TOPIC_AVAILABLE);
			return;
		}

		var paginated = PaginatedText.<S, CommandSyntax<S>>create(commandHelpProvider, wrapper)
						.withDisplayer(new CommandSyntaxPageDisplayer<>(commandHelpProvider));

		commandSyntaxes.forEach(paginated::add);

		paginated.paginate();
		paginated.displayPage(label, sender, page);
	}

	@Override
	public final void executeCommand(
					final @NotNull Command<S> command,
					final @NotNull S sender,
					final @NotNull String[] args
	) {

		DelegateCommandContext<S> context = DelegateCommandContext.create(this, command, sender, args);

		if (!checkRequirements(command, sender, context)) return;

		if (args.length == 0) {
			command.defaultExecution(sender, context);
			return;
		}

		CommandSyntax<S> syntax = findSyntax(command, context);
		if (syntax == null) {
			captionRegistry.sendCaption(sender, context, CaptionKey.UNKNOWN_COMMAND);
			return;
		}

		//log("Found syntax : " + syntaxUsed.formatted());
		CommandContext<S> commandContext = CommandContext.create(this, syntax, context);
		commandContext.parse();

		coordinator.coordinateExecution(sender, syntax, commandContext)
						.whenComplete((result, ex) -> log("%s has executed the command '%s'", wrapper.senderName(sender), commandContext.rawFormat()));

	}


	@Override
	public @Nullable CommandSyntax<S> findSyntax(final @NotNull Command<S> command,
	                                             final @NotNull DelegateCommandContext<S> commandContext) {

		for (CommandSyntax<S> syntax : command.syntaxes())
			if (syntax.matchesContext(commandContext)) return syntax;

		return null;
	}

	private boolean checkRequirements(final @NotNull Command<S> command,
	                                  final @NotNull S sender,
	                                  final @NotNull Context<S> commandContext) {

		for (CommandRequirement<S> requirement : command.requirements()) {
			if (!requirement.accepts(sender, commandContext)) {
				this.captionRegistry.sendCaption(sender, commandContext, requirement.caption());
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
				throw new SyntaxAmbiguityException(command, check);
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
	public @NotNull List<String> suggest(Command<S> command, S sender, String[] args) {
		List<String> completions = new ArrayList<>();

		for (var syntax : command.syntaxes()) {
			var info = syntax.getInfo();
			String permission = info == null ? null : info.permission();

			if (args.length >= syntax.withoutFlagsOrOptionalArgumentsLength()
							&& args.length <= syntax.length()
							&& wrapper.hasPermission(sender, permission)) {

				completions.addAll(collectSuggestions(syntax, args));
			}

		}

		return completions;
	}

	private List<String> collectSuggestions(CommandSyntax<S> syntax, String[] rawArgs) {

		List<String> suggestions = new ArrayList<>();
		for (int r = 0, a = 0; a < syntax.length(); r++) {

			String raw = r >= rawArgs.length ? null : rawArgs[r];
			Argument<?> required = syntax.getArgument(r);

			if (ContextFlagRegistry.isRawArgumentFlag(raw)) continue;

			if (required == null) break;

			if (required.isOptional() && raw == null) {
				a++;
				continue;
			} else if (raw == null) {
				break;
			}


			if (required instanceof ArgumentLiteral && raw.equalsIgnoreCase(required.id())) {
				suggestions.add(required.id());

			} else {

				suggestions.addAll(required.suggestions()
								.stream().map(Object::toString)
								.toList());
			}

			a++;
		}

		return suggestions;
	}

	@Override
	public synchronized @NotNull List<CommandSyntax<S>> findAmbiguity(@NotNull List<CommandSyntax<S>> syntaxes) {
		AmbiguityChecker<S> ambiguityChecker = AmbiguityChecker.of(syntaxes);
		return ambiguityChecker.findAmbiguity();
	}

	@Override
	public void log(String msg, Object... args) {
		logger.info(String.format(msg, args));
	}

}
