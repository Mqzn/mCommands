package io.github.mqzn.commands.base.manager

import io.github.mqzn.commands.arguments.Argument
import io.github.mqzn.commands.arguments.ArgumentLiteral
import io.github.mqzn.commands.base.Command
import io.github.mqzn.commands.base.syntax.CommandSyntax
import io.github.mqzn.commands.base.syntax.SubCommandSyntax
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors

@Suppress("UNCHECKED_CAST")
class CommandSuggestionEngine<S> private constructor(private val command: Command<S>) {
    private val suggestionContainers: MutableSet<SyntaxSuggestionContainer> = HashSet()

    init {
        initialize()
    }

    private fun initialize() {
        for (syntax in command.syntaxes()) suggestionContainers.add(SyntaxSuggestionContainer(syntax))
    }

    fun getSuggestions(args: Array<String>): Set<SyntaxSuggestionContainer> {
        return suggestionContainers.stream()
            .filter { container: SyntaxSuggestionContainer -> container.argSequenceMatches(args) }
            .collect(Collectors.toSet())
    }

    inner class SyntaxSuggestionContainer @JvmOverloads constructor(
        private val syntax: CommandSyntax<S>,
        private val provider: SyntaxSuggestionContainerKey<S> = SyntaxSuggestionContainerKey.from(syntax)
    ) {
        private val suggestions: MutableMap<Int, List<String>> = HashMap()
        private val dynamicArgs: MutableSet<Int> = HashSet()

        init {
            fetchArgumentSuggestions()
        }

        private fun fetchArgumentSuggestions() {
            for (arg in 0 until syntax.length()) {
                val argument = if (syntax is SubCommandSyntax<S>) command.tree().getParentalArguments(
                    syntax.name
                )[arg] else syntax.getArgument(arg)
                if (argument == null) break
                if (argument.isSuggestionDynamic) {
                    dynamicArgs.add(arg)
                }
                suggestions[arg] = collectArgumentSuggestions<Any>(argument)
            }
        }

        private fun isArgumentDynamic(index: Int): Boolean {
            return dynamicArgs.contains(index)
        }

        fun <T> getArgumentSuggestions(argIndex: Int): List<String>? {
            if (isArgumentDynamic(argIndex)) {
                val argument = (syntax.getArgument(argIndex) as Argument<T>?)!!
                return argument.suggestions().stream().map { obj: T -> argument.toString(obj) }
                    .collect(Collectors.toList())
            }
            return suggestions[argIndex]
        }

        fun argSequenceMatches(args: Array<String>): Boolean {
            return provider.matches(args)
        }
    }

    class SyntaxSuggestionContainerKey<S> private constructor(syntax: CommandSyntax<S>) {
        private val rawPredicates: MutableMap<Int, Predicate<Array<String>>>

        init {
            rawPredicates = HashMap()
            init(syntax)
        }

        /**
         * Creates a condition to accept a specific tab completion of a specific syntax
         */
        private fun init(syntax: CommandSyntax<S>) {
            for (index in 0 until syntax.length()) {
                val argument = syntax.getArgument(index) as? ArgumentLiteral ?: continue
                rawPredicates[index] = Predicate { args: Array<String> ->
                    val raw = args[index]
                    if (raw.isBlank() || raw.isEmpty()) return@Predicate true
                    return@Predicate raw.equals(argument.id(), ignoreCase = true)
                }
            }
        }

        fun matches(args: Array<String>): Boolean {
            if (args.size <= 1) return true
            for (i in args.indices) {
                val predicate = rawPredicates[i] ?: continue
                if (!predicate.test(args)) {
                    return false
                }
            }
            return true
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return if (other !is SyntaxSuggestionContainerKey<*>) false else rawPredicates == other.rawPredicates
        }

        override fun hashCode(): Int {
            return Objects.hash(rawPredicates)
        }

        companion object {
            fun <S> from(syntax: CommandSyntax<S>): SyntaxSuggestionContainerKey<S> {
                return SyntaxSuggestionContainerKey(syntax)
            }
        }
    }

    companion object {
        @JvmStatic
        fun <S> create(command: Command<S>): CommandSuggestionEngine<S> {
            return CommandSuggestionEngine(command)
        }

        private fun <T> collectArgumentSuggestions(arg: Argument<*>): List<String> {
            val argument = arg as Argument<T>
            return ArrayList(argument.suggestions().stream()
                .map { obj: T -> argument.toString(obj) }
                .toList())
        }
    }
}