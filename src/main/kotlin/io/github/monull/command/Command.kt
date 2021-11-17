package io.github.monull.command

import net.dv8tion.jda.api.JDA

class CommandBuilder(val name: String) {
    internal var requirement: (CommandSource.() -> Boolean)? = null
    internal var executor: (CommandSource.() -> Unit)? = null

    fun requires(requirement: CommandSource.() -> Boolean) {
        this.requirement = requirement
    }

    fun executes(executor: CommandSource.() -> Unit) {
        this.executor = executor
    }
}

fun JDA.command(init: CommandDispatcherBuilder.() -> Unit): CommandDispatcher {
    return CommandDispatcherBuilder(this).apply(init).build()
}