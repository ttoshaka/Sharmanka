package core.command

import core.models.Event
import core.models.Member
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class CommandExecutor(private val commandFabric: CommandFabric) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
            options = optionsMap
        )
        val command = commandFabric.createCommand(event.name.toCommand())
        if (command.isLongCommand) interaction.deferReply().complete()
        scope.launch { command(event).invoke(interaction, command.isLongCommand).complete() }
    }

    private fun String.toCommand(): Commands? =
        Commands.entries.find { it.info.name == this }
}