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

class ClearQueueCommandTest {

    private val botAudioPlayer = mockk<BotAudioPlayer>()
    private val guild = mockk<Guild>()
    private val event = Event(
        name = "clear",
        guild = guild,
        member = mockk<Member>(),
        options = emptyMap(),
    )

    @Test
    fun should_return_cleared_text_reply() = runTest {
        justRun { botAudioPlayer.clearQueue(guild) }
        val command = ClearQueueCommand(botAudioPlayer)

        val reply = command.invoke(event)

        assertIs<Reply.Text>(reply)
        assertEquals("Cleared", reply.value)
    }

    @Test
    fun should_call_clear_queue_on_player() = runTest {
        justRun { botAudioPlayer.clearQueue(guild) }
        val command = ClearQueueCommand(botAudioPlayer)

        command.invoke(event)

        verify(exactly = 1) { botAudioPlayer.clearQueue(guild) }
    }

    @Test
    fun should_have_false_is_long_command() {
        val command = ClearQueueCommand(botAudioPlayer)
        assertEquals(false, command.isLongCommand)
    }
}
