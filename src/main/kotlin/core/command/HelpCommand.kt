package core.command

import core.models.Event
import core.models.Reply
import core.utils.EmbedFactory

/**
 * Команда вывода списка всех доступных команд бота
 *
 * @property embedFactory фабрика для построения embed-сообщений
 */
class HelpCommand(
    private val embedFactory: EmbedFactory,
) : Command() {

    /** Выполняет команду и возвращает ответ пользователю. */
    override suspend fun invoke(event: Event): Reply =
        Reply.Embed(embedFactory.buildHelpEmbed())
}
