package io.github.mqzn.commands.base.cooldown

import io.github.mqzn.commands.base.caption.Caption
import io.github.mqzn.commands.base.caption.CaptionKey
import io.github.mqzn.commands.base.caption.Message
import io.github.mqzn.commands.base.context.Context
import io.github.mqzn.commands.exceptions.CommandException
import io.github.mqzn.commands.utilities.Pair
import io.github.mqzn.commands.utilities.TimeParser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import java.util.concurrent.TimeUnit

class CooldownCaption<S>(parser: TimeParser) : Caption<S> {
    private val timeData: Pair<Long, TimeUnit>

    init {
        timeData = parser.highestLogicalUnitValue()
    }

    override fun key(): CaptionKey {
        return CaptionKey.COMMAND_IN_COOLDOWN
    }

    override fun <E : CommandException?> message(sender: S, context: Context<S>, exception: E): TextComponent {
        return Message.prefixed(Message.EXECUTION_ERROR)
            .append(
                Component.text(
                    String.format(
                        "Command '" + context.commandUsed().name()
                                + "' is in cooldown for %s %s", timeData.left, formatUnit(timeData.right)
                    ), NamedTextColor.YELLOW
                )
            )
    }

    companion object {
        private fun formatUnit(unit: TimeUnit): String {
            var unitName = unit.name.lowercase(Locale.getDefault())
            unitName = unitName.substring(0, unitName.length - 1)
            return "$unitName(s)"
        }
    }
}