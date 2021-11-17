package io.github.monull.command

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandDispatcher(val children: Map<String, CommandBuilder>, jda: JDA) {
    init {
        val adapter = CommandListener(this)
        jda.addEventListener(adapter)
    }

    internal fun dispatch(event: MessageReceivedEvent) {
        children[event.message.contentRaw]?.let { root ->
            val source = CommandSource(event)
            if (root.requirement?.invoke(source)!!) {
                root.executor?.invoke(source)
            }
        }
    }
}
class CommandDispatcherBuilder(private val jda: JDA) {
    private val buildersByName = LinkedHashMap<String, CommandBuilder>()
    fun register(name: String, init: CommandBuilder.() -> Unit) {
        buildersByName[name] = CommandBuilder(name).apply(init)
    }

    fun build(): CommandDispatcher {
        return CommandDispatcher(buildersByName, jda)
    }
}

class CommandListener(private val dispatcher: CommandDispatcher) : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        dispatcher.dispatch(event)
    }
}