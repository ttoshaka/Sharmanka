package music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.*

class TrackQueue : LinkedList<AudioItem>() {

    fun addAudioItem(item: AudioItem) {
        when (item.order) {
            TrackOrder.NORMAL -> addNormal(item)
            TrackOrder.TOP -> addTop(item)
            TrackOrder.TOP_SEPARATE -> addSeparateTop(item)
        }
    }

    fun getQueue(): List<AudioTrack> =
        map(AudioItem::track)

    private fun addNormal(item: AudioItem): Boolean {
        return super.add(item)
    }

    private fun addTop(item: AudioItem) {
        addFirst(item)
    }

    private fun addSeparateTop(item: AudioItem) {
        val index = indexOfLast { it.order == TrackOrder.TOP_SEPARATE } + 1
        add(index, item)
    }
}