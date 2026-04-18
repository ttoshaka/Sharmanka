package core.command

import core.AudioScheduleResult
import core.models.Event
import core.models.MusicForPlayingParameters
import core.models.Reply
import core.music.BotAudioPlayer
import music.TrackOrder

/**
 * Команда воспроизведения трека Rob Zombie — Dragula.
 *
 * Подключает бота к голосовому каналу пользователя и ставит трек
 * Rob Zombie — Dragula через поиск по ключевому слову в YouTube.
 *
 * @property botAudioPlayer плеер для воспроизведения аудио в Discord
 */
class DragulaCommand(
    private val botAudioPlayer: BotAudioPlayer,
) : Command(true) {

    override suspend fun invoke(event: Event): Reply {
        val voiceChannel = event.member.voiceChannel
            ?: return Reply.Text("You must be in a voice channel to use this command")

        botAudioPlayer.connectToVoiceChannel(
            voiceChannel = voiceChannel,
            guild = event.guild,
        )

        val result = botAudioPlayer.loadMusic(
            text = SEARCH_KEYWORD,
            parameters = MusicForPlayingParameters(
                order = TrackOrder.NORMAL,
                guild = event.guild,
            ),
        )

        return when (result) {
            is AudioScheduleResult.TrackPlaying,
            is AudioScheduleResult.TrackAdded -> Reply.Text("Dragula!")
            is AudioScheduleResult.TrackNotFound -> Reply.Text("Track not found.")
            is AudioScheduleResult.Error -> Reply.Text("Error: ${result.exception.message}")
            else -> Reply.Text("Something went wrong.")
        }
    }

    private companion object {
        const val SEARCH_KEYWORD = "Rob Zombie Dragula"
    }
}
