package io.github.monull.command

import io.github.monull.command.argument.CommandArgument
import net.dv8tion.jda.api.JDA

abstract class Command(
    val name: String,
    val requirement: (CommandSource.() -> Boolean)?,
    val executor: (CommandSource.(CommandContext) -> Unit)?,
    children: Collection<Command>
) {
    internal val children: List<Command> = children.toList()

    fun getChild(arg: String): Command? {
        for (child in children) {
            if (child is ArgumentCommand
                || (child is LiteralCommand && child.name == arg)
            ) {
                return child
            }
        }

        return null
    }
}

abstract class CommandBuilder(val name: String) {
    internal var requirement: (CommandSource.() -> Boolean)? = null
    internal var executor: (CommandSource.(CommandContext) -> Unit)? = null
    internal val children = LinkedHashSet<CommandBuilder>()

    fun requires(requirement: CommandSource.() -> Boolean) {
        this.requirement = requirement
    }

    fun executes(executor: CommandSource.(CommandContext) -> Unit) {
        this.executor = executor
    }

    fun then(name: String, init: CommandBuilder.() -> Unit) {
        children += LiteralCommandBuilder(name).apply(init)
    }

    fun then(
        argument: Pair<String, CommandArgument<*>>,
        vararg subArguments: Pair<String, CommandArgument<*>>,
        init: CommandBuilder.() -> Unit
    ) {
        var child = ArgumentCommandBuilder(argument.first, argument.second)
        this.children += child

        for ((name, arg) in subArguments) {
            val grandChild = ArgumentCommandBuilder(name, arg)
            child.children += grandChild
            child = grandChild
        }

        child.apply(init)
    }

    internal abstract fun build(): Command
}

fun JDA.command(init: CommandDispatcherBuilder.() -> Unit): CommandDispatcher {
    return CommandDispatcherBuilder(this).apply(init).build()
}