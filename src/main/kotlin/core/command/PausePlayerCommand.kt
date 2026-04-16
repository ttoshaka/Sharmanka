package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

class PausePlayerCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {
    override suspend fun invoke(event: Event): Reply {
        botAudioPlayer.pausePlayer(event.guild)
        return Reply.Text("Paused")
    }
}