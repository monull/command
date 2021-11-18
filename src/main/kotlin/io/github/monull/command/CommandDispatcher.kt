package io.github.monull.command

import com.google.common.collect.ImmutableList
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandDispatcher(children: Map<String, LiteralCommandBuilder>, jda: JDA) {

    companion object {
        private const val unknownCommandErrorMessage = "알 수 없는 명령입니다."
    }

    private val children: Map<String, LiteralCommand>

    init {
        val builds = HashMap<String, LiteralCommand>()

        for ((command, builder) in children) {
            if (builder.executor == null) {
                builder.executes {
                    message.reply("hi").queue()
                }
            }
            builds[command] = builder.build()
        }

        this.children = builds

        val adapter = CommandListener(children.keys.first(), this)
        jda.addEventListener(adapter)
    }

    private fun parse(command: String, args: Array<out String>, bool: Boolean): CommandContext {
        return children[command]?.let { root ->
            val nodes = ArrayList<Command>()
            nodes.add(root)
            var last: Command = root

            if (bool) {
                for (i in 0 until args.count()) {
                    if (last.children.isEmpty()) throw CommandSyntaxException("인수를 끝내는 공백이 필요하지만, 후행 데이터가 입력되었습니다.")

                    val arg = args[i]
                    val child = last.getChild(arg) ?: throw CommandSyntaxException(unknownCommandErrorMessage)

                    nodes += child
                    last = child
                }
            }

            val executor = last.executor ?: throw CommandSyntaxException(unknownCommandErrorMessage)

            CommandContext(command, args, nodes).apply {
                this.executor = executor
            }
        } ?: throw CommandSyntaxException(unknownCommandErrorMessage)
    }

    internal fun dispatch(event: MessageReceivedEvent) {
        runCatching {
            val raw = event.message.contentRaw.split(" ")
            val command = if (event.message.mentionedUsers.isNotEmpty()) raw[1] else raw[0]
            val args = if (event.message.mentionedUsers.isEmpty()) event.message.contentRaw.removePrefix("$command ").split(" ").toTypedArray() else event.message.contentRaw.removePrefix("${raw[0]} $command ").split(" ").toTypedArray()
            val context = if (raw.size == 1 && event.message.mentionedUsers.isEmpty() || raw.size == 2 && event.message.mentionedUsers.isNotEmpty()) {
                parse(command, args, false)
            } else {
                parse(command, args, true)
            }
            context.executor.invoke(CommandSource(event), context)
        }
    }
}
class CommandDispatcherBuilder(private val jda: JDA) {
    private val buildersByName = LinkedHashMap<String, LiteralCommandBuilder>()
    fun register(name: String, init: CommandBuilder.() -> Unit) {
        buildersByName[name] = LiteralCommandBuilder(name).apply(init)
    }

    fun build(): CommandDispatcher {
        return CommandDispatcher(buildersByName, jda)
    }
}

class CommandListener(private val command: String, private val dispatcher: CommandDispatcher) : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val raw = event.message.contentRaw.split(" ")
        val command = if (event.message.mentionedUsers.isNotEmpty()) raw[1] else raw[0]
        if (this.command == command) {
            dispatcher.dispatch(event)
        }
    }
}