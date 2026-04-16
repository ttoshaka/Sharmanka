package network.api

import network.models.ai.DeepSeekRequest
import network.models.ai.DeepSeekResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AiApi {

    @POST("chat/completions")
    suspend fun chat(
        @Body body: DeepSeekRequest,
        @Header("Authorization") key: String,
    ): Response<DeepSeekResponse>
}