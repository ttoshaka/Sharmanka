package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

/**
 * Команда переключения фонового трека.
 *
 * @property botAudioPlayer плеер для управления фоновым воспроизведением в Discord
 */
class BackgroundCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        val loopOption = event.options["loop"]
        val loop = loopOption?.lowercase() == "true"

        val started = botAudioPlayer.toggleBackgroundTrack(event.guild, loop)

        val text = if (started) {
            if (loop) "Фоновый трек запущен (с повтором)." else "Фоновый трек запущен."
        } else {
            "Фоновый трек остановлен."
        }

        return Reply.Text(text)
    }
}
