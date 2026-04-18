package music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.concurrent.TimeUnit

/**
 * Планировщик треков: управляет очередью воспроизведения и реагирует на события плеера.
 *
 * @property player аудиоплеер LavaPlayer, которым управляет планировщик
 */
class TrackScheduler(private val player: AudioPlayer) : AudioEventAdapter() {

    /** Текущий воспроизводимый трек, или `null` если ничего не играет. */
    var currentTrack: AudioTrack? = null

    /** Признак паузы: `true` — воспроизведение приостановлено, `false` — активно. */
    var isPaused: Boolean
        get() = player.isPaused
        set(value) {
            player.isPaused = value
        }
    private val queue = TrackQueue()

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) startNextTrack()
    }

    /**
     * Добавляет трек в очередь или немедленно начинает воспроизведение, если плеер свободен.
     *
     * @param item трек с порядком вставки в очередь
     * @return `true` если трек начал воспроизводиться немедленно, `false` если добавлен в очередь
     */
    fun addAudioItem(item: AudioItem): Boolean {
        val isTrackStarted = startTrack(item.track, true)
        if (!isTrackStarted) queue.addAudioItem(item)
        return isTrackStarted
    }

    /**
     * Добавляет список треков в очередь.
     *
     * @param items список треков с порядками вставки
     * @return `true` если хотя бы один трек начал воспроизводиться немедленно
     */
    fun addAudioItems(items: List<AudioItem>): Boolean {
        var isPlaylistStarted = false
        items.forEach { item ->
            val isTrackStarted = addAudioItem(item)
            if (isTrackStarted) isPlaylistStarted = true
        }
        return isPlaylistStarted
    }

    /** Возвращает текущую очередь треков в порядке воспроизведения. */
    fun getQueue(): List<AudioTrack> =
        queue.getQueue()

    /** Пропускает текущий трек и начинает воспроизведение следующего из очереди. */
    fun skipTrack() {
        startNextTrack(false)
    }

    /** Перемешивает очередь треков в случайном порядке. */
    fun shuffle() {
        queue.shuffle()
    }

    /** Возвращает суммарную длительность очереди в виде отформатированной строки. */
    fun getLength(): String {
        var total = 0L
        getQueue().forEach { track ->
            total += track.duration
        }
        return DateUtils.formatDuration(TimeUnit.MILLISECONDS, total)
    }

    /** Очищает очередь треков. */
    fun clear() {
        queue.clear()
    }

    private fun startNextTrack(noInterrupt: Boolean = true) {
        val nextTrack = queue.poll()?.track
        startTrack(nextTrack, noInterrupt)
    }

    private fun startTrack(track: AudioTrack?, noInterrupt: Boolean): Boolean {
        val isTrackStarted = player.startTrack(track, noInterrupt)
        if (isTrackStarted || track == null) currentTrack = track
        return isTrackStarted
    }
}