package io.github.mqzn.commands.base.manager

import io.github.mqzn.commands.arguments.Argument
import io.github.mqzn.commands.arguments.ArgumentLiteral
import io.github.mqzn.commands.base.Command
import io.github.mqzn.commands.base.syntax.CommandSyntax
import lombok.EqualsAndHashCode
import lombok.Getter
import java.util.function.Predicate
import java.util.stream.Collectors

class CommandSuggestionEngine<S> private constructor(private val command: Command<S>) {
    private val suggestionContainers: MutableSet<SyntaxSuggestionContainer<S>> = HashSet()

    init {
        initialize()
    }

    private fun initialize() {
        for (syntax in command.syntaxes()) suggestionContainers.add(SyntaxSuggestionContainer(syntax))
    }

    fun getSuggestions(args: Array<String>): Set<SyntaxSuggestionContainer<S>> {
        return suggestionContainers.stream()
            .filter { container: SyntaxSuggestionContainer<S> -> container.argSequenceMatches(args) }
            .collect(Collectors.toSet())
    }

    class SyntaxSuggestionContainer<S> @JvmOverloads constructor(
        @field:Getter private val syntax: CommandSyntax<S>,
        private val provider: SyntaxSuggestionContainerKey<S> = SyntaxSuggestionContainerKey.from(syntax)
    ) {
        private val suggestions: MutableMap<Int, List<String>> = HashMap()
        private val dynamicArgs: MutableSet<Int> = HashSet()

        init {
            fetchArgumentSuggestions()
        }

        private fun fetchArgumentSuggestions() {
            for (arg in 0 until syntax.length()) {
                val argument = syntax.getArgument(arg) ?: break
                if (argument.isSuggestionDynamic) {
                    dynamicArgs.add(arg)
                }
                suggestions[arg] = collectArgumentSuggestions<Any>(argument)
            }
        }

        private fun isArgumentDynamic(index: Int): Boolean {
            return dynamicArgs.contains(index)
        }

        @Suppress("UNCHECKED_cAST")
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

    @EqualsAndHashCode
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
                    if (raw.isBlank() || raw.isEmpty()) {
                        return@Predicate true
                    }
                    raw.equals(argument.id(), ignoreCase = true)
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

        @Suppress("UNCHECKED_CAST")
        private fun <T> collectArgumentSuggestions(arg: Argument<*>): List<String> {
            val argument = arg as Argument<T>
            return ArrayList(argument.suggestions().stream()
                .map { obj: T -> argument.toString(obj) }
                .toList())
        }
    }
}