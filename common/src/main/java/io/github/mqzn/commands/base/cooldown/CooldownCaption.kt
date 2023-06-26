package io.github.mqzn.commands.base.cooldown

import io.github.mqzn.commands.base.caption.Caption
import io.github.mqzn.commands.base.caption.CaptionKey
import io.github.mqzn.commands.base.caption.Message
import io.github.mqzn.commands.base.context.Context
import io.github.mqzn.commands.exceptions.CommandException
import io.github.mqzn.commands.utilities.TimeParser
import io.github.mqzn.commands.utilities.TimeParser.Companion.parse
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import java.util.concurrent.TimeUnit

open class CooldownCaption<S> : Caption<S> {

    override fun key(): CaptionKey {
        return CaptionKey.COMMAND_IN_COOLDOWN
    }

    override fun <E : CommandException?> message(sender: S, context: Context<S>, exception: E): TextComponent {
        val command = context.commandUsed()
        val manager = command.manager()
        val cooldown: CommandCooldown = command.cooldown()
        val lastTimeCommandExecuted: Long = manager.getCommandCooldown(manager.senderWrapper.senderName(sender))!!

        //send a caption telling the user that he's in a cool down
        //calculating remaining time
        val parser: TimeParser = parse(calculateRemainingTime(lastTimeCommandExecuted, cooldown))
        val timeData = parser.highestLogicalUnitValue()

        return cooldownMessage(timeData.left, timeData.right, context)
    }

    open fun cooldownMessage(time: Long, unit: TimeUnit, context: Context<S>) : TextComponent {
        return Message.prefixed(Message.EXECUTION_ERROR)
            .append(
                Component.text(
                    String.format(
                        "Command '" + context.commandUsed().name()
                                + "' is in cooldown for %s %s", time, formatUnit(unit)
                    ), NamedTextColor.YELLOW
                )
            )
    }

    companion object {
        fun formatUnit(unit: TimeUnit): String {
            var unitName = unit.name.lowercase(Locale.getDefault())
            unitName = unitName.substring(0, unitName.length - 1)
            return "$unitName(s)"
        }

        fun calculateRemainingTime(lastTime: Long, commandCooldown: CommandCooldown): Long {
            val diff = System.currentTimeMillis() - lastTime
            return commandCooldown.toMillis() - diff
        }
    }
}