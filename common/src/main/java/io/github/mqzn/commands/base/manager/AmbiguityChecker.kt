package io.github.mqzn.commands.base.manager

import io.github.mqzn.commands.arguments.ArgumentLiteral
import io.github.mqzn.commands.base.syntax.CommandSyntax

class AmbiguityChecker<S> private constructor(private val syntaxes: List<CommandSyntax<S>>) {
    @Synchronized
    fun findAmbiguity(): List<CommandSyntax<S>> {
        for (syntax in syntaxes) {
            if (syntax.useSpace() && !hasLiteralArgs(syntax) && syntaxes.size > 1) {
                return syntaxes
            }
        }
        val ambigious: MutableList<CommandSyntax<S>> = ArrayList()
        for (first in syntaxes.indices) {
            val firstSyntax = syntaxes[first]
            for (second in syntaxes.indices) {
                if (first == second) continue
                val secondSyntax = syntaxes[second]
                val areAmbigious = areAmbigious(firstSyntax, secondSyntax)
                if (areAmbigious) {
                    ambigious.add(firstSyntax)
                    ambigious.add(secondSyntax)
                }
            }
        }
        return ambigious
    }

    private fun areAmbigious(s1: CommandSyntax<S>, s2: CommandSyntax<S>): Boolean {

        // first condition is that 2 or more syntaxes without literal args and same length
        // second condition is just a duplicate syntax
        val firstCondition = !hasLiteralArgs(s1) && !hasLiteralArgs(s2) && s1.length() == s2.length()
        val secondCondition = s1 == s2
        return firstCondition || secondCondition
    }

    companion object {
        fun <S> of(syntaxes: List<CommandSyntax<S>>): AmbiguityChecker<S> {
            return AmbiguityChecker(syntaxes)
        }

        @JvmStatic
        fun <S> hasLiteralArgs(syntax: CommandSyntax<S>): Boolean {
            if (syntax.isSubCommand) return true
            for (arg in syntax.arguments) {
                if (arg is ArgumentLiteral) {
                    return true
                }
            }
            return false
        }
    }
}