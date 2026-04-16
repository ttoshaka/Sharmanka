package network.models.tts

data class TtsRequest(
    val text: String,
    val speaker: String = "baya"
)
