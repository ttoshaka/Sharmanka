package music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.Locale
import java.util.concurrent.TimeUnit

class TrackScheduler(private val player: AudioPlayer) : AudioEventAdapter() {

    var currentTrack: AudioTrack? = null
    var isPaused: Boolean
        get() = player.isPaused
        set(value) {
            player.isPaused = value
        }
    private val queue = TrackQueue()

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) startNextTrack()
    }

    fun addAudioItem(item: AudioItem): Boolean {
        val isTrackStarted = startTrack(item.track, true)
        if (!isTrackStarted) queue.addAudioItem(item)
        return isTrackStarted
    }

    fun addAudioItems(items: List<AudioItem>): Boolean {
        var isPlaylistStarted = false
        items.forEach { item ->
            val isTrackStarted = addAudioItem(item)
            if (isTrackStarted) isPlaylistStarted = true
        }
        return isPlaylistStarted
    }

    fun getQueue(): List<AudioTrack> =
        queue.getQueue()

    fun skipTrack() {
        startNextTrack(false)
    }

    fun shuffle() {
        queue.shuffle()
    }

    fun getLength(): String {
        var total = 0L
        getQueue().forEach { track ->
            total += track.duration
        }
        return formatDuration(TimeUnit.MILLISECONDS, total)
    }

    fun formatDuration(unit: TimeUnit, value: Long): String {
        val time = if (value > 0) value else -value
        val seconds = unit.toSeconds(time) % 60
        val minutes = unit.toMinutes(time) % 60
        val hours = unit.toHours(time)
        val formattedMinutesSeconds = String.format(Locale.ENGLISH, "%1$02d:%2$02d", minutes, seconds)
        return StringBuilder()
            .append(
                if (hours != 0L) {
                    String.format(Locale.ENGLISH, "%d:%s", hours, formattedMinutesSeconds)
                } else {
                    formattedMinutesSeconds
                }
            )
            .toString()
    }

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