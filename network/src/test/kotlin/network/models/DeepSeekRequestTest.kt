package network.models

import network.models.ai.DeepSeekRequest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeepSeekRequestTest {

    @Test
    fun should_create_request_with_required_fields() {
        val message = DeepSeekRequest.DeepSeekMessage(
            content = "Hello",
            role = "user",
        )
        val request = DeepSeekRequest(
            messages = listOf(message),
            model = "deepseek-chat",
        )

        assertEquals(listOf(message), request.messages)
        assertEquals("deepseek-chat", request.model)
    }

    @Test
    fun should_have_correct_default_values() {
        val request = DeepSeekRequest(
            messages = emptyList(),
            model = "deepseek-chat",
        )

        assertEquals(0, request.frequencyPenalty)
        assertEquals(4096, request.maxTokens)
        assertEquals(0, request.presencePenalty)
        assertFalse(request.stream)
        assertEquals(1, request.temperature)
        assertEquals(1, request.topP)
        assertEquals("none", request.toolChoice)
        assertFalse(request.logprobs)
        assertNull(request.stop)
        assertNull(request.streamOptions)
        assertNull(request.tools)
        assertNull(request.topLogprobs)
    }

    @Test
    fun should_have_text_response_format_by_default() {
        val request = DeepSeekRequest(
            messages = emptyList(),
            model = "deepseek-chat",
        )

        assertEquals("text", request.responseFormat.type)
    }

    @Test
    fun should_create_message_with_correct_fields() {
        val message = DeepSeekRequest.DeepSeekMessage(
            content = "What is Kotlin?",
            role = "user",
        )

        assertEquals("What is Kotlin?", message.content)
        assertEquals("user", message.role)
    }

    @Test
    fun should_create_response_format_with_type() {
        val format = DeepSeekRequest.ResponseFormat(type = "json_object")
        assertEquals("json_object", format.type)
    }

    @Test
    fun should_support_multiple_messages() {
        val messages = listOf(
            DeepSeekRequest.DeepSeekMessage(content = "Hello", role = "user"),
            DeepSeekRequest.DeepSeekMessage(content = "Hi there", role = "assistant"),
            DeepSeekRequest.DeepSeekMessage(content = "How are you?", role = "user"),
        )
        val request = DeepSeekRequest(
            messages = messages,
            model = "deepseek-chat",
        )

        assertEquals(3, request.messages.size)
    }
}
