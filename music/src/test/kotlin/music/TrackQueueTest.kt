package music

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TrackQueueTest {

    private fun makeItem(order: TrackOrder): AudioItem {
        val track = mockk<com.sedmelluq.discord.lavaplayer.track.AudioTrack>(relaxed = true)
        return AudioItem(track = track, order = order)
    }

    @Test
    fun should_be_empty_when_created() {
        val queue = TrackQueue()
        assertTrue(queue.isEmpty())
    }

    @Test
    fun should_add_item_to_end_when_order_is_normal() {
        val queue = TrackQueue()
        val first = makeItem(TrackOrder.NORMAL)
        val second = makeItem(TrackOrder.NORMAL)

        queue.addAudioItem(first)
        queue.addAudioItem(second)

        assertEquals(first, queue[0])
        assertEquals(second, queue[1])
    }

    @Test
    fun should_add_item_to_front_when_order_is_top() {
        val queue = TrackQueue()
        val first = makeItem(TrackOrder.NORMAL)
        val top = makeItem(TrackOrder.TOP)

        queue.addAudioItem(first)
        queue.addAudioItem(top)

        assertEquals(top, queue[0])
        assertEquals(first, queue[1])
    }

    @Test
    fun should_insert_at_front_when_queue_empty_and_order_is_top() {
        val queue = TrackQueue()
        val item = makeItem(TrackOrder.TOP)

        queue.addAudioItem(item)

        assertEquals(item, queue[0])
        assertEquals(1, queue.size)
    }

    @Test
    fun should_insert_after_last_top_separate_when_order_is_top_separate() {
        val queue = TrackQueue()
        val normal = makeItem(TrackOrder.NORMAL)
        val separate1 = makeItem(TrackOrder.TOP_SEPARATE)
        val separate2 = makeItem(TrackOrder.TOP_SEPARATE)

        queue.addAudioItem(normal)
        queue.addAudioItem(separate1)
        queue.addAudioItem(separate2)

        // separate1 at 0, separate2 at 1, normal at 2
        assertEquals(separate1, queue[0])
        assertEquals(separate2, queue[1])
        assertEquals(normal, queue[2])
    }

    @Test
    fun should_insert_top_separate_at_front_when_no_existing_top_separate() {
        val queue = TrackQueue()
        val normal = makeItem(TrackOrder.NORMAL)
        val separate = makeItem(TrackOrder.TOP_SEPARATE)

        queue.addAudioItem(normal)
        queue.addAudioItem(separate)

        assertEquals(separate, queue[0])
        assertEquals(normal, queue[1])
    }

    @Test
    fun should_return_tracks_in_order_from_get_queue() {
        val queue = TrackQueue()
        val item1 = makeItem(TrackOrder.NORMAL)
        val item2 = makeItem(TrackOrder.NORMAL)

        queue.addAudioItem(item1)
        queue.addAudioItem(item2)

        val tracks = queue.getQueue()

        assertEquals(2, tracks.size)
        assertEquals(item1.track, tracks[0])
        assertEquals(item2.track, tracks[1])
    }

    @Test
    fun should_return_empty_list_from_get_queue_when_empty() {
        val queue = TrackQueue()
        assertTrue(queue.getQueue().isEmpty())
    }

    @Test
    fun should_maintain_priority_block_when_adding_multiple_top_separate() {
        val queue = TrackQueue()
        val normal1 = makeItem(TrackOrder.NORMAL)
        val normal2 = makeItem(TrackOrder.NORMAL)
        val sep1 = makeItem(TrackOrder.TOP_SEPARATE)
        val sep2 = makeItem(TrackOrder.TOP_SEPARATE)
        val sep3 = makeItem(TrackOrder.TOP_SEPARATE)

        queue.addAudioItem(normal1)
        queue.addAudioItem(normal2)
        queue.addAudioItem(sep1)
        queue.addAudioItem(sep2)
        queue.addAudioItem(sep3)

        assertEquals(sep1, queue[0])
        assertEquals(sep2, queue[1])
        assertEquals(sep3, queue[2])
        assertEquals(normal1, queue[3])
        assertEquals(normal2, queue[4])
    }

    @Test
    fun should_place_top_before_top_separate_items() {
        val queue = TrackQueue()
        val normal = makeItem(TrackOrder.NORMAL)
        val separate = makeItem(TrackOrder.TOP_SEPARATE)
        val top = makeItem(TrackOrder.TOP)

        queue.addAudioItem(normal)
        queue.addAudioItem(separate)
        queue.addAudioItem(top)

        // TOP always goes to index 0 (addFirst)
        assertEquals(top, queue[0])
    }
}
