package io.github.mqzn.commands.utilities

import lombok.Getter
import java.util.concurrent.TimeUnit

/**
 * The main class that is used to parse time strings inputs
 * The algorithm is fully written by me (aka Mqzen)
 * Make sure to know just when to use it properly :D !
 *
 * @author Mqzen
 */
class TimeParser {
    @Getter
    private var days: Long = 0

    @Getter
    private var hours: Long = 0

    @Getter
    private var minutes: Long = 0

    @Getter
    private var seconds: Long = 0

    private constructor(millis: Long) {
        days = millis / 86400000L
        hours = millis / 3600000L % 24L
        minutes = millis / 60000L % 60L
        seconds = millis / 1000L % 60L
    }

    private constructor(timePeriod: String) {
        val chars = timePeriod.toCharArray()
        var i = 0
        while (i < timePeriod.length) {
            if (Character.isDigit(chars[i])) {
                val digitToCollect = StringBuilder()
                var start = i
                while (Character.isDigit(chars[start])) {
                    digitToCollect.append(chars[start])
                    start++
                }

                //the current index is the end of the digit to collect
                //so the current index is that of a unit char
                val unit = chars[start]
                val digit = digitToCollect.toString().toInt()
                when (unit) {
                    'd', 'D' -> days += digit.toLong()
                    'h', 'H' -> hours += digit.toLong()
                    'm', 'M' -> minutes += digit.toLong()
                    's', 'S' -> seconds += digit.toLong()
                }
                i = start
            }
            i++
        }
    }

    fun highestLogicalUnitValue(): Pair<Long, TimeUnit> {
        if (days != 0L) return Pair.of(days, TimeUnit.DAYS)
        if (hours != 0L) return Pair.of(hours, TimeUnit.HOURS)
        return if (minutes != 0L) Pair.of(minutes, TimeUnit.MINUTES) else Pair.of(seconds, TimeUnit.SECONDS)
    }

    companion object {
        fun parse(timePeriod: String): TimeParser {
            return TimeParser(timePeriod)
        }

        @JvmStatic
        fun parse(millis: Long): TimeParser {
            return TimeParser(millis)
        }
    }
}