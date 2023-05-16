package io.github.mqzn.commands.base.syntax

import io.github.mqzn.commands.arguments.Argument
import java.util.*

class SubCommandArgumentWrapper<S> private constructor(
    private val tree: CommandTree<S>,
    private val subCommand: SubCommandSyntax<S>
) {
    private val totalArguments = LinkedList<Argument<*>>()

    init {
        collectArguments()
    }

    private fun collectArguments() {
        totalArguments.add(
            Argument.literal(subCommand.name)
                .aliases(*subCommand.aliases.array)
        )
        totalArguments.addAll(subCommand.arguments)
        var parent = subCommand.parent
        while (parent != null) {
            val parentSubCommand = tree.getSubCommand(parent) ?: break
            for (parentArg in parentSubCommand.arguments) {
                totalArguments.addFirst(parentArg)
            }
            totalArguments.addFirst(
                Argument.literal(parentSubCommand.name)
                    .aliases(*parentSubCommand.aliases.array)
            )
            parent = parentSubCommand.parent
        }
    }

    fun get(): SubCommandSyntax<S> {
        return subCommand
    }

    fun parentalArguments(): LinkedList<Argument<*>> {
        return totalArguments
    }

    companion object {
        fun <S> wrap(
            tree: CommandTree<S>,
            subCommand: SubCommandSyntax<S>
        ): SubCommandArgumentWrapper<S> {
            return SubCommandArgumentWrapper(tree, subCommand)
        }
    }
}