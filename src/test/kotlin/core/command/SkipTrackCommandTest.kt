package core.command

import core.models.Event
import core.models.Member
import core.models.Reply
import core.music.BotAudioPlayer
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import net.dv8tion.jda.api.entities.Guild
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SkipTrackCommandTest {

    private val botAudioPlayer = mockk<BotAudioPlayer>()
    private val guild = mockk<Guild>()
    private val event = Event(
        name = "skip",
        guild = guild,
        member = mockk<Member>(),
        options = emptyMap(),
    )

    @Test
    fun should_return_skipped_text_reply() = runTest {
        justRun { botAudioPlayer.skipTrack(guild) }
        val command = SkipTrackCommand(botAudioPlayer)

        val reply = command.invoke(event)

        assertIs<Reply.Text>(reply)
        assertEquals("Skipped", reply.value)
    }

    @Test
    fun should_call_skip_track_on_player() = runTest {
        justRun { botAudioPlayer.skipTrack(guild) }
        val command = SkipTrackCommand(botAudioPlayer)

        command.invoke(event)

        verify(exactly = 1) { botAudioPlayer.skipTrack(guild) }
    }

    @Test
    fun should_have_false_is_long_command() {
        val command = SkipTrackCommand(botAudioPlayer)
        assertEquals(false, command.isLongCommand)
    }
}
