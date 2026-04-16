package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

class BackgroundCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    override suspend fun invoke(event: Event): Reply {
        val loopOption = event.options["loop"]
        val loop = loopOption?.lowercase() == "true"

        val started = botAudioPlayer.toggleBackgroundTrack(event.guild, loop)

        val text = if (started) {
            if (loop) "Background track started (looping)." else "Background track started."
        } else {
            "Background track stopped."
        }

        return Reply.Text(text)
    }
}
