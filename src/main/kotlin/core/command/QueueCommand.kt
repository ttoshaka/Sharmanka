package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer
import core.utils.EmbedFactory

/**
 * Команда отображения текущей очереди треков
 *
 * @property botAudioPlayer плеер для проигрывания аудио в discord
 * @property embedFactory фабрика для построения embed-сообщений
 */
class QueueCommand(
    private val botAudioPlayer: BotAudioPlayer,
    private val embedFactory: EmbedFactory,
) : Command() {

    override suspend fun invoke(event: Event): Reply {
        val currentTrack = botAudioPlayer.getCurrentTrack(event.guild)
        val queue = botAudioPlayer.getQueue(event.guild)
        return Reply.Embed(
            embedFactory.buildQueueEmbed(
                currentTrack = currentTrack,
                queue = queue,
                member = event.member,
            )
        )
    }
}
