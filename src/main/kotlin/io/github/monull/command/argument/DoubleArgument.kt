package io.github.monull.command.argument

import io.github.monull.command.CommandContext

class DoubleArgument internal constructor() : CommandArgument<Double> {
    override val parseFailMessage: String
        get() = "${CommandArgument.TOKEN} <-- $minimum ~ $maximum 사이의 실수가 아닙니다."

    var maximum = Double.MAX_VALUE
        set(value) {
            require(value >= minimum) { "maximum $value was not more than minimum $minimum." }
            field = value
        }
    var minimum = -Double.MIN_VALUE
        set(value) {
            require(value <= maximum) { "minimum $value was not less than maximum $maximum." }
            field = value
        }
    var allowInfinite = false
    var allowNaN = false

    override fun parse(context: CommandContext, param: String): Double? {
        return param.toDoubleOrNull()?.coerceIn(minimum, maximum)?.takeIf {
            when {
                it.isInfinite() -> allowInfinite
                it.isNaN() -> allowNaN
                else -> true
            }
        }
    }
}