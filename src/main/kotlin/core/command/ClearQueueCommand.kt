package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

/**
 * Команда очистки очереди треков.
 *
 * @property botAudioPlayer плеер для управления очередью в Discord
 */
class ClearQueueCommand(
    private val botAudioPlayer: BotAudioPlayer
): Command(false) {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        botAudioPlayer.clearQueue(event.guild)
        return Reply.Text("Очередь очищена.")
    }
}