package core.models

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageEditData

sealed class Reply {

    operator fun invoke(interaction: SlashCommandInteractionEvent, isLong: Boolean): RestAction<*> =
        if (isLong) executeLong(interaction) else execute(interaction)

    protected abstract fun execute(interaction: SlashCommandInteractionEvent): RestAction<*>

    protected abstract fun executeLong(interaction: SlashCommandInteractionEvent): RestAction<*>

    data class Text(val value: String) : Reply() {
        override fun execute(interaction: SlashCommandInteractionEvent): RestAction<*> =
            interaction.reply(value)

        override fun executeLong(interaction: SlashCommandInteractionEvent): RestAction<*> =
            interaction.hook.editOriginal(value)
    }

    data class Embed(val value: MessageEmbed) : Reply() {
        override fun execute(interaction: SlashCommandInteractionEvent): RestAction<*> =
            interaction.replyEmbeds(value)

        override fun executeLong(interaction: SlashCommandInteractionEvent): RestAction<*> =
            interaction.hook.editOriginalEmbeds(value)
    }

    data class File(val value: FileUpload) : Reply() {
        override fun execute(interaction: SlashCommandInteractionEvent): RestAction<*> =
            interaction.replyFiles(value)

        override fun executeLong(interaction: SlashCommandInteractionEvent): RestAction<*> =
            interaction.hook.editOriginal(MessageEditData.fromFiles(value))
    }
}