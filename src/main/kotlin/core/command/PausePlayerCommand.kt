package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

/**
 * Команда паузы текущего воспроизведения.
 *
 * @property botAudioPlayer плеер для управления воспроизведением в Discord
 */
class PausePlayerCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        botAudioPlayer.pausePlayer(event.guild)
        return Reply.Text("Воспроизведение приостановлено.")
    }
}