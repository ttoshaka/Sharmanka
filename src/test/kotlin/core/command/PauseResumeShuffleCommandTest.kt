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

class PauseResumeShuffleCommandTest {

    private val botAudioPlayer = mockk<BotAudioPlayer>()
    private val guild = mockk<Guild>()

    private fun makeEvent(name: String) = Event(
        name = name,
        guild = guild,
        member = mockk<Member>(),
        options = emptyMap(),
    )

    @Test
    fun should_pause_player_and_return_paused_reply() = runTest {
        justRun { botAudioPlayer.pausePlayer(guild) }
        val command = PausePlayerCommand(botAudioPlayer)

        val reply = command.invoke(makeEvent("pause"))

        assertIs<Reply.Text>(reply)
        assertEquals("Paused", reply.value)
        verify(exactly = 1) { botAudioPlayer.pausePlayer(guild) }
    }

    @Test
    fun should_resume_player_and_return_resumed_reply() = runTest {
        justRun { botAudioPlayer.resumePlayer(guild) }
        val command = ResumePlayerCommand(botAudioPlayer)

        val reply = command.invoke(makeEvent("resume"))

        assertIs<Reply.Text>(reply)
        assertEquals("Resumed", reply.value)
        verify(exactly = 1) { botAudioPlayer.resumePlayer(guild) }
    }

    @Test
    fun should_shuffle_queue_and_return_shuffled_reply() = runTest {
        justRun { botAudioPlayer.shuffle(guild) }
        val command = ShuffleCommand(botAudioPlayer)

        val reply = command.invoke(makeEvent("shuffle"))

        assertIs<Reply.Text>(reply)
        assertEquals("Shuffled", reply.value)
        verify(exactly = 1) { botAudioPlayer.shuffle(guild) }
    }

    @Test
    fun should_have_false_is_long_command_for_pause() {
        assertEquals(false, PausePlayerCommand(botAudioPlayer).isLongCommand)
    }

    @Test
    fun should_have_false_is_long_command_for_resume() {
        assertEquals(false, ResumePlayerCommand(botAudioPlayer).isLongCommand)
    }

    @Test
    fun should_have_false_is_long_command_for_shuffle() {
        assertEquals(false, ShuffleCommand(botAudioPlayer).isLongCommand)
    }
}
