package core.command

import core.models.Reply
import core.models.Event

/**
 * Базовый класс для всех slash-команд бота.
 *
 * @property isLongCommand если `true`, перед выполнением вызывается `deferReply()` (для медленных операций)
 */
abstract class Command(val isLongCommand: Boolean = false) {

    /**
     * Выполняет команду и возвращает ответ пользователю.
     *
     * @param event событие с данными вызова команды
     * @return ответ, который будет отправлен в Discord
     */
    abstract suspend operator fun invoke(event: Event): Reply
}