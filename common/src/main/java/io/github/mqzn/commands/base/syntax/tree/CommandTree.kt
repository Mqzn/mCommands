package io.github.mqzn.commands.base.syntax.tree

import io.github.mqzn.commands.arguments.Argument
import io.github.mqzn.commands.base.Command
import io.github.mqzn.commands.base.context.DelegateCommandContext
import io.github.mqzn.commands.base.manager.flags.ContextFlagRegistry
import io.github.mqzn.commands.base.syntax.ArgumentSyntaxUtility
import io.github.mqzn.commands.base.syntax.CommandSyntax
import io.github.mqzn.commands.base.syntax.SubCommandSyntax
import io.github.mqzn.commands.help.SubCommandHelp
import java.util.*

/**
 * This class represents a tree data structure
 * containing all nodes of subcommands of a
 * particular command.
 *
 * @param <S> the sender type
 * @author Mqzen
</S> */
class CommandTree<S : Any> private constructor(private val command: Command<S>) {

    private val subCommands: MutableMap<SubCommandKey<S>, SubCommandSyntax<S>> = HashMap()
    private val roots: MutableMap<String, CommandNode<S>> = HashMap()
    private var subtree: CommandSubTree<S>

    init {
        init()
        subtree = CommandSubTree.wrap(command, this)
    }

    fun getRoots(): MutableMap<String, CommandNode<S>> = roots

    /**
     * Creates chains linked to their starting subcommand
     * where the subcommand must be a root
     */
    private fun init() {
        for (syntax in command.syntaxes()) {
            if (!syntax.isSubCommand) continue
            val subCommandSyntax = syntax as SubCommandSyntax<S>

            if (subCommandSyntax.hasChildren()) {
                val helpSubCommand = SubCommandHelp(command.manager(), command.name(), subCommandSyntax)
                val helpSubKey = SubCommandKey.create(helpSubCommand)
                if (subCommands[helpSubKey] == null) subCommands[helpSubKey] = helpSubCommand

                subCommandSyntax.addChild(helpSubCommand)
            }

            val subKey = SubCommandKey.create(subCommandSyntax)

            subCommands[subKey] = subCommandSyntax


            if (subCommandSyntax.isOrphan) {
                roots[subCommandSyntax.name] = CommandNode(
                    command, subCommandSyntax
                )
            }
        }

        for (root in roots.values) {
            if (root.data.hasChildren()) {
                loadChildrenNodes(root)
            }
            //debugRootContent(root)
        }


    }

    /*private fun debugRootContent(root: CommandNode<S>) {
        for(child in root.data.children) {
            println("Detected Child $child in root ${root.data.name}")
        }

        for(node in root.nextNodes) {
            debugRootContent(node)
        }

    }*/

    /**
     * Loads children nodes from the root
     *
     * @param root the root
     */
    private fun loadChildrenNodes(root: CommandNode<S>) {

        for (child in root.data.children) {
            val parentName = if (root.data.name.equals(child, true)) null else root.data.name
            val childSubCommand = getSubCommand(SubCommandKey.create(parentName, child)) ?: continue
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
    fun traverse(context: DelegateCommandContext<S>): CommandSearchResult<S> {
        val root = findRoot(context) ?: return CommandSearchResult(null, CommandSearchResultState.NOT_FOUND)

        if (matchesContext(root.data, context))
            return CommandSearchResult(root.data, CommandSearchResultState.FOUND)

        val lastArg = getLastRawArgument(context)
        if (lastArg != null && root.data.matches(lastArg)) {
            return CommandSearchResult(
                root.data,
                CommandSearchResultState.FOUND_INCOMPLETE
            )
        } else if (root.data.hasChildren()) return searchInRoot(lastArg, root, context)

        return CommandSearchResult(null, CommandSearchResultState.NOT_FOUND)
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
            if (ArgumentSyntaxUtility.aliasesIncludes(root.data.aliases, nextLiteral)) {
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
    private fun searchInRoot(
        lastArg: String?,
        root: CommandNode<S>,
        context: DelegateCommandContext<S>
    ): CommandSearchResult<S> {

        for (nextNode in root.nextNodes) {
            val previousParent = getPreviousParent(nextNode.data, context)
            if (matchesContext(nextNode.data, context)) return CommandSearchResult(
                nextNode.data,
                CommandSearchResultState.FOUND
            ) else if (lastArg != null && nextNode.data.matches(lastArg) &&
                !nextNode.data.isOrphan && previousParent != null
                && previousParent.matches(nextNode.data.parent)
            ) {

                println("Last arg matching")
                return CommandSearchResult(
                    nextNode.data,
                    CommandSearchResultState.FOUND_INCOMPLETE
                )
            } else if (nextNode.data.hasChildren()) {
                val deepSearch = searchInRoot(lastArg, nextNode, context)
                if (deepSearch.state != CommandSearchResultState.NOT_FOUND) return deepSearch
            }
        }
        return CommandSearchResult(null, CommandSearchResultState.NOT_FOUND)
    }

    fun getSubCommand(key: SubCommandKey<S>): SubCommandSyntax<S>? {
        return subCommands[key]
    }

    fun getParentalArguments(key: SubCommandKey<S>): LinkedList<Argument<*>> {
        return subtree.getSubCommandArguments(key) ?: LinkedList()
    }


    /**
     * Checks if the syntax matches the context input
     *
     * @param commandContext the input
     * @return whether the syntax is suitable for the context used !
     */
    private fun matchesContext(subCommand: SubCommandSyntax<S>, commandContext: DelegateCommandContext<S>): Boolean {
        //getting the previous parent
        val matchesLength = matchesContextLength(subCommand, commandContext)
        if (subCommand.isOrphan) return matchesLength

        val previousParent = getPreviousParent(subCommand, commandContext)
        println("Previous parent for ${subCommand.name} = ${previousParent?.name}")
        return previousParent != null && previousParent.matches(subCommand.parent) && matchesLength
    }


    private fun getPreviousParent(
        base: SubCommandSyntax<S>,
        commandContext: DelegateCommandContext<S>
    ): SubCommandSyntax<S>? {
        var subPositionInContext = commandContext.rawArguments.indexOf(base.name)
        if (subPositionInContext == -1) {
            for (index in commandContext.rawArguments.indices) {
                val arg = commandContext.getRawArgument(index) ?: break
                if (ArgumentSyntaxUtility.aliasesIncludes(base.aliases, arg)) {
                    subPositionInContext = index
                    break
                }
            }
        }

        var parent: SubCommandSyntax<S>? = null
        for (index in subPositionInContext - 1 downTo 0) {
            val rawArg = commandContext.getRawArgument(index) ?: break
            val parentSub = searchForSub(rawArg) ?: continue
            parent = parentSub
            break
        }
        return parent
    }

    private fun matchesContextLength(
        subCommand: SubCommandSyntax<S>,
        commandContext: DelegateCommandContext<S>
    ): Boolean {
        val subPosition = getSubCommandPosition(subCommand, commandContext)
        val arguments = subCommand.arguments

        println("Sub ${subCommand.key()} args: $arguments")

        val flagsUsed = usedFlagsInContext(commandContext)
        val flagsCount = subCommand.flags.count()
        val minSyntaxLength = arguments.stream()
            .filter { arg: Argument<*> -> !arg.isOptional }
            .count().toInt()

        val maxSyntaxLength = arguments.size + if (flagsUsed) flagsCount else 0
        val rawArgsLength = commandContext.rawArguments.size
        val rawLength = rawArgsLength - subPosition - 1
        println("rawArgsLength = $rawArgsLength, rawLength= $rawLength, pos=$subPosition  min=$minSyntaxLength, max=$maxSyntaxLength, sub=${subCommand.key()}")
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

    enum class CommandSearchResultState {
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
    class CommandNode<S>(
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

    fun searchForSub(name: String): SubCommandSyntax<S>? {
        for (sub in subCommands.values) {
            if (sub.matches(name)) return sub
        }
        return null
    }

    data class CommandSearchResult<S>(
        @JvmField val data: CommandSyntax<S>?,
        @JvmField val state: CommandSearchResultState
    )

    data class SubCommandKey<S>(val parent: String?, val name: String) {


        companion object {
            fun <S> create(parent: String?, name: String): SubCommandKey<S> = SubCommandKey(parent, name)

            fun <S> create(sub: SubCommandSyntax<S>): SubCommandKey<S> = SubCommandKey(sub.parent, sub.name)
        }

        @Suppress("UNUSED")
        fun nextKey(name: String?): SubCommandKey<S>? {
            if (name == null) return null
            return SubCommandKey(this.name, name)
        }

        @Suppress("UNCHECKED_CAST")
        override fun equals(other: Any?): Boolean {
            if (other !is SubCommandKey<*>) return false
            val otherKey: SubCommandKey<S> = other as SubCommandKey<S>
            return name.equals(other.name, true) && parent == otherKey.parent
        }


        override fun hashCode(): Int {
            var result = parent?.hashCode() ?: 0
            result = 31 * result + name.hashCode()
            return result
        }

        override fun toString(): String {
            return "$parent:$name"
        }
    }

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
        fun <S : Any> create(command: Command<S>): CommandTree<S> {
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