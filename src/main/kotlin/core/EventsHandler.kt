package core

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

typealias MessageReceiveCallback = (MessageReceivedEvent) -> Unit
typealias SlashCommandCallback = (SlashCommandInteractionEvent) -> Unit

class EventsHandler(
    private val slashCommandCallback: SlashCommandCallback
) : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent): Unit {
        return slashCommandCallback(event)
    }

}