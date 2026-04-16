package core

import core.command.CommandExecutor
import core.command.CommandFabric
import core.music.BotAudioPlayer
import moe.kyokobot.libdave.DaveFactory
import moe.kyokobot.libdave.NativeDaveFactory
import moe.kyokobot.libdave.jda.LDJDADaveSessionFactory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.audio.AudioModuleConfig
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import network.AiNetwork
import network.YoutubeNetwork


fun main(args: Array<String>) {

    val discordToken = ""
    val youtubeKey = ""
    val deepseekKey = ""
    val porcupineKey = ""

    val daveFactory: DaveFactory = NativeDaveFactory()
    val daveSessionFactory = LDJDADaveSessionFactory(daveFactory)

    val youtubeNetwork = YoutubeNetwork(youtubeKey)
    val aiNetwork = AiNetwork(deepseekKey)
    val botAudioPlayer = BotAudioPlayer(youtubeNetwork, porcupineKey)
    val commandFabric = CommandFabric(botAudioPlayer, aiNetwork)
    val commandExecutor = CommandExecutor(commandFabric)
    JDABuilder.createDefault(discordToken)
        .addEventListeners(EventsHandler { event -> commandExecutor(event) })
        .setAudioModuleConfig(AudioModuleConfig().withDaveSessionFactory(daveSessionFactory))
        .build()
        .awaitReady()
        .apply(::initCommands)
}

fun initCommands(jda: JDA) {
    val commandsList = mutableListOf<SlashCommandData>()
    core.command.Commands.entries.forEach { command ->
        val slashCommand = Commands.slash(
            command.info.name,
            command.info.description
        )
        command.info.options?.forEach { option ->
            slashCommand.addOption(
                OptionType.STRING,
                option.name,
                option.description,
                true
            )
        }
        commandsList.add(slashCommand)
    }
    jda.updateCommands().addCommands(commandsList).complete()
}