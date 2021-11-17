package io.github.monull.command

import io.github.monull.command.argument.CommandArgument

class ArgumentCommand(
    name: String,
    requirement: (CommandSource.() -> Boolean)?,
    executor: (CommandSource.(CommandContext) -> Unit)?,
    children: Collection<Command>,
    internal val argument: CommandArgument<*>
) : Command(name, requirement, executor, children)

internal class ArgumentCommandBuilder(
    name: String,
    val argument: CommandArgument<*>
) : CommandBuilder(name) {
    override fun build(): ArgumentCommand {
        return ArgumentCommand(name, requirement, executor, children.map { it.build() }, argument)
    }

    override fun hashCode() = name.hashCode().inv()

    override fun equals(other: Any?): Boolean {
        if (other == this) return true

        if (other is ArgumentCommandBuilder) {
            if (name == other.name && argument == other.argument)
                return true
        }

        return false
    }

}