package core.models

import music.TrackOrder
import net.dv8tion.jda.api.entities.Guild

data class MusicForPlayingParameters(
    val order: TrackOrder,
    val guild: Guild,
)