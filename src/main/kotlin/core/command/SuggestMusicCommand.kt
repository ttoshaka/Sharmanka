package core.command

import core.models.Event
import core.models.MusicForPlayingParameters
import core.models.Reply
import core.music.BotAudioPlayer
import music.TrackOrder
import network.AiNetwork
import network.NetworkException

/**
 * Команда подбора и воспроизведения одной песни по запросу пользователя через AI.
 *
 * @property aiNetwork клиент для обращений к DeepSeek AI
 * @property botAudioPlayer плеер для загрузки и воспроизведения трека в Discord
 */
class SuggestMusicCommand(
    private val aiNetwork: AiNetwork,
    private val botAudioPlayer: BotAudioPlayer,
) : Command(true) {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply {
        val userRequest = event.options["request"] ?: return Reply.Text("Пустой запрос.")

        // Отправляем запрос в Deepseek с промптом для подбора песни
        val prompt = buildPrompt(userRequest)
        val aiResponse = try {
            aiNetwork.chat(prompt)
        } catch (e: NetworkException) {
            return Reply.Text("Ошибка при обращении к AI: ${e.message}")
        }

        // Извлекаем название песни из ответа (AI должен ответить только названием)
        val songQuery = extractSongFromResponse(aiResponse)

        // Создаём сообщение для пользователя
        val suggestionMessage = "AI предлагает: $songQuery"

        // Загружаем песню в очередь
        val result = botAudioPlayer.loadMusic(
            text = songQuery,
            parameters = MusicForPlayingParameters(
                order = TrackOrder.NORMAL,
                guild = event.guild,
            )
        )

        // Формируем итоговый ответ
        val resultText = result.toText(suggestionPrefix = "$suggestionMessage\n")

        return Reply.Text(resultText)
    }

    private fun buildPrompt(userRequest: String): String {
        return """
            Пользователь просит подобрать песню со следующим запросом: "$userRequest"

            Твоя задача - предложить ОДНУ песню, которая лучше всего подходит под этот запрос.

            ВАЖНО: Ответь ТОЛЬКО названием песни и исполнителем в формате "Исполнитель - Название песни".
            НЕ добавляй никаких пояснений, комментариев или дополнительного текста.
            НЕ используй кавычки, скобки или другие символы.

            Пример правильного ответа: Imagine Dragons - Believer
        """.trimIndent()
    }

    private fun extractSongFromResponse(response: String): String {
        // Убираем лишние символы и берём первую строку
        return response.trim()
            .lines()
            .firstOrNull { it.isNotBlank() }
            ?.trim()
            ?: response.trim()
    }
}
