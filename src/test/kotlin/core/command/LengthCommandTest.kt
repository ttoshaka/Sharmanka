package core.command

import core.models.Event
import core.models.Member
import core.models.Reply
import core.music.BotAudioPlayer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.dv8tion.jda.api.entities.Guild
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class LengthCommandTest {

    private val botAudioPlayer = mockk<BotAudioPlayer>()
    private val guild = mockk<Guild>()
    private val event = Event(
        name = "length",
        guild = guild,
        member = mockk<Member>(),
        options = emptyMap(),
    )

    @Test
    fun should_return_text_reply_with_playlist_length() = runTest {
        every { botAudioPlayer.getPlaylistLength(guild) } returns "12:34"
        val command = LengthCommand(botAudioPlayer)

        val reply = command.invoke(event)

        assertIs<Reply.Text>(reply)
        assertEquals("12:34", reply.value)
    }

    @Test
    fun should_return_zero_duration_when_queue_empty() = runTest {
        every { botAudioPlayer.getPlaylistLength(guild) } returns "00:00"
        val command = LengthCommand(botAudioPlayer)

        val reply = command.invoke(event)

        assertIs<Reply.Text>(reply)
        assertEquals("00:00", reply.value)
    }

    @Test
    fun should_have_false_is_long_command() {
        val command = LengthCommand(botAudioPlayer)
        assertEquals(false, command.isLongCommand)
    }
}
