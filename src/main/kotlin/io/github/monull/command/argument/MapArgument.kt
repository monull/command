package io.github.monull.command.argument

import io.github.monull.command.CommandContext

class MapArgument<T> internal constructor(
    private val map: Map<String, T>
) : CommandArgument<T> {
    override fun parse(context: CommandContext, param: String): T? {
        return map[param]
    }
}