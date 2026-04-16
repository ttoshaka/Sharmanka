package core.music

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import core.AudioScheduleResult
import core.core.music.PorcupineReceiveHandler
import core.models.MusicForPlayingParameters
import core.utils.UrlValidator
import dev.lavalink.youtube.YoutubeAudioSourceManager
import music.AudioItem
import music.TrackOrder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel
import network.YoutubeNetwork
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BotAudioPlayer(
    private val youtubeNetwork: YoutubeNetwork,
    private val porcupineKey: String,
) {

    private val musicManagers = hashMapOf<String, GuildMusicManager>()
    private val sourceManager = YoutubeAudioSourceManager()
    private val playerManager = DefaultAudioPlayerManager().apply {
        configuration.outputFormat = StandardAudioDataFormats.DISCORD_PCM_S16_BE
        registerSourceManager(TwitchStreamAudioSourceManager())
        registerSourceManager(sourceManager)
    }
    private val backgroundPlayerManager = DefaultAudioPlayerManager().apply {
        configuration.outputFormat = StandardAudioDataFormats.DISCORD_PCM_S16_BE
        registerSourceManager(LocalAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY))
    }
    private val backgroundTrackPath = File("recorded.wav").absolutePath

    fun connectToVoiceChannel(voiceChannel: AudioChannel, guild: Guild) {
        if (guild.audioManager.connectedChannel != voiceChannel) {
            val musicManager = getMusicManager(guild)
            guild.audioManager.sendingHandler = musicManager.sendHandler
            guild.audioManager.openAudioConnection(voiceChannel)
            guild.audioManager.receivingHandler = PorcupineReceiveHandler(porcupineKey)
        }
    }

    fun toggleBackgroundTrack(guild: Guild, loop: Boolean): Boolean {
        val musicManager = getMusicManager(guild)
        return if (musicManager.isBackgroundPlaying()) {
            musicManager.stopBackgroundTrack()
            false
        } else {
            loadBackgroundTrack(musicManager, loop)
            true
        }
    }

    fun isBackgroundPlaying(guild: Guild): Boolean =
        getMusicManager(guild).isBackgroundPlaying()

    private fun loadBackgroundTrack(musicManager: GuildMusicManager, loop: Boolean) {
        backgroundPlayerManager.loadItem(
            backgroundTrackPath,
            FunctionalResultHandler(
                { track -> musicManager.setBackgroundTrack(track, loop) },
                { /* playlist - ignore */ },
                { /* no matches */ },
                { exception -> println("Failed to load background track: ${exception.message}") }
            )
        )
    }

    fun playTtsAudio(guild: Guild, audioBytes: ByteArray) {
        val musicManager = getMusicManager(guild)
        val tempFile = File.createTempFile("tts_", ".wav")
        tempFile.deleteOnExit()
        tempFile.writeBytes(audioBytes)

        backgroundPlayerManager.loadItem(
            tempFile.absolutePath,
            FunctionalResultHandler(
                { track -> musicManager.playTtsTrack(track) },
                { /* playlist - ignore */ },
                { /* no matches */ },
                { exception -> println("Failed to load TTS audio: ${exception.message}") }
            )
        )
    }

    fun skipTrack(guild: Guild) {
        getMusicManager(guild).scheduler.skipTrack()
    }

    fun getQueue(guild: Guild): List<AudioTrack> =
        getMusicManager(guild).scheduler.getQueue()

    fun getCurrentTrack(guild: Guild): AudioTrack? =
        getMusicManager(guild).scheduler.currentTrack

    fun pausePlayer(guild: Guild) {
        changePlayingState(guild, true)
    }

    fun resumePlayer(guild: Guild) {
        changePlayingState(guild, false)
    }

    fun shuffle(guild: Guild) {
        getMusicManager(guild).scheduler.shuffle()
    }

    fun getPlaylistLength(guild: Guild): String =
        getMusicManager(guild).scheduler.getLength()

    fun clearQueue(guild: Guild) {
        getMusicManager(guild).scheduler.clear()
    }

    suspend fun loadMusic(text: String, parameters: MusicForPlayingParameters): AudioScheduleResult =
        if (UrlValidator.isValidUrl(text)) {
            loadTrackByUrl(text, parameters)
        } else {
            loadTrackByKeyword(text, parameters)
        }

    private fun getMusicManager(guild: Guild): GuildMusicManager {
        var musicManager = musicManagers[guild.id]
        if (musicManager == null) {
            musicManager = GuildMusicManager(playerManager, backgroundPlayerManager)
            musicManagers[guild.id] = musicManager
        }
        return musicManager
    }


    private suspend fun loadTrackByKeyword(
        keyword: String,
        parameters: MusicForPlayingParameters
    ): AudioScheduleResult {
        val url = youtubeNetwork.findVideo(keyword) ?: return AudioScheduleResult.TrackNotFound(keyword)
        return loadTrackByUrl(url = url, parameters = parameters)
    }

    private suspend fun loadTrackByUrl(url: String, parameters: MusicForPlayingParameters): AudioScheduleResult =
        suspendCoroutine { cont ->
            playerManager.loadItem(
                url, FunctionalResultHandler(
                    { track ->
                        cont.resume(
                            onTrackLoaded(
                                track = track,
                                order = parameters.order,
                                guild = parameters.guild
                            )
                        )
                    },
                    { track ->
                        cont.resume(
                            onPlaylistLoaded(
                                playlist = track,
                                order = parameters.order,
                                guild = parameters.guild
                            )
                        )
                    },
                    { cont.resume(onNoMatches()) },
                    { cont.resume(onErrorLoadMusic(it)) })
            )
        }

    private fun onTrackLoaded(
        track: AudioTrack,
        order: TrackOrder,
        guild: Guild
    ): AudioScheduleResult {
        val trackName = track.info.title
        val scheduler = getMusicManager(guild).scheduler
        val trackIsPlaying = scheduler.addAudioItem(track.toAudioItem(order))
        return if (trackIsPlaying) {
            AudioScheduleResult.TrackPlaying(trackName)
        } else {
            AudioScheduleResult.TrackAdded(trackName)
        }
    }

    private fun onPlaylistLoaded(
        playlist: AudioPlaylist,
        order: TrackOrder,
        guild: Guild
    ): AudioScheduleResult {
        val playlistName = playlist.name
        if (playlist.tracks.isEmpty())
            return AudioScheduleResult.EmptyPlaylist(playlistName)
        val tracks = playlist.tracks
        val trackScheduler = getMusicManager(guild).scheduler
        val isPlaylistPlaying = trackScheduler.addAudioItems(tracks.map { it.toAudioItem(order) })
        return if (isPlaylistPlaying) {
            AudioScheduleResult.PlaylistPlaying(tracks.first().info.title, playlistName)
        } else {
            AudioScheduleResult.PlaylistAdded(playlistName)
        }
    }

    private fun onNoMatches(): AudioScheduleResult =
        AudioScheduleResult.NoMatches

    private fun onErrorLoadMusic(exception: Exception): AudioScheduleResult =
        AudioScheduleResult.Error(exception)

    private fun AudioTrack.toAudioItem(order: TrackOrder): AudioItem =
        AudioItem(this, order)

    private fun changePlayingState(guild: Guild, isPause: Boolean) {
        getMusicManager(guild).scheduler.isPaused = isPause
    }
}