package network.models.ai

import com.google.gson.annotations.SerializedName

data class DeepSeekRequest(
    @SerializedName("messages")
    val messages: List<DeepSeekMessage>,
    @SerializedName("model")
    val model: String,
    @SerializedName("frequency_penalty")
    val frequencyPenalty: Int = 0,
    @SerializedName("max_tokens")
    val maxTokens: Int = 4096,
    @SerializedName("presence_penalty")
    val presencePenalty: Int = 0,
    @SerializedName("response_format")
    val responseFormat: ResponseFormat = ResponseFormat("text"),
    @SerializedName("stop")
    val stop: String? = null,
    @SerializedName("stream")
    val stream: Boolean = false,
    @SerializedName("stream_options")
    val streamOptions: String? = null,
    @SerializedName("temperature")
    val temperature: Int = 1,
    @SerializedName("top_p")
    val topP: Int = 1,
    @SerializedName("tools")
    val tools: String? = null,
    @SerializedName("tool_choice")
    val toolChoice: String = "none",
    @SerializedName("logprobs")
    val logprobs: Boolean = false,
    @SerializedName("top_logprobs")
    val topLogprobs: String? = null
) {

    data class DeepSeekMessage(
        @SerializedName("content")
        val content: String,

        @SerializedName("role")
        val role: String
    )

    data class ResponseFormat(
        @SerializedName("type")
        val type: String
    )
}