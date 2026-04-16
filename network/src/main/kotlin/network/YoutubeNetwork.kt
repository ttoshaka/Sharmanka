package network

import network.api.YouTubeApi
import network.factory.RetrofitFactory

class YoutubeNetwork(
    private val youtubeKey: String
) {

    private val baseVideoUrl = "https://www.youtube.com/watch?v="
    private val api = RetrofitFactory.createApi<YouTubeApi>("https://www.googleapis.com/youtube/v3/")

    suspend fun findVideo(keyword: String): String? {
        val videoId = api.findByKeyword(
            keyword = keyword,
            maxResults = "1",
            key = youtubeKey
        ).body()?.items?.firstOrNull()?.id?.videoId
        return videoId?.let { baseVideoUrl + it }
    }
}