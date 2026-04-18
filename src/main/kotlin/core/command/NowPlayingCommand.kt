package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer
import core.utils.EmbedFactory

/**
 * Команда отображения текущего воспроизводимого трека
 *
 * @property botAudioPlayer плеер для проигрывания аудио в discord
 * @property embedFactory фабрика для построения embed-сообщений
 */
class NowPlayingCommand(
    private val botAudioPlayer: BotAudioPlayer,
    private val embedFactory: EmbedFactory,
) : Command() {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        val currentTrack = botAudioPlayer.getCurrentTrack(event.guild)
        return if (currentTrack != null) {
            Reply.Embed(
                embedFactory.buildNowPlayingEmbed(
                    track = currentTrack,
                    member = event.member,
                )
            )
        } else {
            Reply.Text("Сейчас ничего не играет.")
        }
    }
}
