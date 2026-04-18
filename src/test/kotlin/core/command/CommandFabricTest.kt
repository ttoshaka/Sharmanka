package core.command

import core.music.BotAudioPlayer
import core.utils.EmbedFactory
import io.mockk.mockk
import music.TrackOrder
import network.AiNetwork
import org.junit.jupiter.api.Test
import kotlin.test.assertIs

class CommandFabricTest {

    private val botAudioPlayer = mockk<BotAudioPlayer>(relaxed = true)
    private val aiNetwork = mockk<AiNetwork>(relaxed = true)
    private val embedFactory = mockk<EmbedFactory>(relaxed = true)
    private val fabric = CommandFabric(
        botAudioPlayer = botAudioPlayer,
        aiNetwork = aiNetwork,
        embedFactory = embedFactory,
    )

    @Test
    fun should_create_connect_command() {
        assertIs<ConnectVoiceChannelCommand>(fabric.createCommand(Commands.CONNECT))
    }

    @Test
    fun should_create_play_command_with_normal_order() {
        val command = fabric.createCommand(Commands.PLAY)
        assertIs<PlayMusicCommand>(command)
    }

    @Test
    fun should_create_play_top_command() {
        assertIs<PlayMusicCommand>(fabric.createCommand(Commands.PLAY_TOP))
    }

    @Test
    fun should_create_play_top_plus_command() {
        assertIs<PlayMusicCommand>(fabric.createCommand(Commands.PLAY_TOP_PLUS))
    }

    @Test
    fun should_create_skip_command() {
        assertIs<SkipTrackCommand>(fabric.createCommand(Commands.SKIP))
    }

    @Test
    fun should_create_queue_command() {
        assertIs<QueueCommand>(fabric.createCommand(Commands.QUEUE))
    }

    @Test
    fun should_create_resume_command() {
        assertIs<ResumePlayerCommand>(fabric.createCommand(Commands.RESUME))
    }

    @Test
    fun should_create_pause_command() {
        assertIs<PausePlayerCommand>(fabric.createCommand(Commands.PAUSE))
    }

    @Test
    fun should_create_shuffle_command() {
        assertIs<ShuffleCommand>(fabric.createCommand(Commands.SHUFFLE))
    }

    @Test
    fun should_create_length_command() {
        assertIs<LengthCommand>(fabric.createCommand(Commands.LENGTH))
    }

    @Test
    fun should_create_clear_command() {
        assertIs<ClearQueueCommand>(fabric.createCommand(Commands.CLEAR))
    }

    @Test
    fun should_create_help_command() {
        assertIs<HelpCommand>(fabric.createCommand(Commands.HELP))
    }

    @Test
    fun should_create_now_playing_command() {
        assertIs<NowPlayingCommand>(fabric.createCommand(Commands.NOW_PLAYING))
    }

    @Test
    fun should_create_background_command() {
        assertIs<BackgroundCommand>(fabric.createCommand(Commands.BACKGROUND))
    }

    @Test
    fun should_create_send_message_command_when_null() {
        assertIs<SendMessageCommand>(fabric.createCommand(null))
    }

    @Test
    fun should_create_suggest_music_command() {
        assertIs<SuggestMusicCommand>(fabric.createCommand(Commands.SUGGEST_MUSIC))
    }

    @Test
    fun should_create_suggest_playlist_command() {
        assertIs<SuggestPlaylistCommand>(fabric.createCommand(Commands.SUGGEST_PLAYLIST))
    }
}
