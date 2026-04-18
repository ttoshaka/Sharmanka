package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

/**
 * Команда пропуска текущего трека.
 *
 * @property botAudioPlayer плеер для управления воспроизведением в Discord
 */
class SkipTrackCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        botAudioPlayer.skipTrack(event.guild)
        return Reply.Text("Трек пропущен.")
    }
}