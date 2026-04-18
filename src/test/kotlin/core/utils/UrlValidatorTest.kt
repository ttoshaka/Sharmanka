package core.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UrlValidatorTest {

    @Test
    fun should_return_true_for_https_url() {
        assertTrue(UrlValidator.isValidUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
    }

    @Test
    fun should_return_true_for_http_url() {
        assertTrue(UrlValidator.isValidUrl("http://example.com"))
    }

    @Test
    fun should_return_true_for_url_without_www() {
        assertTrue(UrlValidator.isValidUrl("https://youtube.com/watch?v=abc123"))
    }

    @Test
    fun should_return_true_for_url_with_path() {
        assertTrue(UrlValidator.isValidUrl("https://open.spotify.com/track/abc"))
    }

    @Test
    fun should_return_true_for_url_with_port() {
        assertTrue(UrlValidator.isValidUrl("http://example.com:8080/path"))
    }

    @Test
    fun should_return_false_for_plain_text() {
        assertFalse(UrlValidator.isValidUrl("just a search query"))
    }

    @Test
    fun should_return_false_for_empty_string() {
        assertFalse(UrlValidator.isValidUrl(""))
    }

    @Test
    fun should_return_false_for_string_without_tld() {
        assertFalse(UrlValidator.isValidUrl("notaurl"))
    }

    @Test
    fun should_return_true_for_url_with_query_params() {
        assertTrue(UrlValidator.isValidUrl("https://www.youtube.com/results?search_query=kotlin"))
    }

    @Test
    fun should_return_true_for_twitch_stream_url() {
        assertTrue(UrlValidator.isValidUrl("https://www.twitch.tv/somestreamer"))
    }

    @Test
    fun should_return_false_for_text_with_spaces() {
        assertFalse(UrlValidator.isValidUrl("hello world"))
    }
}
