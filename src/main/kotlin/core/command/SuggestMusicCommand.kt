package core.command

import core.AudioScheduleResult
import core.models.Event
import core.models.MusicForPlayingParameters
import core.models.Reply
import core.music.BotAudioPlayer
import music.TrackOrder
import network.AiNetwork
import network.NetworkException

class SuggestMusicCommand(
    private val aiNetwork: AiNetwork,
    private val botAudioPlayer: BotAudioPlayer,
) : Command(true) {

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
        val resultText = when (result) {
            is AudioScheduleResult.Error -> "Ошибка при загрузке: ${result.exception}."
            AudioScheduleResult.NoMatches -> "Песня не найдена."
            is AudioScheduleResult.PlaylistAdded -> "$suggestionMessage\nПлейлист ${result.playlistName} загружен."
            is AudioScheduleResult.PlaylistPlaying -> "$suggestionMessage\nПлейлист ${result.playlistName} загружен. Играет - ${result.trackName}."
            is AudioScheduleResult.TrackAdded -> "$suggestionMessage\nТрек ${result.trackName} добавлен в очередь."
            is AudioScheduleResult.TrackPlaying -> "$suggestionMessage\nТрек ${result.trackName} начал играть."
            is AudioScheduleResult.EmptyPlaylist -> "$suggestionMessage\nПлейлист ${result.playlistName} пуст."
            is AudioScheduleResult.TrackNotFound -> "$suggestionMessage\nНо ничего не найдено для \"${result.keyword}\""
        }

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
