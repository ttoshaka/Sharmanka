package core.command

import core.models.Event
import core.models.Member
import kotlinx.coroutines.CoroutineExceptionHandler
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

    /**
     * Обрабатывает входящее событие slash-команды.
     *
     * @param interaction событие взаимодействия со slash-командой
     */
    operator fun invoke(interaction: SlashCommandInteractionEvent) {
        val optionsMap = mutableMapOf<String, String>()
        interaction.options.forEach { optionMapping ->
            optionsMap[optionMapping.name] = optionMapping.asString
        }

        val event = Event(
            guild = interaction.guild!!,
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
        System.err.println("[CommandExecutor] Необработанное исключение в команде '${interaction.name}': $throwable")
        throwable.printStackTrace()

        val errorMessage = "Произошла ошибка при выполнении команды. Попробуйте ещё раз."
        if (isLongCommand) {
            interaction.hook.editOriginal(errorMessage).complete()
        } else {
            interaction.reply(errorMessage).setEphemeral(true).complete()
        }
    }
}