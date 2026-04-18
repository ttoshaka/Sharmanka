package network

import network.api.YouTubeApi
import network.factory.RetrofitFactory

/**
 * Клиент для поиска видео через YouTube Data API v3.
 *
 * @property youtubeKey API-ключ для доступа к YouTube Data API
 */
class YoutubeNetwork(
    private val youtubeKey: String,
) {

    private val baseVideoUrl = "https://www.youtube.com/watch?v="
    private val api = RetrofitFactory.createApi<YouTubeApi>("https://www.googleapis.com/youtube/v3/")

    /**
     * Выполняет поиск видео на YouTube по ключевому слову и возвращает URL первого результата.
     *
     * @param keyword поисковый запрос
     * @return полный URL видео или `null`, если ни одного результата не найдено
     * @throws NetworkException если сервер вернул неуспешный HTTP-статус
     */
    suspend fun findVideo(keyword: String): String? {
        val response = api.findByKeyword(
            keyword = keyword,
            maxResults = "1",
            key = youtubeKey,
        )
        if (!response.isSuccessful) {
            throw NetworkException(
                "YouTube request failed: HTTP ${response.code()} — ${response.errorBody()?.string()}"
            )
        }
        val videoId = response.body()?.items?.firstOrNull()?.id?.videoId
        return videoId?.let { baseVideoUrl + it }
    }
}