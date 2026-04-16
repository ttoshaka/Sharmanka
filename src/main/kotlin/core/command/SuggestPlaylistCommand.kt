package core.command

import core.AudioScheduleResult
import core.models.Event
import core.models.MusicForPlayingParameters
import core.models.Reply
import core.music.BotAudioPlayer
import music.TrackOrder
import network.AiNetwork

class SuggestPlaylistCommand(
    private val aiNetwork: AiNetwork,
    private val botAudioPlayer: BotAudioPlayer,
) : Command(true) {

    override suspend fun invoke(event: Event): Reply {
        val userRequest = event.options["request"] ?: return Reply.Text("Пустой запрос.")

        // Получаем количество песен из опции (по умолчанию 5, максимум 20)
        val countStr = event.options["count"] ?: "5"
        val count = countStr.toIntOrNull()?.coerceIn(1, 20) ?: 5

        // Отправляем запрос в Deepseek с промптом для подбора песен
        val prompt = buildPrompt(userRequest, count)
        val aiResponse = aiNetwork.chat(prompt)

        // Извлекаем список песен из ответа
        val songs = extractSongsFromResponse(aiResponse, count)

        if (songs.isEmpty()) {
            return Reply.Text("AI не смог подобрать песни. Попробуйте переформулировать запрос.")
        }

        // Создаём сообщение для пользователя
        val suggestionMessage = buildString {
            appendLine("🎵 AI подобрал плейлист из ${songs.size} песен:")
            songs.forEachIndexed { index, song ->
                appendLine("${index + 1}. $song")
            }
            appendLine()
            appendLine("Добавляю в очередь...")
        }

        // Загружаем все песни в очередь
        val results = mutableListOf<String>()
        var successCount = 0
        var failCount = 0

        songs.forEach { song ->
            val result = botAudioPlayer.loadMusic(
                text = song,
                parameters = MusicForPlayingParameters(
                    order = TrackOrder.NORMAL,
                    guild = event.guild,
                )
            )

            when (result) {
                is AudioScheduleResult.TrackAdded -> {
                    results.add("✓ ${result.trackName}")
                    successCount++
                }
                is AudioScheduleResult.TrackPlaying -> {
                    results.add("▶ ${result.trackName}")
                    successCount++
                }
                is AudioScheduleResult.PlaylistAdded -> {
                    results.add("✓ Плейлист ${result.playlistName}")
                    successCount++
                }
                is AudioScheduleResult.PlaylistPlaying -> {
                    results.add("▶ Плейлист ${result.playlistName}")
                    successCount++
                }
                is AudioScheduleResult.NoMatches,
                is AudioScheduleResult.TrackNotFound -> {
                    results.add("✗ $song - не найдено")
                    failCount++
                }
                is AudioScheduleResult.Error -> {
                    results.add("✗ $song - ошибка")
                    failCount++
                }
                is AudioScheduleResult.EmptyPlaylist -> {
                    results.add("✗ $song - пустой плейлист")
                    failCount++
                }
            }
        }

        // Формируем итоговый ответ
        val finalMessage = buildString {
            append(suggestionMessage)
            appendLine()
            results.forEach { appendLine(it) }
            appendLine()
            append("Успешно добавлено: $successCount")
            if (failCount > 0) {
                append(" | Не найдено: $failCount")
            }
        }

        return Reply.Text(finalMessage)
    }

    private fun buildPrompt(userRequest: String, count: Int): String {
        return """
            Пользователь просит подобрать плейлист со следующим запросом: "$userRequest"

            Твоя задача - предложить РОВНО $count песен, которые лучше всего подходят под этот запрос.

            ВАЖНО:
            - Верни список из РОВНО $count песен
            - Каждая песня на отдельной строке в формате "Исполнитель - Название песни"
            - НЕ нумеруй строки
            - НЕ добавляй пояснений, комментариев или дополнительного текста
            - НЕ используй кавычки, скобки или другие символы
            - Только название песен, по одной на строку

            Пример правильного ответа:
            Imagine Dragons - Believer
            Linkin Park - Numb
            Arctic Monkeys - Do I Wanna Know
            The Killers - Mr. Brightside
            Muse - Starlight
        """.trimIndent()
    }

    private fun extractSongsFromResponse(response: String, count: Int): List<String> {
        // Разбиваем ответ на строки и фильтруем пустые
        return response.trim()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .filter { it.contains("-") } // Оставляем только строки с форматом "Исполнитель - Название"
            .take(count) // Берём указанное количество песен
    }
}
