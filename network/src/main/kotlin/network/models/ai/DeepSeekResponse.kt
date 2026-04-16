package network.models.ai

import com.google.gson.annotations.SerializedName

data class DeepSeekResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("object")
    val `object`: String,
    @SerializedName("created")
    val created: Long,
    @SerializedName("model")
    val model: String,
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("usage")
    val usage: Usage? = null,
    @SerializedName("system_fingerprint")
    val systemFingerprint: String? = null
) {
    data class Choice(
        @SerializedName("index")
        val index: Int,
        @SerializedName("message")
        val message: Message,
        @SerializedName("logprobs")
        val logprobs: String? = null,
        @SerializedName("finish_reason")
        val finishReason: String? = null
    )

    data class Message(
        @SerializedName("role")
        val role: String,
        @SerializedName("content")
        val content: String
    )

    data class Usage(
        @SerializedName("prompt_tokens")
        val promptTokens: Int,
        @SerializedName("completion_tokens")
        val completionTokens: Int,
        @SerializedName("total_tokens")
        val totalTokens: Int,
        @SerializedName("prompt_tokens_details")
        val promptTokensDetails: PromptTokensDetails? = null,
        @SerializedName("prompt_cache_hit_tokens")
        val promptCacheHitTokens: Int? = null,
        @SerializedName("prompt_cache_miss_tokens")
        val promptCacheMissTokens: Int? = null
    ) {
        data class PromptTokensDetails(
            @SerializedName("cached_tokens")
            val cachedTokens: Int
        )
    }
}