package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.caption.CaptionRegistry;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.base.context.DelegateCommandContext;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.exceptions.CommandExceptionHandler;
import io.github.mqzn.commands.help.CommandHelpProvider;
import io.github.mqzn.commands.sender.SenderWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface CommandManager<P, S> {

	@NotNull P getBootstrap();

	@NotNull CommandExceptionHandler<S> exceptionHandler();

	<C> void executeCommand(
					@NotNull Command<S> command,
					S sender,
					@NotNull String[] args
	);

	@NotNull SenderWrapper<S> getSenderWrapper();

	@NotNull ArgumentTypeRegistry typeRegistry();

	char commandStarter();

	<C extends Command<S>> void registerCommand(C command);

	void unregisterCommand(String name);

	@Nullable Command<S> getCommand(String name);

	@NotNull Collection<Command<S>> getCommands();

	@NotNull FlagRegistry flagRegistry();

	@NotNull CaptionRegistry<S> captionRegistry();

	@NotNull SenderProviderRegistry<S> senderProviderRegistry();

	@Nullable CommandHelpProvider helpProvider();

	void setHelpProvider(@Nullable CommandHelpProvider helpProvider);

	void handleHelpProvider(@NotNull S sender,
	                        @NotNull Context<S> context,
	                        @NotNull String label,
	                        int page,
	                        @NotNull List<CommandSyntax<S>> syntaxes);

	@NotNull List<CommandSyntax<S>> findAmbiguity(@NotNull List<CommandSyntax<S>> syntaxes);

	CommandSyntax<S> findSyntax(@NotNull Command<S> command, DelegateCommandContext<S> delegateContext);

	@NotNull List<String> suggest(Command<S> command, S sender, String[] args);


	void log(String msg, Object... args);

}
