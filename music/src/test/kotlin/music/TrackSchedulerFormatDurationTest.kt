package music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class TrackSchedulerFormatDurationTest {

    private lateinit var scheduler: TrackScheduler

    @BeforeEach
    fun setup() {
        val player = mockk<AudioPlayer>(relaxed = true)
        scheduler = TrackScheduler(player)
    }

    @Test
    fun should_format_zero_duration_as_zeroes() {
        val result = scheduler.formatDuration(unit = TimeUnit.MILLISECONDS, value = 0L)
        assertEquals("00:00", result)
    }

    @Test
    fun should_format_seconds_only() {
        val result = scheduler.formatDuration(unit = TimeUnit.MILLISECONDS, value = 45_000L)
        assertEquals("00:45", result)
    }

    @Test
    fun should_format_minutes_and_seconds() {
        val result = scheduler.formatDuration(unit = TimeUnit.MILLISECONDS, value = 3 * 60_000L + 27_000L)
        assertEquals("03:27", result)
    }

    @Test
    fun should_format_one_hour_exactly() {
        val result = scheduler.formatDuration(unit = TimeUnit.MILLISECONDS, value = 3_600_000L)
        assertEquals("1:00:00", result)
    }

    @Test
    fun should_format_hours_minutes_and_seconds() {
        val value = 2 * 3_600_000L + 15 * 60_000L + 30_000L
        val result = scheduler.formatDuration(unit = TimeUnit.MILLISECONDS, value = value)
        assertEquals("2:15:30", result)
    }

    @Test
    fun should_treat_negative_value_as_positive() {
        val positive = scheduler.formatDuration(unit = TimeUnit.MILLISECONDS, value = 45_000L)
        val negative = scheduler.formatDuration(unit = TimeUnit.MILLISECONDS, value = -45_000L)
        assertEquals(positive, negative)
    }

    @Test
    fun should_format_from_seconds_unit() {
        val result = scheduler.formatDuration(unit = TimeUnit.SECONDS, value = 90L)
        assertEquals("01:30", result)
    }

    @Test
    fun should_format_exactly_59_minutes_59_seconds() {
        val value = 59 * 60_000L + 59_000L
        val result = scheduler.formatDuration(unit = TimeUnit.MILLISECONDS, value = value)
        assertEquals("59:59", result)
    }
}
