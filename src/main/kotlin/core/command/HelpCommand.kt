package core.command

import core.models.Event
import core.models.Reply
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class HelpCommand : Command() {

    override suspend fun invoke(event: Event): Reply {
        val embed = EmbedBuilder()
            .setTitle("📚 Available Commands")
            .setColor(Color.CYAN)
            .setDescription("Here are all available commands for the bot:")

        Commands.entries.forEach { command ->
            val commandInfo = command.info
            val optionsText = commandInfo.options?.joinToString("\n") { 
                "• **${it.name}**: ${it.description}" 
            } ?: "No options"
            
            embed.addField(
                "/${commandInfo.name}",
                "**Description**: ${commandInfo.description}\n**Options**:\n$optionsText",
                false
            )
        }

        embed.setFooter("Use /<command> to execute a command")
        
        return Reply.Embed(embed.build())
    }
}