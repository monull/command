package io.github.monull.command.argument

import io.github.monull.command.CommandContext
import io.github.monull.command.CommandSource

interface CommandArgument<T> {
    companion object {
        const val TOKEN = "<argument>"
    }

    val parseFailMessage: String
        get() = "$TOKEN <-- 알 수 없는 인수입니다."

    fun parse(context: CommandContext, param: String): T?

}