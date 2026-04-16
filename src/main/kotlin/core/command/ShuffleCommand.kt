package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

class ShuffleCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {
    override suspend fun invoke(event: Event): Reply {
        botAudioPlayer.shuffle(event.guild)
        return Reply.Text("Shuffled")
    }
}