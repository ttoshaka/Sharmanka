package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

class LengthCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    override suspend fun invoke(event: Event): Reply {
        val text = botAudioPlayer.getPlaylistLength(event.guild)
        return Reply.Text(text)
    }
}