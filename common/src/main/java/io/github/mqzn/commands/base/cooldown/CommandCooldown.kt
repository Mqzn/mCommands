package io.github.mqzn.commands.base.cooldown

import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class CommandCooldown(private val value: Long, private val unit: TimeUnit) {
    private val duration: Duration = Duration.of(value, unit.toChronoUnit())

    val isEmpty: Boolean
        get() = value <= 0

    fun toMillis(): Long {
        return duration.toMillis()
    }

    fun value(): Long {
        return value
    }

    fun unit(): TimeUnit {
        return unit
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return if (other !is CommandCooldown) false else value == other.value && unit == other.unit
    }

    override fun hashCode(): Int {
        return Objects.hash(value, unit)
    }

    companion object {
        @JvmField
        val EMPTY = CommandCooldown(0, TimeUnit.SECONDS)
    }
}