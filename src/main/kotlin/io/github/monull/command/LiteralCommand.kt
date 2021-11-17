package io.github.monull.command


class LiteralCommand(
    name: String,
    requirement: (CommandSource.() -> Boolean)?,
    executor: (CommandSource.(CommandContext) -> Unit)?,
    children: Collection<Command>
) : Command(name, requirement, executor, children)

class LiteralCommandBuilder(name: String) : CommandBuilder(name) {
    override fun build(): LiteralCommand {
        return LiteralCommand(name, requirement, executor, children.map { it.build() }.toSet())
    }

    override fun hashCode() = name.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == this) return true

        if (other is LiteralCommandBuilder) {
            if (name == other.name) return true
        }

        return false
    }

}