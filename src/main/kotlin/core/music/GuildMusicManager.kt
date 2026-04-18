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
    private var backgroundTrackActive: Boolean = false
    private val ttsQueue = LinkedBlockingQueue<AudioTrack>()

    init {
        player.addListener(scheduler)
        backgroundPlayer.addListener(BackgroundTrackHandler())
    }

    /**
     * Устанавливает фоновый трек и начинает его воспроизведение.
     *
     * @param track аудиотрек для воспроизведения в фоне
     * @param loop флаг повторного воспроизведения трека
     */
    fun setBackgroundTrack(track: AudioTrack, loop: Boolean) {
        backgroundTrack = track
        loopBackground = loop
        backgroundTrackActive = true
        backgroundPlayer.playTrack(track.makeClone())
    }

    /**
     * Останавливает воспроизведение фонового трека и сбрасывает связанное состояние.
     */
    fun stopBackgroundTrack() {
        backgroundTrack = null
        loopBackground = false
        backgroundTrackActive = false
        backgroundPlayer.stopTrack()
    }

    /**
     * Возвращает `true`, если в данный момент воспроизводится именно фоновый трек,
     * а не TTS-аудио.
     */
    fun isBackgroundPlaying(): Boolean =
        backgroundTrackActive && backgroundPlayer.playingTrack != null

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