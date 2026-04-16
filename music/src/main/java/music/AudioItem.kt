package music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

data class AudioItem(
    val track: AudioTrack,
    val order: TrackOrder
)