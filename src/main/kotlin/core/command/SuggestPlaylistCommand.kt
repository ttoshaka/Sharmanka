package core.command

import core.AudioScheduleResult
import core.models.Event
import core.models.MusicForPlayingParameters
import core.models.Reply
import core.music.BotAudioPlayer
import music.TrackOrder
import network.AiNetwork
import network.NetworkException

/**
 * Команда подбора и воспроизведения плейлиста по запросу пользователя через AI.
 *
 * @property aiNetwork клиент для обращений к DeepSeek AI
 * @property botAudioPlayer плеер для загрузки и воспроизведения треков в Discord
 */
class SuggestPlaylistCommand(
    private val aiNetwork: AiNetwork,
    private val botAudioPlayer: BotAudioPlayer,
) : Command(true) {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        val userRequest = event.options["request"] ?: return Reply.Text("Пустой запрос.")

        // Получаем количество песен из опции (по умолчанию 5, максимум 20).
        // Опция зарегистрирована как OptionType.INTEGER — Discord гарантирует числовое значение.
        val count = event.options["count"]?.toInt()?.coerceIn(1, 20) ?: 5

        // Отправляем запрос в Deepseek с промптом для подбора песен
        val prompt = buildPrompt(userRequest, count)
        val aiResponse = try {
            aiNetwork.chat(prompt)
        } catch (e: NetworkException) {
            return Reply.Text("Ошибка при обращении к AI: ${e.message}")
        }

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

            val isSuccess = result is AudioScheduleResult.TrackAdded
                || result is AudioScheduleResult.TrackPlaying
                || result is AudioScheduleResult.PlaylistAdded
                || result is AudioScheduleResult.PlaylistPlaying
            results.add(result.toStatusLine(songName = song))
            if (isSuccess) successCount++ else failCount++
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
        val numberedLine = Regex("""^\d+[.\-)\s]+(.+)$""")
        return response.trim()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                numberedLine.find(line)?.groupValues?.get(1)?.trim()?.takeIf { it.isNotBlank() }
            }
            .take(count)
    }
}
