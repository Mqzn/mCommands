package io.github.mqzn.commands.base.manager

import io.github.mqzn.commands.arguments.*


class ArgumentNumberComparator {
    private val comparators: MutableMap<Class<out ArgumentNumber<*>?>, ArgumentComparator<*>>

    init {
        comparators = HashMap()
        registerComparator(ArgumentInteger::class.java, IntegerComparator())
        registerComparator(ArgumentDouble::class.java, DoubleComparator())
        registerComparator(ArgumentFloat::class.java, FloatComparator())
        registerComparator(ArgumentLong::class.java, LongComparator())
    }

    fun <N : Number?> registerComparator(argClass: Class<out ArgumentNumber<N>?>, comparator: ArgumentComparator<N>) {
        comparators[argClass] = comparator
    }

    @Suppress("UNCHECKED_CAST")
    fun <N : Number?> comparatorOfArg(clazzArg: Class<out ArgumentNumber<N>?>): ArgumentComparator<N> {
        return comparators[clazzArg] as ArgumentComparator<N>
    }

    interface ArgumentComparator<N : Number?> {
        fun greaterThan(n1: N, n2: N): Boolean
        fun greaterThanOrEqual(n1: N, n2: N): Boolean
        fun lessThan(n1: N, n2: N): Boolean
        fun lessThanOrEqual(n1: N, n2: N): Boolean
    }

    private class IntegerComparator : ArgumentComparator<Int> {
        override fun greaterThan(n1: Int, n2: Int): Boolean {
            return n1 > n2
        }

        override fun greaterThanOrEqual(n1: Int, n2: Int): Boolean {
            return n1 >= n2
        }

        override fun lessThan(n1: Int, n2: Int): Boolean {
            return n1 < n2
        }

        override fun lessThanOrEqual(n1: Int, n2: Int): Boolean {
            return n1 <= n2
        }
    }

    private class DoubleComparator : ArgumentComparator<Double> {
        override fun greaterThan(n1: Double, n2: Double): Boolean {
            return n1 > n2
        }

        override fun greaterThanOrEqual(n1: Double, n2: Double): Boolean {
            return n1 >= n2
        }

        override fun lessThan(n1: Double, n2: Double): Boolean {
            return n1 < n2
        }

        override fun lessThanOrEqual(n1: Double, n2: Double): Boolean {
            return n1 <= n2
        }
    }

    private class FloatComparator : ArgumentComparator<Float> {
        override fun greaterThan(n1: Float, n2: Float): Boolean {
            return n1 > n2
        }

        override fun greaterThanOrEqual(n1: Float, n2: Float): Boolean {
            return n1 >= n2
        }

        override fun lessThan(n1: Float, n2: Float): Boolean {
            return n1 < n2
        }

        override fun lessThanOrEqual(n1: Float, n2: Float): Boolean {
            return n1 <= n2
        }
    }

    private class LongComparator : ArgumentComparator<Long> {
        override fun greaterThan(n1: Long, n2: Long): Boolean {
            return n1 > n2
        }

        override fun greaterThanOrEqual(n1: Long, n2: Long): Boolean {
            return n1 >= n2
        }

        override fun lessThan(n1: Long, n2: Long): Boolean {
            return n1 < n2
        }

        override fun lessThanOrEqual(n1: Long, n2: Long): Boolean {
            return n1 <= n2
        }
    }
}