package core.models

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel

data class Member(
    val voiceChannel: AudioChannel?,
    val messageChannel: MessageChannel,
    val nickname: String,
    val avatarUrl: String
)