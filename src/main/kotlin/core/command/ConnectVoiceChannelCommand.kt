package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

class ConnectVoiceChannelCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    override suspend fun invoke(event: Event): Reply {
        val voiceChannel = event.member.voiceChannel
            ?: return Reply.Text("You must be in a voice channel to use this command")

        botAudioPlayer.connectToVoiceChannel(voiceChannel, event.guild)
        return Reply.Text("Connected")
    }
}