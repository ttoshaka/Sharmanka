package network

import network.api.AiApi
import network.factory.RetrofitFactory
import network.models.ai.DeepSeekRequest

/**
 * Сетевой клиент для взаимодействия с DeepSeek AI API.
 *
 * @property deepseekKey API-ключ для авторизации запросов
 */
class AiNetwork(
    private val deepseekKey: String,
) {

    private val api = RetrofitFactory.createApi<AiApi>("https://api.deepseek.com/")

    /**
     * Отправляет сообщение в DeepSeek AI и возвращает текстовый ответ модели.
     *
     * @param message текст сообщения от пользователя
     * @return текстовый ответ от AI-модели
     * @throws NetworkException если сервер вернул неуспешный HTTP-статус
     */
    suspend fun chat(message: String): String {
        val response = api.chat(
            body = DeepSeekRequest(
                messages = listOf(
                    DeepSeekRequest.DeepSeekMessage(
                        content = "",
                        role = "system",
                    ),
                    DeepSeekRequest.DeepSeekMessage(
                        content = message,
                        role = "user",
                    ),
                ),
                model = "deepseek-chat",
            ),
            key = "Bearer $deepseekKey",
        )

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw NetworkException("AI request failed: HTTP ${response.code()} — $errorBody")
        }

        return response.body()?.choices?.first()?.message?.content ?: ""
    }
}
