package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

class ClearQueueCommand(
    private val botAudioPlayer: BotAudioPlayer
): Command(false) {
    override suspend fun invoke(event: Event): Reply {
        botAudioPlayer.clearQueue(event.guild)
        return Reply.Text("Cleared")
    }
}