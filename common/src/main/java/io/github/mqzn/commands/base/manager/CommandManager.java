package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.SenderWrapper;
import io.github.mqzn.commands.base.SuggestionProvider;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.caption.CaptionRegistry;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.base.context.DelegateCommandContext;
import io.github.mqzn.commands.base.syntax.ArgumentSyntaxUtility;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import io.github.mqzn.commands.base.syntax.tree.CommandTree;
import io.github.mqzn.commands.exceptions.CommandExceptionHandler;
import io.github.mqzn.commands.help.CommandHelpProvider;
import io.github.mqzn.commands.help.CommandHelpStyle;
import io.github.mqzn.commands.utilities.text.PaginatedText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface CommandManager<B, S> {
	
	/**
	 * The bootstrap for the platform
	 * on which the manager is being used on
	 *
	 * @return the bootstrap instance
	 */
	@NotNull B getBootstrap();
	
	/**
	 * The exception handler
	 * which handles exceptions during execution stage.
	 *
	 * @return the exception handler
	 */
	@NotNull CommandExceptionHandler<S> exceptionHandler();
	
	/**
	 * Executes a command asynchronously OR synchronously
	 * using the execution coordinator {@link CommandExecutionCoordinator<S>}
	 *
	 * @param command the command to execute
	 * @param sender  the command sender
	 * @param args    the arguments used
	 * @param <C>     the type of the custom sender
	 */
	<C> void executeCommand(
		@NotNull Command<S> command,
		@NotNull S sender,
		@NotNull String[] args
	);
	
	/**
	 * The wrapper for sending messages
	 * across different platforms
	 *
	 * @return the sender wrapper
	 */
	@NotNull SenderWrapper<S> getSenderWrapper();
	
	/**
	 * The argument types registry
	 * which contains all types of arguments
	 * that will be handled during execution
	 *
	 * @return the argument type registry
	 */
	@NotNull ArgumentTypeRegistry typeRegistry();
	
	/**
	 * The prefix of all commands
	 *
	 * @return the prefix
	 */
	char commandPrefix();
	
	/**
	 * Registers a command in the platform
	 *
	 * @param command the command instance
	 * @param <C>     the type of the command class
	 */
	<C extends Command<S>> void registerCommand(C command);
	
	/**
	 * Unregisters the command
	 *
	 * @param name the command to unregister
	 */
	void unregisterCommand(String name);
	
	/**
	 * Gets a command which is already registered
	 *
	 * @param name the command's name to fetch
	 * @return the registered command that goes by a specific name
	 * returns null if no command with that name was registered !
	 */
	@Nullable Command<S> getCommand(String name);
	
	/**
	 * All registered commands
	 *
	 * @return all registered commands
	 */
	@NotNull Collection<Command<S>> getCommands();
	
	/**
	 * The registry that holds all the flags that will be handled
	 * during command execution
	 *
	 * @return the flags registry
	 * @see FlagRegistry
	 */
	@NotNull FlagRegistry flagRegistry();
	
	/**
	 * The registry that holds all the captions that will be used
	 * during command execution
	 *
	 * @return the ca registry
	 * @see CaptionRegistry
	 */
	@NotNull CaptionRegistry<S> captionRegistry();
	
	/**
	 * The registry that holds all custom sender providers that will be used
	 * during command execution
	 *
	 * @return the sender provider registry
	 * @see SenderProviderRegistry
	 */
	@NotNull SenderProviderRegistry<S> senderProviderRegistry();
	
	/**
	 * Fetches the command help provider
	 * during command execution of '/cmd help'
	 *
	 * @return the sender provider registry
	 * @see CommandHelpStyle
	 * @see CommandHelpProvider
	 */
	@Nullable CommandHelpProvider<S> helpProvider();
	
	/**
	 * Sets the help menu provider for
	 * all commands registered
	 *
	 * @param helpProvider the new help provider
	 */
	void setHelpProvider(@Nullable CommandHelpProvider<S> helpProvider);
	
	/**
	 * Finds syntaxes that are ambigious
	 *
	 * @param command the syntaxes provider to check upon for ambiguity
	 * @return the ambigious syntaxes
	 */
	@NotNull List<CommandSyntax<S>> findAmbiguity(@NotNull Command<S> command);
	
	/**
	 * Finds the most suitable syntax for the context
	 * that is being executed
	 *
	 * @param command         the command being executed
	 * @param delegateContext the delegate command context (context containing only raws)
	 * @return the traversing result of the search
	 */
	CommandTree.CommandSearchResult<S> findSyntax(@NotNull Command<S> command, DelegateCommandContext<S> delegateContext);
	
	/**
	 * Suggests the suggestions in correspond to the args being used
	 * while tab completing and to the command arguments of course !
	 *
	 * @param command the command being used while tab completing
	 * @param sender  the sender
	 * @param args    the args
	 * @return the suggestions
	 */
	@NotNull List<String> suggest(Command<S> command, S sender, String[] args);
	
	
	/**
	 * Numeric argument suggestion processor
	 *
	 * @return the processor of numeric args suggestions
	 */
	@NotNull ArgumentNumberSuggestionProcessor numericArgumentSuggestionProcessor();
	
	/**
	 * The suggestion provider registry
	 *
	 * @return the provider registry for suggestions per argument
	 * @see SuggestionProvider
	 */
	@NotNull SuggestionProviderRegistry suggestionProviderRegistry();
	
	void log(String msg, Object... args);
	
	
	default void handleHelpRequest(@NotNull S sender,
	                               @NotNull Context<S> context,
	                               @NotNull String label,
	                               int page,
	                               @NotNull List<? extends CommandSyntax<S>> syntaxes) throws IllegalArgumentException {
		
		CommandHelpProvider<S> helpProvider = helpProvider();
		CaptionRegistry<S> captionRegistry = captionRegistry();
		if (helpProvider == null) {
			captionRegistry.sendCaption(sender, context, CaptionKey.NO_HELP_TOPIC_AVAILABLE);
			return;
		}
		
		CommandHelpStyle<S> style = helpProvider.menuStyle();
		
		var paginated = PaginatedText.<S, CommandSyntax<S>>create(style, getSenderWrapper())
			.withDisplayer(helpProvider.syntaxDisplayer(this, context.commandUsed(), style));
		
		Command<S> command = context.commandUsed();
		for (var syntax : syntaxes) {
			if (label.equalsIgnoreCase(command.name()) || ArgumentSyntaxUtility.aliasesIncludes(command.info().aliases(), label)) {
				if ((syntax instanceof SubCommandSyntax<S> sub) && (!sub.isOrphan())) continue;
			}
			paginated.add(syntax);
		}
		
		paginated.paginate();
		paginated.displayPage(label, sender, page);
	}
	
}
