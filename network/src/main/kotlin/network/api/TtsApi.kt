package network.api

import network.models.tts.TtsRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TtsApi {

    @POST("tts")
    suspend fun synthesize(@Body request: TtsRequest): Response<ResponseBody>
}
