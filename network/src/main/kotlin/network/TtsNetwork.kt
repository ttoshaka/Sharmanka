package network

import network.api.TtsApi
import network.factory.RetrofitFactory
import network.models.tts.TtsRequest

class TtsNetwork(baseUrl: String = "http://localhost:8000/") {

    private val api = RetrofitFactory.createApi<TtsApi>(baseUrl)

    suspend fun synthesize(text: String, speaker: String = "baya"): ByteArray? {
        val response = api.synthesize(TtsRequest(text, speaker))
        return response.body()?.bytes()
    }
}
