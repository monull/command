package io.github.monull.command

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class CommandSource(val event: MessageReceivedEvent) {
    val author: User
        get() = event.author
    val message: Message
        get() = event.message
    val responseNumber: Long
        get() = event.responseNumber
    val member: Member
        get() = event.member!!
    val isWebHookMessage: Boolean
        get() = event.isWebhookMessage
    val channel: MessageChannel
        get() = event.channel
    val channelType: ChannelType
        get() = event.channelType
    val guild: Guild
        get() = event.guild
    val isFromGuild: Boolean
        get() = event.isFromGuild
    val messageId: String
        get() = event.messageId
    val messageIdLong: Long
        get() = event.messageIdLong
    val privateChannel: PrivateChannel
        get() = event.privateChannel
    val textChannel: TextChannel
        get() = event.textChannel
}