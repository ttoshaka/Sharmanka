package core.command

import core.models.Event
import core.models.MusicForPlayingParameters
import core.models.Reply
import core.music.BotAudioPlayer
import music.TrackOrder

/**
 * Команда воспроизведения музыки из URL или по ключевому слову.
 *
 * @property botAudioPlayer плеер для воспроизведения аудио в Discord
 * @property order порядок добавления трека в очередь
 */
class PlayMusicCommand(
    private val botAudioPlayer: BotAudioPlayer,
    private val order: TrackOrder,
) : Command(true) {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        val source = event.options.get("source") ?: return Reply.Text("Источник не указан.")
        val result = botAudioPlayer.loadMusic(
            text = source,
            parameters = MusicForPlayingParameters(
                order = order,
                guild = event.guild,
            )
        )
        val text = result.toText()
        return Reply.Text(text)
    }
}