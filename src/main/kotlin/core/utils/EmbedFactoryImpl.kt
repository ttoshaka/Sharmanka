package core.utils

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import core.command.Commands
import core.models.Member
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.utils.MarkdownUtil
import java.awt.Color
import java.util.concurrent.TimeUnit

/**
 * Реализация фабрики embed-сообщений Discord
 */
class EmbedFactoryImpl : EmbedFactory {

    override fun buildHelpEmbed(): MessageEmbed {
        val embed = EmbedBuilder()
            .setTitle("Available Commands")
            .setColor(Color.CYAN)
            .setDescription("Here are all available commands for the bot:")

        Commands.entries.forEach { command ->
            val commandInfo = command.info
            val optionsText = commandInfo.options?.joinToString("\n") {
                "• **${it.name}**: ${it.description}"
            } ?: "No options"

            embed.addField(
                "/${commandInfo.name}",
                "**Description**: ${commandInfo.description}\n**Options**:\n$optionsText",
                false,
            )
        }

        embed.setFooter("Use /<command> to execute a command")

        return embed.build()
    }

    override fun buildQueueEmbed(
        currentTrack: AudioTrack?,
        queue: List<AudioTrack>,
        member: Member,
    ): MessageEmbed {
        val embed = EmbedBuilder()
            .setColor(Color.YELLOW)

        currentTrack?.let { embed.addField(buildCurrentTrackField(it)) }

        val displayCount = Constants.MAX_QUEUE_DISPLAY_SIZE.coerceAtMost(queue.size)
        for (i in 0 until displayCount) {
            embed.addField(buildQueueTrackField(queue[i], i + 1))
        }

        embed.setFooter("Requested by ${member.nickname}")

        return embed.build()
    }

    override fun buildNowPlayingEmbed(
        track: AudioTrack,
        member: Member,
    ): MessageEmbed {
        val embed = EmbedBuilder()
            .setColor(Color.GREEN)
            .setTitle("Now Playing")
            .addField("Track", MarkdownUtil.maskedLink(track.info.title, track.info.uri), false)
            .addField("Duration", formatTrackDuration(track), false)
            .addField("Position", buildProgressBar(track.position, track.duration), false)

        if (track.info.uri.contains("youtube")) {
            val videoId = track.info.uri.substringAfter("v=").substringBefore("&")
            embed.setThumbnail("https://img.youtube.com/vi/$videoId/hqdefault.jpg")
        }

        embed.setFooter("Requested by ${member.nickname}")

        return embed.build()
    }

    private fun buildCurrentTrackField(track: AudioTrack): MessageEmbed.Field =
        MessageEmbed.Field(
            "Current track:",
            MarkdownUtil.maskedLink(track.info.title, track.info.uri) + " | " +
                MarkdownUtil.monospace(DateUtils.formatDuration(TimeUnit.MILLISECONDS, track.info.length)),
            false,
        )

    private fun buildQueueTrackField(track: AudioTrack, number: Int): MessageEmbed.Field =
        MessageEmbed.Field(
            "",
            MarkdownUtil.monospace("$number.") + " " +
                MarkdownUtil.maskedLink(track.info.title, track.info.uri) + " | " +
                MarkdownUtil.monospace(DateUtils.formatDuration(TimeUnit.MILLISECONDS, track.info.length)),
            false,
        )

    private fun formatTrackDuration(track: AudioTrack): String {
        val duration = DateUtils.formatDuration(TimeUnit.MILLISECONDS, track.duration)
        val position = DateUtils.formatDuration(TimeUnit.MILLISECONDS, track.position)
        return "$position / $duration"
    }

    private fun buildProgressBar(position: Long, duration: Long): String {
        val totalBars = 20
        val progress = if (duration > 0) position.toDouble() / duration.toDouble() else 0.0
        val filledBars = (progress * totalBars).toInt()
        return "▬".repeat(filledBars) + "🔘" + "▬".repeat(totalBars - filledBars)
    }
}
