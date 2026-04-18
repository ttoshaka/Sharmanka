package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

/**
 * Команда отображения суммарной длительности очереди треков.
 *
 * @property botAudioPlayer плеер для получения информации об очереди
 */
class LengthCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        val text = botAudioPlayer.getPlaylistLength(event.guild)
        return Reply.Text(text)
    }
}