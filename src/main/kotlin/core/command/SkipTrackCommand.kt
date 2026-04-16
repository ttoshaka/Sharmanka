package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

class SkipTrackCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    override suspend fun invoke(event: Event): Reply {
        botAudioPlayer.skipTrack(event.guild)
        return Reply.Text("Skipped")
    }
}