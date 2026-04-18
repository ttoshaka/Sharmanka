package core.command

import core.music.BotAudioPlayer
import core.utils.EmbedFactory
import core.utils.EmbedFactoryImpl
import music.TrackOrder
import network.AiNetwork
import network.TtsNetwork

/**
 * Фабрика команд: создаёт конкретный экземпляр [Command] по имени slash-команды.
 *
 * @property botAudioPlayer плеер для передачи в аудио-команды
 * @property aiNetwork клиент AI для передачи в AI-команды
 * @property ttsNetwork клиент TTS для передачи в команды с синтезом речи
 * @property embedFactory фабрика embed-сообщений для передачи в команды с UI
 */
class CommandFabric(
    private val botAudioPlayer: BotAudioPlayer,
    private val aiNetwork: AiNetwork,
    private val ttsNetwork: TtsNetwork,
    private val embedFactory: EmbedFactory = EmbedFactoryImpl(),
) {

    /**
     * Создаёт экземпляр команды по её имени.
     *
     * @param commandName идентификатор команды из enum [Commands], или `null` если имя не распознано
     * @return готовый к выполнению экземпляр [Command]
     */
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
            Commands.QUEUE -> QueueCommand(botAudioPlayer, embedFactory)
            Commands.RESUME -> ResumePlayerCommand(botAudioPlayer)
            Commands.PAUSE -> PausePlayerCommand(botAudioPlayer)
            Commands.SHUFFLE -> ShuffleCommand(botAudioPlayer)
            Commands.LENGTH -> LengthCommand(botAudioPlayer)
            Commands.CLEAR -> ClearQueueCommand(botAudioPlayer)
            Commands.CHAT -> AiChatCommand(aiNetwork, ttsNetwork, botAudioPlayer)
            Commands.HELP -> HelpCommand(embedFactory)
            Commands.NOW_PLAYING -> NowPlayingCommand(botAudioPlayer, embedFactory)
            Commands.BACKGROUND -> BackgroundCommand(botAudioPlayer)
            Commands.SUGGEST_MUSIC -> SuggestMusicCommand(aiNetwork, botAudioPlayer)
            Commands.SUGGEST_PLAYLIST -> SuggestPlaylistCommand(aiNetwork, botAudioPlayer)
            null -> SendMessageCommand("Command name is null")
        }
}