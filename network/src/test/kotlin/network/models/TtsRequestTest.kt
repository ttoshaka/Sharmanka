package network.models

import network.models.tts.TtsRequest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TtsRequestTest {

    @Test
    fun should_create_request_with_text_and_default_speaker() {
        val request = TtsRequest(text = "Hello world")

        assertEquals("Hello world", request.text)
        assertEquals("baya", request.speaker)
    }

    @Test
    fun should_create_request_with_custom_speaker() {
        val request = TtsRequest(text = "Привет", speaker = "custom_speaker")

        assertEquals("Привет", request.text)
        assertEquals("custom_speaker", request.speaker)
    }

    @Test
    fun should_create_request_with_empty_text() {
        val request = TtsRequest(text = "")
        assertEquals("", request.text)
    }

    @Test
    fun should_have_baya_as_default_speaker() {
        val request = TtsRequest(text = "any text")
        assertEquals("baya", request.speaker)
    }
}
