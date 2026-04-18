package music

import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Утилита для форматирования временных интервалов в читаемый вид
 */
object DateUtils {

    /**
     * Форматирует длительность в строку вида `HH:MM:SS` или `MM:SS`
     *
     * @param unit единица измерения входного значения
     * @param value длительность в указанных единицах (отрицательные значения обрабатываются по модулю)
     * @return отформатированная строка длительности
     */
    fun formatDuration(unit: TimeUnit, value: Long): String {
        val time = if (value > 0) value else -value
        val seconds = unit.toSeconds(time) % 60
        val minutes = unit.toMinutes(time) % 60
        val hours = unit.toHours(time)
        val formattedMinutesSeconds = String.format(Locale.ENGLISH, "%1$02d:%2$02d", minutes, seconds)
        return if (hours != 0L) String.format(Locale.ENGLISH, "%d:%s", hours, formattedMinutesSeconds)
        else formattedMinutesSeconds
    }
}
