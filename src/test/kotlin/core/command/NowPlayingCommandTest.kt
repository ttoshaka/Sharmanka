package core.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import core.models.Event
import core.models.Member
import core.models.Reply
import core.music.BotAudioPlayer
import core.utils.EmbedFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NowPlayingCommandTest {

    private val botAudioPlayer = mockk<BotAudioPlayer>()
    private val embedFactory = mockk<EmbedFactory>()
    private val guild = mockk<Guild>()
    private val member = mockk<Member>()
    private val event = Event(
        name = "nowplaying",
        guild = guild,
        member = member,
        options = emptyMap(),
    )

    @Test
    fun should_return_no_track_text_when_nothing_playing() = runTest {
        every { botAudioPlayer.getCurrentTrack(guild) } returns null
        val command = NowPlayingCommand(
            botAudioPlayer = botAudioPlayer,
            embedFactory = embedFactory,
        )

        val reply = command.invoke(event)

        assertIs<Reply.Text>(reply)
        assertEquals("No track is currently playing.", reply.value)
    }

    @Test
    fun should_return_embed_reply_when_track_is_playing() = runTest {
        val track = mockk<AudioTrack>(relaxed = true)
        val embed = mockk<MessageEmbed>()
        every { botAudioPlayer.getCurrentTrack(guild) } returns track
        every { embedFactory.buildNowPlayingEmbed(track = track, member = member) } returns embed
        val command = NowPlayingCommand(
            botAudioPlayer = botAudioPlayer,
            embedFactory = embedFactory,
        )

        val reply = command.invoke(event)

        assertIs<Reply.Embed>(reply)
        assertEquals(embed, reply.value)
    }

    @Test
    fun should_call_build_now_playing_embed_with_correct_args() = runTest {
        val track = mockk<AudioTrack>(relaxed = true)
        val embed = mockk<MessageEmbed>()
        every { botAudioPlayer.getCurrentTrack(guild) } returns track
        every { embedFactory.buildNowPlayingEmbed(track = track, member = member) } returns embed
        val command = NowPlayingCommand(
            botAudioPlayer = botAudioPlayer,
            embedFactory = embedFactory,
        )

        command.invoke(event)

        verify(exactly = 1) { embedFactory.buildNowPlayingEmbed(track = track, member = member) }
    }

    @Test
    fun should_have_false_is_long_command() {
        val command = NowPlayingCommand(
            botAudioPlayer = botAudioPlayer,
            embedFactory = embedFactory,
        )
        assertEquals(false, command.isLongCommand)
    }
}
