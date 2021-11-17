package io.github.monull.command.argument

import io.github.monull.command.CommandContext

class IntegerArgument internal constructor() : CommandArgument<Int> {
    override val parseFailMessage: String
        get() = "${CommandArgument.TOKEN} <-- $minimum ~ $maximum 사이의 정수(${radix}진수)가 아닙니다."
    var maximum = Int.MAX_VALUE
        set(value) {
            require(value >= minimum) { "maximum $value was not more than minimum $minimum." }
            field = value
        }

    var minimum = Int.MIN_VALUE
        set(value) {
            require(value <= maximum) { "minimum $value was not less than maximum $maximum." }
            field = value
        }
    var radix = 10
        set(value) {
            require(value in 2..36) { "radix $value was not in valid range 2..36" }
            field = value
        }

    override fun parse(context: CommandContext, param: String): Int? {
        return param.toIntOrNull(radix)?.coerceIn(minimum, maximum)
    }
}