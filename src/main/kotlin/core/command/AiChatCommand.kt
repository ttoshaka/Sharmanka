package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer
import network.AiNetwork
import network.NetworkException
import network.TtsNetwork

/**
 * Команда AI-чата: отправляет сообщение пользователя в DeepSeek и озвучивает ответ через TTS.
 *
 * @property aiNetwork клиент для обращений к DeepSeek AI
 * @property ttsNetwork клиент для синтеза речи
 * @property botAudioPlayer плеер для воспроизведения TTS-аудио в Discord
 */
class AiChatCommand(
    private val aiNetwork: AiNetwork,
    private val ttsNetwork: TtsNetwork,
    private val botAudioPlayer: BotAudioPlayer,
) : Command(true) {

    override suspend fun invoke(event: Event): Reply {
        val message = event.options["message"] ?: return Reply.Text("Empty message.")

        val answer = try {
            aiNetwork.chat(message)
        } catch (e: NetworkException) {
            return Reply.Text("Ошибка при обращении к AI: ${e.message}")
        }

        val audioBytes = ttsNetwork.synthesize(answer)
        if (audioBytes != null) {
            botAudioPlayer.playTtsAudio(event.guild, audioBytes)
        }

        return Reply.Text(answer)
    }
}