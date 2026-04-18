package core.command

import core.AudioScheduleResult
import core.models.Reply
import network.NetworkException

/**
 * Преобразует [AudioScheduleResult] в текст ответа.
 *
 * Используется командами, которые добавляют один трек и сразу возвращают
 * итоговый [Reply]. Если передан [suggestionPrefix], он подставляется перед
 * сообщением об успехе (и об отсутствии результата по keyword).
 *
 * @param suggestionPrefix префикс для успешных веток (например, "AI предлагает: …\n").
 *   По умолчанию — пустая строка.
 * @return текстовый ответ для пользователя
 */
fun AudioScheduleResult.toText(suggestionPrefix: String = ""): String = when (this) {
    is AudioScheduleResult.Error -> when (val ex = exception) {
        is NetworkException -> "Ошибка при поиске на YouTube: ${ex.message}"
        else -> "Ошибка при загрузке: $ex."
    }
    AudioScheduleResult.NoMatches -> "Песня не найдена."
    is AudioScheduleResult.PlaylistAdded -> "${suggestionPrefix}Плейлист $playlistName загружен."
    is AudioScheduleResult.PlaylistPlaying -> "${suggestionPrefix}Плейлист $playlistName загружен. Играет - $trackName."
    is AudioScheduleResult.TrackAdded -> "${suggestionPrefix}Трек $trackName добавлен в очередь."
    is AudioScheduleResult.TrackPlaying -> "${suggestionPrefix}Трек $trackName начал играть."
    is AudioScheduleResult.EmptyPlaylist -> "${suggestionPrefix}Плейлист $playlistName пуст."
    is AudioScheduleResult.TrackNotFound -> "${suggestionPrefix}Но ничего не найдено для \"$keyword\""
}

/**
 * Преобразует [AudioScheduleResult] в короткую строку статуса для сводного списка.
 *
 * Используется командами, которые загружают несколько треков подряд и собирают
 * итоговый отчёт (например, [SuggestPlaylistCommand]).
 *
 * @param songName исходное название песни, переданное на загрузку (используется
 *   в ветках "не найдено" и "ошибка")
 * @return строка статуса, начинающаяся с символа ✓, ▶ или ✗
 */
fun AudioScheduleResult.toStatusLine(songName: String): String = when (this) {
    is AudioScheduleResult.TrackAdded -> "✓ $trackName"
    is AudioScheduleResult.TrackPlaying -> "▶ $trackName"
    is AudioScheduleResult.PlaylistAdded -> "✓ Плейлист $playlistName"
    is AudioScheduleResult.PlaylistPlaying -> "▶ Плейлист $playlistName"
    AudioScheduleResult.NoMatches,
    is AudioScheduleResult.TrackNotFound -> "✗ $songName - не найдено"
    is AudioScheduleResult.Error -> {
        val errorMsg = if (exception is NetworkException) {
            "ошибка YouTube: ${exception.message}"
        } else {
            "ошибка"
        }
        "✗ $songName - $errorMsg"
    }
    is AudioScheduleResult.EmptyPlaylist -> "✗ $songName - пустой плейлист"
}
