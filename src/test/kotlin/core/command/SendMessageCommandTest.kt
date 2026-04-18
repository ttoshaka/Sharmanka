package core.command

import core.models.Event
import core.models.Member
import core.models.Reply
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.dv8tion.jda.api.entities.Guild
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SendMessageCommandTest {

    private val event = Event(
        name = "test",
        guild = mockk<Guild>(),
        member = mockk<Member>(),
        options = emptyMap(),
    )

    @Test
    fun should_return_text_reply_with_given_message() = runTest {
        val command = SendMessageCommand("Hello!")
        val reply = command.invoke(event)
        assertIs<Reply.Text>(reply)
        assertEquals("Hello!", reply.value)
    }

    @Test
    fun should_return_text_reply_with_empty_message() = runTest {
        val command = SendMessageCommand("")
        val reply = command.invoke(event)
        assertIs<Reply.Text>(reply)
        assertEquals("", reply.value)
    }

    @Test
    fun should_return_null_command_message_when_name_is_null() = runTest {
        val command = SendMessageCommand("Command name is null")
        val reply = command.invoke(event)
        assertIs<Reply.Text>(reply)
        assertEquals("Command name is null", reply.value)
    }

    @Test
    fun should_have_false_is_long_command() {
        val command = SendMessageCommand("test")
        assertEquals(false, command.isLongCommand)
    }
}
