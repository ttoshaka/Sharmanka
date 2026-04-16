package core.utils

import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    fun formatDuration(unit: TimeUnit, value: Long): String {
        val time = if (value > 0) value else -value
        val seconds = unit.toSeconds(time) % 60
        val minutes = unit.toMinutes(time) % 60
        val hours = unit.toHours(time)
        val formattedMinutesSeconds = String.format(Locale.ENGLISH, "%1$02d:%2$02d", minutes, seconds)
        return StringBuilder()
            .append(
                if (hours != 0L) {
                    String.format(Locale.ENGLISH, "%d:%s", hours, formattedMinutesSeconds)
                } else {
                    formattedMinutesSeconds
                }
            )
            .toString()
    }
}