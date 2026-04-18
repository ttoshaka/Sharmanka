package core.command

import core.music.BotAudioPlayer
import music.TrackOrder
import network.AiNetwork
import network.TtsNetwork

class CommandFabric(
    private val botAudioPlayer: BotAudioPlayer,
    private val aiNetwork: AiNetwork
) {
    private val ttsNetwork = TtsNetwork()

    fun createCommand(commandName: Commands?): Command =
        when (commandName) {
            Commands.CONNECT -> ConnectVoiceChannelCommand(botAudioPlayer)
            Commands.PLAY -> PlayMusicCommand(
                botAudioPlayer = botAudioPlayer,
                order = TrackOrder.NORMAL
            )

            Commands.PLAY_TOP -> PlayMusicCommand(
                botAudioPlayer = botAudioPlayer,
                order = TrackOrder.TOP_SEPARATE
            )



            Commands.PLAY_TOP_PLUS -> PlayMusicCommand(
                botAudioPlayer = botAudioPlayer,
                order = TrackOrder.TOP
            )

            Commands.SKIP -> SkipTrackCommand(botAudioPlayer)
            Commands.QUEUE -> QueueCommand(botAudioPlayer)
            Commands.RESUME -> ResumePlayerCommand(botAudioPlayer)
            Commands.PAUSE -> PausePlayerCommand(botAudioPlayer)
            Commands.SHUFFLE -> ShuffleCommand(botAudioPlayer)
            Commands.LENGTH -> LengthCommand(botAudioPlayer)
            Commands.CLEAR -> ClearQueueCommand(botAudioPlayer)
            Commands.CHAT -> AiChatCommand(aiNetwork, ttsNetwork, botAudioPlayer)
            Commands.HELP -> HelpCommand()
            Commands.NOW_PLAYING -> NowPlayingCommand(botAudioPlayer)
            Commands.BACKGROUND -> BackgroundCommand(botAudioPlayer)
            Commands.SUGGEST_MUSIC -> SuggestMusicCommand(aiNetwork, botAudioPlayer)
            Commands.SUGGEST_PLAYLIST -> SuggestPlaylistCommand(aiNetwork, botAudioPlayer)
            Commands.DRAGULA -> DragulaCommand(botAudioPlayer)
            null -> SendMessageCommand("Command name is null")
        }
}