package core.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import music.TrackScheduler
import java.util.concurrent.LinkedBlockingQueue

class GuildMusicManager(
    mainManager: AudioPlayerManager,
    backgroundManager: AudioPlayerManager
) {

    val player: AudioPlayer = mainManager.createPlayer().apply {
        volume = 30
    }
    val backgroundPlayer: AudioPlayer = backgroundManager.createPlayer().apply {
        volume = 100
    }
    val scheduler: TrackScheduler = TrackScheduler(player)
    val sendHandler: AudioPlayerSendHandler = AudioPlayerSendHandler(player, backgroundPlayer)

    private var backgroundTrack: AudioTrack? = null
    private var loopBackground: Boolean = false
    private val ttsQueue = LinkedBlockingQueue<AudioTrack>()

    init {
        player.addListener(scheduler)
        backgroundPlayer.addListener(BackgroundTrackHandler())
    }

    fun setBackgroundTrack(track: AudioTrack, loop: Boolean) {
        backgroundTrack = track
        loopBackground = loop
        backgroundPlayer.playTrack(track.makeClone())
    }

    fun stopBackgroundTrack() {
        backgroundTrack = null
        loopBackground = false
        backgroundPlayer.stopTrack()
    }

    fun isBackgroundPlaying(): Boolean =
        backgroundPlayer.playingTrack != null

    fun playTtsTrack(track: AudioTrack) {
        if (backgroundPlayer.playingTrack == null) {
            backgroundPlayer.playTrack(track)
        } else {
            ttsQueue.offer(track)
        }
    }

    private inner class BackgroundTrackHandler : AudioEventAdapter() {
        override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
            if (!endReason.mayStartNext) return

            val nextTts = ttsQueue.poll()
            if (nextTts != null) {
                player.playTrack(nextTts)
            } else if (loopBackground) {
                backgroundTrack?.let { player.playTrack(it.makeClone()) }
            }
        }
    }
}