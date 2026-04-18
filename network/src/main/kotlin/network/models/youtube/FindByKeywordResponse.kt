package network.models.youtube

data class FindByKeywordResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String?,
    val regionCode: String,
    val pageInfo: PageInfo,
    val items: List<Video>,
    val snippet: Snippet
) {

    data class PageInfo(
        val totalResults: Int,
        val resultPerPage: Int
    )

    data class Video(
        val kind: String,
        val etag: String,
        val id: VideoId,
        val snippet: Snippet,
    ) {

        data class VideoId(
            val kind: String,
            val videoId: String
        )
    }

    data class Snippet(
        val publishedAt: String,
        val channelId: String,
        val title: String,
        val description: String,
        val channelTitle: String,
        val liveBroadcastContent: String
    )
}