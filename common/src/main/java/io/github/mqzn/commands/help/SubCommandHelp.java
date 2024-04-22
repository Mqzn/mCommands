package io.github.mqzn.commands.help;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.base.caption.CaptionKey;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.syntax.CommandAliases;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import io.github.mqzn.commands.base.syntax.SubCommandSyntax;
import io.github.mqzn.commands.base.syntax.SyntaxFlags;
import io.github.mqzn.commands.base.syntax.tree.CommandTree;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubCommandHelp<S> extends SubCommandSyntax<S> {
	
	public SubCommandHelp(CommandManager<?, S> manager, String commandLabel, SubCommandSyntax<S> parent) {
		super(manager, manager.getSenderWrapper().senderType(),
			commandLabel, parent.getName(), "help", CommandAliases.of(),
			((sender, commandContext) -> execute(manager, parent, sender, commandContext)),
			SyntaxFlags.of(),
			List.of(Argument.integer("page").min(1).asOptional().setDefaultValue(1)),
			((sender, context) -> helpExecution(sender, manager, context, 1, parent)));
	}
	
	private static <S> void helpExecution(S sender, CommandManager<?, S> manager, Context<S> context, int page, SubCommandSyntax<S> parent) {
		List<CommandSyntax<S>> children = parent.getChildren().stream()
			.map(childName -> {
				CommandTree.SubCommandKey<S> key = CommandTree.SubCommandKey.create(parent.getName(), childName);
				return context.commandUsed().tree().getSubCommand(key);
			})
			.collect(Collectors.toList());
		
		try {
			manager.handleHelpRequest(sender, context, parent.getName(), page, children);
		} catch (IllegalArgumentException ex) {
			manager.captionRegistry().sendCaption(sender, context, CaptionKey.UNKNOWN_HELP_PAGE);
		}
	}
	
	private static <S> void execute(CommandManager<?, S> manager,
	                                SubCommandSyntax<S> parent,
	                                S sender, Context<S> context) {
		int page = (int) Optional.ofNullable(context.getArgument("page")).orElse(1);
		helpExecution(sender, manager, context, page, parent);
	}
}
