package core.utils

import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class DateUtilsTest {

    @Test
    fun should_format_zero_as_double_zeroes() {
        val result = DateUtils.formatDuration(unit = TimeUnit.MILLISECONDS, value = 0L)
        assertEquals("00:00", result)
    }

    @Test
    fun should_format_seconds_only() {
        val result = DateUtils.formatDuration(unit = TimeUnit.MILLISECONDS, value = 45_000L)
        assertEquals("00:45", result)
    }

    @Test
    fun should_format_minutes_and_seconds() {
        val value = 3 * 60_000L + 27_000L
        val result = DateUtils.formatDuration(unit = TimeUnit.MILLISECONDS, value = value)
        assertEquals("03:27", result)
    }

    @Test
    fun should_format_one_hour() {
        val result = DateUtils.formatDuration(unit = TimeUnit.MILLISECONDS, value = 3_600_000L)
        assertEquals("1:00:00", result)
    }

    @Test
    fun should_format_hours_minutes_seconds() {
        val value = 2 * 3_600_000L + 15 * 60_000L + 30_000L
        val result = DateUtils.formatDuration(unit = TimeUnit.MILLISECONDS, value = value)
        assertEquals("2:15:30", result)
    }

    @Test
    fun should_treat_negative_as_positive() {
        val positive = DateUtils.formatDuration(unit = TimeUnit.MILLISECONDS, value = 90_000L)
        val negative = DateUtils.formatDuration(unit = TimeUnit.MILLISECONDS, value = -90_000L)
        assertEquals(positive, negative)
    }

    @Test
    fun should_format_from_seconds_unit() {
        val result = DateUtils.formatDuration(unit = TimeUnit.SECONDS, value = 125L)
        assertEquals("02:05", result)
    }

    @Test
    fun should_pad_minutes_and_seconds_with_leading_zero() {
        val result = DateUtils.formatDuration(unit = TimeUnit.MILLISECONDS, value = 5_000L)
        assertEquals("00:05", result)
    }

    @Test
    fun should_format_exactly_one_minute() {
        val result = DateUtils.formatDuration(unit = TimeUnit.MILLISECONDS, value = 60_000L)
        assertEquals("01:00", result)
    }
}
