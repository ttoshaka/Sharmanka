package music

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TrackOrderTest {

    @Test
    fun should_have_three_values() {
        assertEquals(3, TrackOrder.entries.size)
    }

    @Test
    fun should_contain_normal_value() {
        assertTrue(TrackOrder.entries.contains(TrackOrder.NORMAL))
    }

    @Test
    fun should_contain_top_value() {
        assertTrue(TrackOrder.entries.contains(TrackOrder.TOP))
    }

    @Test
    fun should_contain_top_separate_value() {
        assertTrue(TrackOrder.entries.contains(TrackOrder.TOP_SEPARATE))
    }

    @Test
    fun should_return_correct_value_by_name() {
        assertEquals(TrackOrder.NORMAL, TrackOrder.valueOf("NORMAL"))
        assertEquals(TrackOrder.TOP, TrackOrder.valueOf("TOP"))
        assertEquals(TrackOrder.TOP_SEPARATE, TrackOrder.valueOf("TOP_SEPARATE"))
    }
}
