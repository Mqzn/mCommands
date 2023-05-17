package io.github.mqzn.commands.base.syntax

import io.github.mqzn.commands.arguments.Argument
import io.github.mqzn.commands.base.Command
import io.github.mqzn.commands.base.context.DelegateCommandContext
import io.github.mqzn.commands.base.manager.flags.ContextFlagRegistry
import java.util.*

/**
 * This class represents a tree data structure
 * containing all nodes of subcommands of a
 * particular command.
 *
 * @param <S> the sender type
 * @author Mqzen
</S> */
class CommandTree<S> private constructor(private val command: Command<S>) {

    private val subCommands: MutableMap<String, SubCommandSyntax<S>> = HashMap()
    private val roots: MutableMap<String, CommandNode<S>> = HashMap()
    private val argumentWrappers: MutableMap<String, SubCommandArgumentWrapper<S>> = HashMap()

    init {
        init()
    }

    /**
     * Creates chains linked to their starting subcommand
     * where the subcommand must be a root
     */
    private fun init() {
        for (syntax in command.syntaxes()) {
            if (!syntax.isSubCommand) continue
            val subCommandSyntax = syntax as SubCommandSyntax<S>
            subCommands[subCommandSyntax.name] = subCommandSyntax
            if (subCommandSyntax.isOrphan) {
                roots[subCommandSyntax.name] = CommandNode(
                    command, subCommandSyntax
                )
            }
        }
        for (sub in subCommands.values) {
            argumentWrappers[sub.name] = SubCommandArgumentWrapper.wrap(this, sub)
        }
        for (root in roots.values) {
            if (root.data.hasChildren()) {
                loadChildrenNodes(root)
            }
        }
    }

    /**
     * Loads children nodes from the root
     *
     * @param root the root
     */
    private fun loadChildrenNodes(root: CommandNode<S>) {
        for (child in root.data.children) {
            val childSubCommand = subCommands[child]!!
            val childNode = CommandNode(
                command, childSubCommand
            )
            root.addNextNode(childNode)
            loadChildrenNodes(childNode)
        }
    }

    /**
     * Searches through the subcommands for appropriate subcommand
     * that was used in the context
     *
     * @param context the command context determined by the sender
     * @return the subcommand to execute
     */
    @Synchronized
    fun traverse(context: DelegateCommandContext<S>): TraversingResult<S> {
        val root = findRoot(context) ?: return TraversingResult(null, TraversingResultState.NOT_FOUND)
        if (matchesContext(root.data, context)) {
            return TraversingResult(root.data, TraversingResultState.FOUND)
        }
        val lastArg = getLastRawArgument(context)
        if (lastArg != null && root.data.matches(lastArg)) return TraversingResult(
            root.data,
            TraversingResultState.FOUND_INCOMPLETE
        ) else if (root.data.hasChildren()) return searchInRoot(lastArg, root, context)
        return TraversingResult(null, TraversingResultState.NOT_FOUND)
    }

    /**
     * Finds the root subcommand to start searching in its
     * chain of children
     *
     * @param context the context being executed
     * @return the root subcommand
     */
    private fun findRoot(context: DelegateCommandContext<S>): CommandNode<S>? {
        var nextLiteral: String? = null
        for (next in context.rawArguments) {
            if (!ContextFlagRegistry.isRawArgumentFlag(next)) {
                nextLiteral = next
                break
            }
        }
        if (nextLiteral == null) {
            return null
        }
        val rootNode = roots[nextLiteral.lowercase(Locale.getDefault())]
        if (rootNode != null) return rootNode
        for (root in roots.values) {
            if (CommandSyntax.aliasesIncludes(root.data.aliases, nextLiteral)) {
                return root
            }
        }
        return null
    }

    /**
     * Searches for children used in
     * the root subcommand
     *
     * @param root    the root subcommand
     * @param context the context being executed
     * @return the subcommand child that matches the context being executed
     */
    private tailrec fun searchInRoot(
        lastArg: String?,
        root: CommandNode<S>,
        context: DelegateCommandContext<S>
    ): TraversingResult<S> {
        for (nextNode in root.nextNodes) {
            return if (matchesContext(nextNode.data, context)) TraversingResult(
                nextNode.data,
                TraversingResultState.FOUND
            ) else if (lastArg != null && nextNode.data.matches(lastArg)) TraversingResult(
                nextNode.data,
                TraversingResultState.FOUND_INCOMPLETE
            ) else searchInRoot(lastArg, nextNode, context)
        }
        return TraversingResult(null, TraversingResultState.NOT_FOUND)
    }

    fun getSubCommand(name: String): SubCommandSyntax<S>? {
        return subCommands[name]
    }

    private fun getArgWrapper(subCmdName: String): SubCommandArgumentWrapper<S>? {
        return argumentWrappers[subCmdName]
    }

    fun getParentalArguments(subCmdName: String): List<Argument<*>> {
        val wrapper = getArgWrapper(subCmdName)
            ?: return emptyList()
        return wrapper.parentalArguments()
    }

    /**
     * Checks if the syntax matches the context input
     * Here are some examples:
     *
     * @param commandContext the input
     * @return whether the syntax is suitable for the context used !
     */
    private fun matchesContext(subCommand: SubCommandSyntax<S>, commandContext: DelegateCommandContext<S>): Boolean {
        val subPosition = getSubCommandPosition(subCommand, commandContext)
        val arguments = subCommand.arguments
        val flagsUsed = usedFlagsInContext(commandContext)
        val flagsCount = subCommand.flags.count()
        val minSyntaxLength = arguments.stream()
            .filter { arg: Argument<*> -> !arg.isOptional }
            .count().toInt()
        val maxSyntaxLength = arguments.size + if (flagsUsed) flagsCount else 0
        val rawArgsLength = commandContext.rawArguments.size
        val rawLength = rawArgsLength - subPosition - 1
        return rawLength in minSyntaxLength..maxSyntaxLength
    }

    private fun usedFlagsInContext(context: DelegateCommandContext<S>): Boolean {
        for (arg in context.rawArguments) {
            if (ContextFlagRegistry.isRawArgumentFlag(arg)) return true
        }
        return false
    }

    private fun getSubCommandPosition(syntax: SubCommandSyntax<S>, context: DelegateCommandContext<S>): Int {
        for (i in context.rawArguments.indices) {
            val raw = context.getRawArgument(i) ?: return -1
            if (syntax.matches(raw)) return i
        }
        return -1
    }

    enum class TraversingResultState {
        NOT_FOUND, FOUND, FOUND_INCOMPLETE
    }

    /**
     * A class that defines a subcommand node,
     * each node has a reference to its children nodes,
     * the children can be more than one, that's why
     * a set is being used
     *
     * @param <S> the type of the sender
    </S> */
    private class CommandNode<S>(
        command: Command<S>,
        val data: SubCommandSyntax<S>
    ) {
        val nextNodes: MutableSet<CommandNode<S>>

        init {
            nextNodes = HashSet(command.syntaxes().size)
        }

        fun addNextNode(node: CommandNode<S>) {
            nextNodes.add(node)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return if (other !is CommandNode<*>) false else data == other.data
        }

        override fun hashCode(): Int {
            return Objects.hash(data)
        }
    }

    data class TraversingResult<S>(@JvmField val data: CommandSyntax<S>?, @JvmField val state: TraversingResultState)
    companion object {
        /**
         * Constructs a new CommandTree instance using the command
         * specified for the tree
         *
         * @param command the command that will hold this tree
         * @param <S>     the sender type
         * @return the command tree instance
        </S> */
        @JvmStatic
        fun <S> create(command: Command<S>): CommandTree<S> {
            return CommandTree(command)
        }

        fun <S> getLastRawArgument(context: DelegateCommandContext<S>): String? {
            var lastArgument = context.getRawArgument(context.rawArguments.size - 1)
            for (i in context.rawArguments.indices.reversed()) {
                val raw = context.getRawArgument(i)
                if (ContextFlagRegistry.isRawArgumentFlag(raw)) continue
                lastArgument = raw
                break
            }
            return lastArgument
        }
    }
}