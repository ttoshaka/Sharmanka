package core.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer
import core.utils.Constants
import core.utils.DateUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.utils.MarkdownUtil
import java.awt.Color
import java.util.concurrent.TimeUnit

class QueueCommand(//TODO
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    override suspend fun invoke(event: Event): Reply {
        val message = EmbedBuilder()
            .setColor(Color.YELLOW)

        botAudioPlayer.getCurrentTrack(event.guild)?.let { message.addField(getCurrentTrackLine(it)) }
        val tracks = botAudioPlayer.getQueue(event.guild)
        for (i in 0 until Constants.MAX_QUEUE_DISPLAY_SIZE.coerceAtMost(tracks.size)) message.addField(
            getLine(tracks[i], i + 1)
        )
        val member = event.member
        if (member != null) message.setFooter("Requested by ${event.member.nickname}")

        return Reply.Embed(message.build())
    }

    private fun getLine(track: AudioTrack, number: Int): MessageEmbed.Field {
        return MessageEmbed.Field(
            "",
            MarkdownUtil.monospace("$number.") + " " + MarkdownUtil.maskedLink(
                track.info.title,
                track.info.uri
            ) + " | " +
                    MarkdownUtil.monospace(DateUtils.formatDuration(TimeUnit.MILLISECONDS, track.info.length)),
            false
        )
    }

    private fun getCurrentTrackLine(track: AudioTrack): MessageEmbed.Field {
        return MessageEmbed.Field(
            "Current track:",
            MarkdownUtil.maskedLink(track.info.title, track.info.uri) + " | " +
                    MarkdownUtil.monospace(DateUtils.formatDuration(TimeUnit.MILLISECONDS, track.info.length)),
            false
        )
    }
}