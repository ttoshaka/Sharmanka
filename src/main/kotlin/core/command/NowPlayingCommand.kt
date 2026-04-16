package core.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer
import core.utils.DateUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.utils.MarkdownUtil
import java.awt.Color
import java.util.concurrent.TimeUnit

class NowPlayingCommand(
    private val botAudioPlayer: BotAudioPlayer
) : Command() {

    override suspend fun invoke(event: Event): Reply {
        val currentTrack = botAudioPlayer.getCurrentTrack(event.guild)
        
        return if (currentTrack != null) {
            val embed = createNowPlayingEmbed(currentTrack, event)
            Reply.Embed(embed.build())
        } else {
            Reply.Text("No track is currently playing.")
        }
    }

    private fun createNowPlayingEmbed(track: AudioTrack, event: Event): EmbedBuilder {
        val embed = EmbedBuilder()
            .setColor(Color.GREEN)
            .setTitle("🎵 Now Playing")
            .addField("Track", MarkdownUtil.maskedLink(track.info.title, track.info.uri), false)
            .addField("Duration", formatDuration(track), false)
            .addField("Position", getPositionInfo(track), false)
        
        if (track.info.uri.contains("youtube")) {
            val videoId = track.info.uri.substringAfter("v=").substringBefore("&")
            embed.setThumbnail("https://img.youtube.com/vi/$videoId/hqdefault.jpg")
        }
        
        event.member?.nickname?.let {
            embed.setFooter("Requested by $it")
        }
        
        return embed
    }

    private fun formatDuration(track: AudioTrack): String {
        val duration = DateUtils.formatDuration(TimeUnit.MILLISECONDS, track.duration)
        val position = DateUtils.formatDuration(TimeUnit.MILLISECONDS, track.position)
        return "$position / $duration"
    }

    private fun getPositionInfo(track: AudioTrack): String {
        val progressBar = createProgressBar(track.position, track.duration)
        return progressBar
    }

    private fun createProgressBar(position: Long, duration: Long): String {
        val totalBars = 20
        val progress = if (duration > 0) (position.toDouble() / duration.toDouble()) else 0.0
        val filledBars = (progress * totalBars).toInt()
        
        val progressBar = StringBuilder()
        progressBar.append("▬".repeat(filledBars))
        progressBar.append("🔘")
        progressBar.append("▬".repeat(totalBars - filledBars))
        
        return progressBar.toString()
    }
}