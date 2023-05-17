package io.github.mqzn.commands.utilities

class Pair<L, R> private constructor(
    @JvmField val left: L,
    @JvmField val right: R
) {

    companion object {
        fun <L, R> of(left: L, right: R): Pair<L, R> {
            return Pair(left, right)
        }
    }

}