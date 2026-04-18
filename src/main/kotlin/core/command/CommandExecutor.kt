package core.command

import core.models.Event
import core.models.Member
import kotlinx.coroutines.CoroutineExceptionHandler
import org.slf4j.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * Исполнитель slash-команд Discord.
 *
 * Принимает [SlashCommandInteractionEvent], строит [Event], определяет нужную команду
 * через [CommandFabric] и запускает её в фоновом coroutine-scope.
 * Все необработанные исключения перехватываются [CoroutineExceptionHandler]:
 * ошибка логируется и пользователь получает короткое сообщение через interaction.
 *
 * @property commandFabric фабрика для создания команд по имени
 */
class CommandExecutor(private val commandFabric: CommandFabric) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private val logger = LoggerFactory.getLogger(CommandExecutor::class.java)
    }

    /**
     * Обрабатывает входящее событие slash-команды.
     *
     * Если команда вызвана в личных сообщениях (guild == null), пользователь получает
     * ephemeral-ответ и метод завершается без запуска корутины.
     *
     * @param interaction событие взаимодействия со slash-командой
     */
    operator fun invoke(interaction: SlashCommandInteractionEvent) {
        val guild = interaction.guild
        if (guild == null) {
            interaction.reply("Эта команда доступна только на серверах.")
                .setEphemeral(true)
                .complete()
            return
        }

        val optionsMap = mutableMapOf<String, String>()
        interaction.options.forEach { optionMapping ->
            optionsMap[optionMapping.name] = optionMapping.asString
        }

        val event = Event(
            guild = guild,
            name = interaction.name,
            member = Member(
                voiceChannel = interaction.member?.voiceState?.channel,
                messageChannel = interaction.messageChannel,
                nickname = interaction.member?.nickname ?: "UNKNOWN",
                avatarUrl = interaction.member?.avatarUrl ?: "",
            ),
            options = optionsMap,
        )
        val command = commandFabric.createCommand(event.name.toCommand())
        if (command.isLongCommand) interaction.deferReply().complete()

        val exceptionHandler = buildExceptionHandler(
            interaction = interaction,
            isLongCommand = command.isLongCommand,
        )
        scope.launch(exceptionHandler) {
            command(event).invoke(interaction, command.isLongCommand).complete()
        }
    }

    private fun String.toCommand(): Commands? =
        Commands.entries.find { it.info.name == this }

    /**
     * Создаёт [CoroutineExceptionHandler], который логирует исключение и отправляет
     * пользователю сообщение об ошибке через [interaction].
     *
     * Если команда была deferred ([isLongCommand] == true), используется
     * `hook.editOriginal`; иначе — `interaction.reply` (ephemeral).
     *
     * @param interaction событие взаимодействия, через которое отправляется ответ
     * @param isLongCommand признак того, что команда уже вызвала `deferReply()`
     */
    private fun buildExceptionHandler(
        interaction: SlashCommandInteractionEvent,
        isLongCommand: Boolean,
    ): CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logger.error("Необработанное исключение в команде '${interaction.name}'", throwable)

        val errorMessage = "Произошла ошибка при выполнении команды. Попробуйте ещё раз."
        if (isLongCommand) {
            interaction.hook.editOriginal(errorMessage).complete()
        } else {
            interaction.reply(errorMessage).setEphemeral(true).complete()
        }
    }
}