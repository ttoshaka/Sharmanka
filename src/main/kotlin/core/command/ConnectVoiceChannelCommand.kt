package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

/**
 * Команда подключения бота к голосовому каналу пользователя.
 *
 * @property botAudioPlayer плеер, управляющий подключением к голосовому каналу
 */
class ConnectVoiceChannelCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        val voiceChannel = event.member.voiceChannel
            ?: return Reply.Text("Вы должны находиться в голосовом канале.")

        botAudioPlayer.connectToVoiceChannel(voiceChannel, event.guild)
        return Reply.Text("Подключён.")
    }
}