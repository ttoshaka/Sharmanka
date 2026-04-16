package network.api

import network.models.youtube.FindByKeywordResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApi {

    @GET("search")
    suspend fun findByKeyword(
        @Query("q") keyword: String,
        @Query("maxResults") maxResults: String,
        @Query("key") key: String,
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video"
    ): Response<FindByKeywordResponse>
}