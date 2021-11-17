package io.github.monull.command.argument

import io.github.monull.command.CommandContext

class BooleanArgument : CommandArgument<Boolean> {
    override fun parse(context: CommandContext, param: String): Boolean? {
        return param.toBoolean()
    }
}