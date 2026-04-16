package core.command

import core.AudioScheduleResult
import core.models.Event
import core.models.MusicForPlayingParameters
import core.models.Reply
import core.music.BotAudioPlayer
import music.TrackOrder

class PlayMusicCommand(
    private val botAudioPlayer: BotAudioPlayer,
    private val order: TrackOrder,
) : Command(true) {

    override suspend fun invoke(event: Event): Reply {
        val source = event.options.get("source") ?: return Reply.Text("Empty source.")
        val result = botAudioPlayer.loadMusic(
            text = source,
            parameters = MusicForPlayingParameters(
                order = order,
                guild = event.guild,
            )
        )
        val text = when (result) {
            is AudioScheduleResult.Error -> "Error ${result.exception}."
            AudioScheduleResult.NoMatches -> "No matches."
            is AudioScheduleResult.PlaylistAdded -> "Playlist ${result.playlistName} loaded."
            is AudioScheduleResult.PlaylistPlaying -> "Playlist ${result.playlistName} loaded. Now playing - ${result.trackName}."
            is AudioScheduleResult.TrackAdded -> "Track ${result.trackName} added."
            is AudioScheduleResult.TrackPlaying -> "Track ${result.trackName} playing."
            is AudioScheduleResult.EmptyPlaylist -> "Playlist ${result.playlistName} is empty."
            is AudioScheduleResult.TrackNotFound -> "Nothing was found for \"${result.keyword}\""
        }
        return Reply.Text(text)
    }
}