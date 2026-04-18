package core.command

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CommandsEnumTest {

    @Test
    fun should_have_unique_command_names() {
        val names = Commands.entries.map { it.info.name }
        assertEquals(names.size, names.distinct().size)
    }

    @Test
    fun should_have_non_blank_names_for_all_commands() {
        Commands.entries.forEach { command ->
            assertTrue(
                command.info.name.isNotBlank(),
                "Command $command has blank name",
            )
        }
    }

    @Test
    fun should_have_non_blank_descriptions_for_all_commands() {
        Commands.entries.forEach { command ->
            assertTrue(
                command.info.description.isNotBlank(),
                "Command $command has blank description",
            )
        }
    }

    @Test
    fun should_have_correct_name_for_play_command() {
        assertEquals("play", Commands.PLAY.info.name)
    }

    @Test
    fun should_have_correct_name_for_skip_command() {
        assertEquals("skip", Commands.SKIP.info.name)
    }

    @Test
    fun should_have_source_option_for_play_command() {
        val options = Commands.PLAY.info.options
        assertNotNull(options)
        assertTrue(options.any { it.name == "source" })
    }

    @Test
    fun should_have_source_option_for_play_top_command() {
        val options = Commands.PLAY_TOP.info.options
        assertNotNull(options)
        assertTrue(options.any { it.name == "source" })
    }

    @Test
    fun should_have_source_option_for_play_top_plus_command() {
        val options = Commands.PLAY_TOP_PLUS.info.options
        assertNotNull(options)
        assertTrue(options.any { it.name == "source" })
    }

    @Test
    fun should_have_no_options_for_skip_command() {
        assertNull(Commands.SKIP.info.options)
    }

    @Test
    fun should_have_no_options_for_queue_command() {
        assertNull(Commands.QUEUE.info.options)
    }

    @Test
    fun should_have_no_options_for_clear_command() {
        assertNull(Commands.CLEAR.info.options)
    }

    @Test
    fun should_have_message_option_for_chat_command() {
        val options = Commands.CHAT.info.options
        assertNotNull(options)
        assertTrue(options.any { it.name == "message" })
    }

    @Test
    fun should_have_request_option_for_suggest_music_command() {
        val options = Commands.SUGGEST_MUSIC.info.options
        assertNotNull(options)
        assertTrue(options.any { it.name == "request" })
    }

    @Test
    fun should_have_request_and_count_options_for_suggest_playlist_command() {
        val options = Commands.SUGGEST_PLAYLIST.info.options
        assertNotNull(options)
        assertTrue(options.any { it.name == "request" })
        assertTrue(options.any { it.name == "count" })
    }

    @Test
    fun should_have_loop_option_for_background_command() {
        val options = Commands.BACKGROUND.info.options
        assertNotNull(options)
        assertTrue(options.any { it.name == "loop" })
    }
}
