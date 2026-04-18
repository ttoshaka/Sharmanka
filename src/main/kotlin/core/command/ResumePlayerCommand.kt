package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

/**
 * Команда возобновления приостановленного воспроизведения.
 *
 * @property botAudioPlayer плеер для управления воспроизведением в Discord
 */
class ResumePlayerCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        botAudioPlayer.resumePlayer(event.guild)
        return Reply.Text("Воспроизведение возобновлено.")
    }
}