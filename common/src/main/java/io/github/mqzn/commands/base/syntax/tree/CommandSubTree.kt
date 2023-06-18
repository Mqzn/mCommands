package io.github.mqzn.commands.base.syntax.tree

import io.github.mqzn.commands.arguments.Argument
import io.github.mqzn.commands.base.Command
import java.util.*

@Suppress("UNUSED")
class CommandSubTree<S : Any> private constructor(
    private val command: Command<S>,
    private val tree: CommandTree<S>,
) {
    private val data: MutableMap<CommandTree.SubCommandKey<S>, MutableMap<CommandTree.SubCommandKey<S>, Pathway>> =
        HashMap()

    init {
        initPathways()
        loadParentalPathway()
        //debug()
    }

    private fun initPathways() {
        for (root in tree.getRoots().values)
            loadRootArguments(root, root)
    }

    private fun loadParentalPathway() {
        for ((_, nodePathwayMapping) in data) {
            for ((key, pathway) in nodePathwayMapping) {
                val subCommand = tree.getSubCommand(key) ?: continue
                if (subCommand.isOrphan) continue

                var parentName: String? = key.parent!!
                var parentSub = tree.searchForSub(parentName!!)
                while (parentSub != null) {

                    for (arg in parentSub.arguments.reversed()) pathway.addFirstArg(arg)

                    pathway.addFirstArg(
                        Argument.literal(parentName)
                            .aliases(*parentSub.aliases.array)
                    )

                    parentName = parentSub.parent
                    if (parentName == null) break
                    parentSub = tree.searchForSub(parentName)
                }

            }
        }
    }

    /*private fun debug() {
        for(map in data.values) {
            for(key in map.keys) {
                val pathway: Pathway = map[key] !!
                print("Pathway of key $key = $pathway")
            }
        }

    }*/


    private fun loadRootArguments(
        root: CommandTree.CommandNode<S>,
        node: CommandTree.CommandNode<S>
    ) {

        val rootKey = CommandTree.SubCommandKey<S>(root.data.parent, root.data.name)

        data.compute(rootKey) { _, oldMap ->
            if (oldMap == null) {
                val list: LinkedList<Argument<*>> = LinkedList<Argument<*>>()
                list.addLast(Argument.literal(node.data.name).aliases(*node.data.aliases.array))
                for (arg in node.data.arguments) list.addLast(arg)

                return@compute mutableMapOf(node.data.key() to Pathway(list))
            } else {
                val nodeKey = node.data.key()
                if (!oldMap.containsKey(nodeKey)) {
                    val list: LinkedList<Argument<*>> = LinkedList<Argument<*>>()
                    list.addLast(Argument.literal(node.data.name).aliases(*node.data.aliases.array))
                    for (arg in node.data.arguments) list.addLast(arg)
                    oldMap.computeIfAbsent(nodeKey) { _ -> return@computeIfAbsent Pathway(list) }
                    return@compute oldMap
                } else {
                    val oldPathway = oldMap[nodeKey]!!
                    oldPathway.addArg(Argument.literal(node.data.name).aliases(*node.data.aliases.array))
                    for (arg in node.data.arguments) oldPathway.addArg(arg)
                    oldMap.computeIfPresent(nodeKey) { _, _ -> return@computeIfPresent oldPathway }
                    return@compute oldMap
                }
            }
        }

        for (child in node.data.children) {
            val childKey: CommandTree.SubCommandKey<S> = CommandTree.SubCommandKey.create(node.data.name, child)
            val childSub = tree.getSubCommand(childKey)
                ?: throw IllegalStateException("Unknown child sub command `${childKey.name}` make sure you registered this subcommand in the main class ")
            loadRootArguments(root, CommandTree.CommandNode(command, childSub))
        }

    }

    fun getSubCommandArguments(key: CommandTree.SubCommandKey<S>): LinkedList<Argument<*>>? {
        for (mapping in data.values) {
            val pathway: Pathway = mapping[key] ?: continue
            return pathway.arguments
        }
        return null
    }


    companion object {
        fun <S : Any> wrap(
            command: Command<S>,
            tree: CommandTree<S>,
        ): CommandSubTree<S> {
            return CommandSubTree(command, tree)
        }
    }

    private class Pathway constructor(val arguments: LinkedList<Argument<*>>) {

        fun addFirstArg(arg: Argument<*>) {
            arguments.addFirst(arg)
        }

        fun addArg(arg: Argument<*>) {
            arguments.addLast(arg)
        }

        fun removeArg(arg: Argument<*>) {
            arguments.remove(arg)
        }

        fun addPathway(pathway: Pathway): Pathway {
            for (arg in pathway.arguments) addArg(arg)
            return this
        }

        override fun toString(): String {
            return arguments.toString()
        }
    }


}