package music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TrackSchedulerTest {

    private lateinit var player: AudioPlayer
    private lateinit var scheduler: TrackScheduler

    @BeforeEach
    fun setup() {
        player = mockk(relaxed = true)
        scheduler = TrackScheduler(player)
    }

    private fun makeTrack(durationMs: Long = 60_000L): AudioTrack {
        val track = mockk<AudioTrack>(relaxed = true)
        every { track.duration } returns durationMs
        every { track.makeClone() } returns track
        return track
    }

    private fun makeItem(
        track: AudioTrack = makeTrack(),
        order: TrackOrder = TrackOrder.NORMAL,
    ) = AudioItem(track = track, order = order)

    @Test
    fun should_have_null_current_track_when_created() {
        assertNull(scheduler.currentTrack)
    }

    @Test
    fun should_start_track_immediately_when_player_is_idle() {
        val track = makeTrack()
        every { player.startTrack(track, true) } returns true

        val started = scheduler.addAudioItem(makeItem(track = track))

        assertTrue(started)
        assertEquals(track, scheduler.currentTrack)
    }

    @Test
    fun should_add_to_queue_when_player_is_busy() {
        val track = makeTrack()
        every { player.startTrack(track, true) } returns false

        val started = scheduler.addAudioItem(makeItem(track = track))

        assertFalse(started)
        assertNull(scheduler.currentTrack)
        assertEquals(1, scheduler.getQueue().size)
    }

    @Test
    fun should_start_next_track_on_track_end_when_may_start_next() {
        val nextTrack = makeTrack()
        every { player.startTrack(any(), any()) } returns false
        scheduler.addAudioItem(makeItem(track = nextTrack))

        scheduler.onTrackEnd(player, mockk(relaxed = true), AudioTrackEndReason.FINISHED)

        verify { player.startTrack(nextTrack, true) }
    }

    @Test
    fun should_not_start_next_track_on_track_end_when_may_not_start_next() {
        val nextTrack = makeTrack()
        every { player.startTrack(any(), any()) } returns false
        scheduler.addAudioItem(makeItem(track = nextTrack))

        scheduler.onTrackEnd(player, mockk(relaxed = true), AudioTrackEndReason.REPLACED)

        // REPLACED has mayStartNext = false — should not start next
        verify(exactly = 1) { player.startTrack(nextTrack, true) }
    }

    @Test
    fun should_return_queue_tracks_in_order() {
        val track1 = makeTrack()
        val track2 = makeTrack()
        every { player.startTrack(any(), true) } returns false

        scheduler.addAudioItem(makeItem(track = track1))
        scheduler.addAudioItem(makeItem(track = track2))

        val queue = scheduler.getQueue()
        assertEquals(2, queue.size)
        assertEquals(track1, queue[0])
        assertEquals(track2, queue[1])
    }

    @Test
    fun should_skip_track_by_starting_next() {
        val nextTrack = makeTrack()
        every { player.startTrack(any(), true) } returns false
        scheduler.addAudioItem(makeItem(track = nextTrack))

        scheduler.skipTrack()

        verify { player.startTrack(nextTrack, false) }
    }

    @Test
    fun should_clear_queue() {
        every { player.startTrack(any(), true) } returns false
        scheduler.addAudioItem(makeItem())
        scheduler.addAudioItem(makeItem())

        scheduler.clear()

        assertTrue(scheduler.getQueue().isEmpty())
    }

    @Test
    fun should_return_paused_state_from_player() {
        every { player.isPaused } returns true
        assertTrue(scheduler.isPaused)

        every { player.isPaused } returns false
        assertFalse(scheduler.isPaused)
    }

    @Test
    fun should_set_paused_state_on_player() {
        scheduler.isPaused = true
        verify { player.isPaused = true }

        scheduler.isPaused = false
        verify { player.isPaused = false }
    }

    @Test
    fun should_add_multiple_items_and_return_true_when_any_started() {
        val track1 = makeTrack()
        val track2 = makeTrack()
        every { player.startTrack(track1, true) } returns true
        every { player.startTrack(track2, true) } returns false

        val result = scheduler.addAudioItems(listOf(makeItem(track1), makeItem(track2)))

        assertTrue(result)
    }

    @Test
    fun should_return_false_when_no_items_started_in_add_audio_items() {
        every { player.startTrack(any(), true) } returns false

        val result = scheduler.addAudioItems(listOf(makeItem(), makeItem()))

        assertFalse(result)
    }

    @Test
    fun should_return_false_from_add_audio_items_when_list_is_empty() {
        val result = scheduler.addAudioItems(emptyList())
        assertFalse(result)
    }
}
