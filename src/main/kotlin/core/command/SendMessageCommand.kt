package core.command

import core.models.Event
import core.models.Reply

/**
 * Команда отправки фиксированного текстового сообщения пользователю.
 *
 * @property message текст сообщения для отправки
 */
class SendMessageCommand(
    private val message: String,
) : Command() {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply =
        Reply.Text(message)
}