package network

import network.api.TtsApi
import network.factory.RetrofitFactory
import network.models.tts.TtsRequest

/**
 * Клиент для синтеза речи через локальный Silero TTS-сервер.
 *
 * @property baseUrl базовый URL TTS-сервера (по умолчанию `http://localhost:8000/`)
 */
class TtsNetwork(private val baseUrl: String = "http://localhost:8000/") {

    private val api = RetrofitFactory.createApi<TtsApi>(baseUrl)

    /**
     * Синтезирует речь из текста и возвращает аудио в виде байтов WAV-файла.
     *
     * @param text текст для синтеза
     * @param speaker идентификатор голоса (по умолчанию `"baya"`)
     * @return байты аудиофайла или `null` если сервер вернул пустое тело ответа
     */
    suspend fun synthesize(text: String, speaker: String = "baya"): ByteArray? {
        val response = api.synthesize(TtsRequest(text, speaker))
        return response.body()?.bytes()
    }
}
