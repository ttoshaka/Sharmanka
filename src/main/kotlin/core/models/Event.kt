package core.models

import net.dv8tion.jda.api.entities.Guild

data class Event(
    val name: String,
    val guild: Guild,
    val member: Member,
    val options: Map<String, String>
)