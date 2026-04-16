package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer
import network.AiNetwork
import network.TtsNetwork

class AiChatCommand(
    private val aiNetwork: AiNetwork,
    private val ttsNetwork: TtsNetwork,
    private val botAudioPlayer: BotAudioPlayer,
) : Command(true) {
    override suspend fun invoke(event: Event): Reply {
        val message = event.options["message"] ?: return Reply.Text("Empty message.")
        val answer = aiNetwork.chat(message)

        val audioBytes = ttsNetwork.synthesize(answer)
        if (audioBytes != null) {
            botAudioPlayer.playTtsAudio(event.guild, audioBytes)
        }

        return Reply.Text(answer)
    }
}