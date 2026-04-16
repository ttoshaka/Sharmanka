package core.command

import core.models.Event
import core.models.Reply

class SendMessageCommand(
    private val message: String,
) : Command() {

    override suspend fun invoke(event: Event): Reply =
        Reply.Text(message)
}