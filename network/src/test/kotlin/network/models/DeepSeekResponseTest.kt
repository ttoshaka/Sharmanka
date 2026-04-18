package network.models

import network.models.ai.DeepSeekResponse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeepSeekResponseTest {

    private fun makeResponse(choices: List<DeepSeekResponse.Choice> = emptyList()) =
        DeepSeekResponse(
            id = "resp-123",
            `object` = "chat.completion",
            created = 1_700_000_000L,
            model = "deepseek-chat",
            choices = choices,
        )

    @Test
    fun should_create_response_with_required_fields() {
        val response = makeResponse()

        assertEquals("resp-123", response.id)
        assertEquals("chat.completion", response.`object`)
        assertEquals(1_700_000_000L, response.created)
        assertEquals("deepseek-chat", response.model)
    }

    @Test
    fun should_have_null_optional_fields_by_default() {
        val response = makeResponse()

        assertNull(response.usage)
        assertNull(response.systemFingerprint)
    }

    @Test
    fun should_extract_content_from_first_choice() {
        val choice = DeepSeekResponse.Choice(
            index = 0,
            message = DeepSeekResponse.Message(
                role = "assistant",
                content = "Hello, how can I help?",
            ),
        )
        val response = makeResponse(choices = listOf(choice))

        assertEquals("Hello, how can I help?", response.choices[0].message.content)
    }

    @Test
    fun should_create_usage_with_token_counts() {
        val usage = DeepSeekResponse.Usage(
            promptTokens = 100,
            completionTokens = 50,
            totalTokens = 150,
        )

        assertEquals(100, usage.promptTokens)
        assertEquals(50, usage.completionTokens)
        assertEquals(150, usage.totalTokens)
        assertNull(usage.promptTokensDetails)
        assertNull(usage.promptCacheHitTokens)
        assertNull(usage.promptCacheMissTokens)
    }

    @Test
    fun should_create_choice_with_finish_reason() {
        val choice = DeepSeekResponse.Choice(
            index = 0,
            message = DeepSeekResponse.Message(role = "assistant", content = "done"),
            finishReason = "stop",
        )

        assertEquals("stop", choice.finishReason)
        assertNull(choice.logprobs)
    }

    @Test
    fun should_support_multiple_choices() {
        val choices = listOf(
            DeepSeekResponse.Choice(
                index = 0,
                message = DeepSeekResponse.Message(role = "assistant", content = "Option A"),
            ),
            DeepSeekResponse.Choice(
                index = 1,
                message = DeepSeekResponse.Message(role = "assistant", content = "Option B"),
            ),
        )
        val response = makeResponse(choices = choices)

        assertEquals(2, response.choices.size)
        assertEquals("Option A", response.choices[0].message.content)
        assertEquals("Option B", response.choices[1].message.content)
    }
}
