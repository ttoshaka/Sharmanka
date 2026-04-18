package core.command

import core.AudioScheduleResult
import core.models.Event
import core.models.Member
import core.models.MusicForPlayingParameters
import core.models.Reply
import core.music.BotAudioPlayer
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import music.TrackOrder
import net.dv8tion.jda.api.entities.Guild
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PlayMusicCommandTest {

    private val botAudioPlayer = mockk<BotAudioPlayer>()
    private val guild = mockk<Guild>()

    private fun makeEvent(source: String? = "some song") = Event(
        name = "play",
        guild = guild,
        member = mockk<Member>(),
        options = if (source != null) mapOf("source" to source) else emptyMap(),
    )

    @Test
    fun should_return_empty_source_reply_when_no_source_option() = runTest {
        val command = PlayMusicCommand(botAudioPlayer = botAudioPlayer, order = TrackOrder.NORMAL)

        val reply = command.invoke(makeEvent(source = null))

        assertIs<Reply.Text>(reply)
        assertEquals("Empty source.", reply.value)
    }

    @Test
    fun should_return_track_playing_reply_when_track_starts() = runTest {
        coEvery {
            botAudioPlayer.loadMusic(text = any(), parameters = any())
        } returns AudioScheduleResult.TrackPlaying(trackName = "Cool Song")
        val command = PlayMusicCommand(botAudioPlayer = botAudioPlayer, order = TrackOrder.NORMAL)

        val reply = command.invoke(makeEvent())

        assertIs<Reply.Text>(reply)
        assertEquals("Track Cool Song playing.", reply.value)
    }

    @Test
    fun should_return_track_added_reply_when_track_queued() = runTest {
        coEvery {
            botAudioPlayer.loadMusic(text = any(), parameters = any())
        } returns AudioScheduleResult.TrackAdded(trackName = "Another Song")
        val command = PlayMusicCommand(botAudioPlayer = botAudioPlayer, order = TrackOrder.NORMAL)

        val reply = command.invoke(makeEvent())

        assertIs<Reply.Text>(reply)
        assertEquals("Track Another Song added.", reply.value)
    }

    @Test
    fun should_return_no_matches_reply() = runTest {
        coEvery {
            botAudioPlayer.loadMusic(text = any(), parameters = any())
        } returns AudioScheduleResult.NoMatches
        val command = PlayMusicCommand(botAudioPlayer = botAudioPlayer, order = TrackOrder.NORMAL)

        val reply = command.invoke(makeEvent())

        assertIs<Reply.Text>(reply)
        assertEquals("No matches.", reply.value)
    }

    @Test
    fun should_return_error_reply_when_exception_thrown() = runTest {
        val exception = Exception("network error")
        coEvery {
            botAudioPlayer.loadMusic(text = any(), parameters = any())
        } returns AudioScheduleResult.Error(exception = exception)
        val command = PlayMusicCommand(botAudioPlayer = botAudioPlayer, order = TrackOrder.NORMAL)

        val reply = command.invoke(makeEvent())

        assertIs<Reply.Text>(reply)
        assertTrue(reply.value.startsWith("Error"))
    }

    @Test
    fun should_return_playlist_playing_reply() = runTest {
        coEvery {
            botAudioPlayer.loadMusic(text = any(), parameters = any())
        } returns AudioScheduleResult.PlaylistPlaying(
            trackName = "First Track",
            playlistName = "My Playlist",
        )
        val command = PlayMusicCommand(botAudioPlayer = botAudioPlayer, order = TrackOrder.NORMAL)

        val reply = command.invoke(makeEvent())

        assertIs<Reply.Text>(reply)
        assertEquals("Playlist My Playlist loaded. Now playing - First Track.", reply.value)
    }

    @Test
    fun should_return_playlist_added_reply() = runTest {
        coEvery {
            botAudioPlayer.loadMusic(text = any(), parameters = any())
        } returns AudioScheduleResult.PlaylistAdded(playlistName = "My Playlist")
        val command = PlayMusicCommand(botAudioPlayer = botAudioPlayer, order = TrackOrder.NORMAL)

        val reply = command.invoke(makeEvent())

        assertIs<Reply.Text>(reply)
        assertEquals("Playlist My Playlist loaded.", reply.value)
    }

    @Test
    fun should_return_empty_playlist_reply() = runTest {
        coEvery {
            botAudioPlayer.loadMusic(text = any(), parameters = any())
        } returns AudioScheduleResult.EmptyPlaylist(playlistName = "Empty List")
        val command = PlayMusicCommand(botAudioPlayer = botAudioPlayer, order = TrackOrder.NORMAL)

        val reply = command.invoke(makeEvent())

        assertIs<Reply.Text>(reply)
        assertEquals("Playlist Empty List is empty.", reply.value)
    }

    @Test
    fun should_return_not_found_reply_when_track_not_found() = runTest {
        coEvery {
            botAudioPlayer.loadMusic(text = any(), parameters = any())
        } returns AudioScheduleResult.TrackNotFound(keyword = "unknown song")
        val command = PlayMusicCommand(botAudioPlayer = botAudioPlayer, order = TrackOrder.NORMAL)

        val reply = command.invoke(makeEvent())

        assertIs<Reply.Text>(reply)
        assertEquals("Nothing was found for \"unknown song\"", reply.value)
    }

    @Test
    fun should_have_true_is_long_command() {
        val command = PlayMusicCommand(botAudioPlayer = botAudioPlayer, order = TrackOrder.NORMAL)
        assertEquals(true, command.isLongCommand)
    }
}
