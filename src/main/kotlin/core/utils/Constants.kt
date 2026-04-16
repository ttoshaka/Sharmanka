package core.utils

object Constants {
    const val MAX_QUEUE_DISPLAY_SIZE = 23
    const val IMAGE_STATUS_POLL_DELAY_MS = 10000L
    const val URL_PATTERN = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]" +
            "+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?\$"
}
