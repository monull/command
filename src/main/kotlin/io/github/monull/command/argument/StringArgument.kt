package io.github.monull.command.argument

import com.google.common.collect.ImmutableList
import io.github.monull.command.CommandContext

class StringArgument internal constructor(
    private val values: () -> Collection<String>
) : CommandArgument<String> {
    override fun parse(context: CommandContext, param: String): String? {
        val values = values()

        return param.takeIf { values.isEmpty() || param in values }
    }

    companion object {
        internal val emptyStringArgument = StringArgument { ImmutableList.of() }
    }
}