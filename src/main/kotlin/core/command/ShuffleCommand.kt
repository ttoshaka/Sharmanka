package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

/**
 * Команда перемешивания очереди треков в случайном порядке.
 *
 * @property botAudioPlayer плеер для управления очередью в Discord
 */
class ShuffleCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        botAudioPlayer.shuffle(event.guild)
        return Reply.Text("Очередь перемешана.")
    }
}