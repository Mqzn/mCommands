package io.github.mqzn.commands.help

import io.github.mqzn.commands.arguments.Argument
import io.github.mqzn.commands.base.caption.CaptionKey
import io.github.mqzn.commands.base.context.Context
import io.github.mqzn.commands.base.manager.CommandManager
import io.github.mqzn.commands.base.syntax.*
import io.github.mqzn.commands.base.syntax.tree.CommandTree

class SubCommandHelp<S : Any>(
    manager: CommandManager<*, S>,
    commandLabel: String,
    parent: SubCommandSyntax<S>
) : SubCommandSyntax<S>(manager, manager.senderWrapper.senderType(), commandLabel,
    parent.name,
    "help",
    CommandAliases.of(),
    CommandExecution { sender: S, context: Context<S> ->
        var page: Int? = context.getArgument("page")
        if (page == null) page = 1
        helpExecution(sender, manager, context, page!!, parent)
    },
    SyntaxFlags.of(),
    mutableListOf<Argument<*>>(Argument.integer("page").min(1).asOptional().setDefaultValue(1)),
    CommandExecution { sender: S, context: Context<S> -> helpExecution(sender, manager, context, 1, parent) }) {

    companion object {
        private fun <S : Any> helpExecution(
            sender: S,
            manager: CommandManager<*, S>,
            context: Context<S>,
            page: Int,
            parent: SubCommandSyntax<S>
        ) {
            val children: List<CommandSyntax<S>> = parent.children.stream()
                .map { childName: String ->
                    val key: CommandTree.SubCommandKey<S> = CommandTree.SubCommandKey.create(parent.name, childName)
                    context.commandUsed().tree().getSubCommand(key)!!
                }.toList()

            try {
                manager.handleHelpRequest(sender, context, parent.name, page, children)
            } catch (ex: IllegalArgumentException) {
                manager.captionRegistry().sendCaption(sender, context, CaptionKey.UNKNOWN_HELP_PAGE)
            }
        }
    }
}