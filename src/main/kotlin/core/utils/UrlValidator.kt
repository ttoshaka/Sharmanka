package core.utils

import java.util.regex.Pattern

object UrlValidator {

    private val urlPattern = Pattern.compile(Constants.URL_PATTERN)

    fun isValidUrl(url: String): Boolean {
        return urlPattern.matcher(url).matches()
    }
}
