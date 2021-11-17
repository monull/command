package io.github.monull.command

import io.github.monull.command.argument.CommandArgument

class CommandContext(
    val command: String,
    val rawArguments: Array<out String>,
    val nodes: List<Command>
){
    internal lateinit var executor: CommandSource.(CommandContext) -> Unit

    private val argumentsByName: Map<String, Pair<String, CommandArgument<*>>>

    init {
        val arguments = HashMap<String, Pair<String, CommandArgument<*>>>()

        nodes.forEachIndexed { index, kommand ->
            if (kommand is ArgumentCommand) {
                arguments[kommand.name] = rawArguments[index - 1] to kommand.argument
            }
        }

        this.argumentsByName = arguments
    }

    private fun argumentBy(name: String): Pair<String, CommandArgument<*>> {
        return argumentsByName[name] ?: throw IllegalArgumentException("[$name] is unknown argument name")
    }

    fun getArgument(name: String) = argumentBy(name).first

    @Suppress("UNCHECKED_CAST")
    fun <T> parseOrNullArgument(name: String): T? {
        val pair = argumentBy(name)
        val param = pair.first
        val argument = pair.second

        return argument.parse(this, param) as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> parseArgument(name: String): T {
        val pair = argumentBy(name)
        val param = pair.first
        val argument = pair.second

        return argument.parse(this, param) as T
            ?: throw CommandSyntaxException(argument.parseFailMessage.replace(CommandArgument.TOKEN, param))
    }
}