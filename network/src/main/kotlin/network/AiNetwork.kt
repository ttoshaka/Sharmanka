package network

import network.api.AiApi
import network.factory.RetrofitFactory
import network.models.ai.DeepSeekRequest

class AiNetwork(
    private val deepseekKey: String
) {

    private val api = RetrofitFactory.createApi<AiApi>("https://api.deepseek.com/")

    suspend fun chat(message: String): String {
        return api.chat(
            body = DeepSeekRequest(
                messages = listOf(
                    DeepSeekRequest.DeepSeekMessage(
                        content = "",
                        role = "system"
                    ),
                    DeepSeekRequest.DeepSeekMessage(
                        content = message,
                        role = "user"
                    )
                ),
                model = "deepseek-chat",
            ),
            key = "Bearer $deepseekKey"
        ).body()?.choices?.first()?.message?.content ?: ""
    }
}