package core.command

import net.dv8tion.jda.api.interactions.commands.OptionType

enum class Commands(val info: CommandInfo) {

    CONNECT(
        CommandInfo(
            name = "connect",
            description = "Connect to voice channel"
        )
    ),
    PLAY(
        CommandInfo(
            name = "play",
            description = "Play track",
            options = listOf(
                CommandInfo.Option(
                    name = "source",
                    description = "Track name or url"
                )
            )
        )
    ),
    PLAY_TOP(
        CommandInfo(
            name = "playtop",
            description = "Play track",
            options = listOf(
                CommandInfo.Option(
                    name = "source",
                    description = "Track name or url"
                )
            )
        )
    ),
    PLAY_TOP_PLUS(
        CommandInfo(
            name = "playtopplus",
            description = "Play track",
            options = listOf(
                CommandInfo.Option(
                    name = "source",
                    description = "Track name or url"
                )
            )
        )
    ),
    SKIP(
        CommandInfo(
            name = "skip",
            description = "Skip current track"
        )
    ),
    QUEUE(
        CommandInfo(
            name = "queue",
            description = "Show tracks queue"
        )
    ),
    RESUME(
        CommandInfo(
            name = "resume",
            description = "Resume track"
        )
    ),
    PAUSE(
        CommandInfo(
            name = "pause",
            description = "Pause track"
        )
    ),
    SHUFFLE(
        CommandInfo(
            name = "shuffle",
            description = "Shuffle queue"
        )
    ),
    LENGTH(
        CommandInfo(
            name = "length",
            description = "Playlist duration"
        )
    ),
    CLEAR(
        CommandInfo(
            name = "clear",
            description = "Clear queue"
        )
    ),
    CHAT(
        CommandInfo(
            name = "chat",
            description = "Send message to Ai",
            options = listOf(
                CommandInfo.Option(
                    name = "message",
                    description = "message"
                )
            )
        )
    ),
    HELP(
        CommandInfo(
            name = "help",
            description = "Show available commands"
        )
    ),
    NOW_PLAYING(
        CommandInfo(
            name = "nowplaying",
            description = "Show currently playing track"
        )
    ),
    BACKGROUND(
        CommandInfo(
            name = "background",
            description = "Toggle background track playback",
            options = listOf(
                CommandInfo.Option(
                    name = "loop",
                    description = "Loop the track (true/false)"
                )
            )
        )
    ),
    SUGGEST_MUSIC(
        CommandInfo(
            name = "suggestmusic",
            description = "AI suggests a song based on your request",
            options = listOf(
                CommandInfo.Option(
                    name = "request",
                    description = "Your mood, genre, or description (e.g., 'energetic rock', 'sad song')"
                )
            )
        )
    ),
    SUGGEST_PLAYLIST(
        CommandInfo(
            name = "suggestplaylist",
            description = "AI suggests songs and adds them to queue",
            options = listOf(
                CommandInfo.Option(
                    name = "request",
                    description = "Your mood, genre, or description (e.g., 'workout music', 'chill vibes')"
                ),
                CommandInfo.Option(
                    name = "count",
                    description = "Number of songs (1-20, default: 5)",
                    type = OptionType.INTEGER,
                    required = false,
                )
            )
        )
    )
}

data class CommandInfo(
    val name: String,
    val description: String,
    val options: List<Option>? = null,
) {
    /**
     * Описание опции slash-команды.
     *
     * @property name имя опции
     * @property description описание опции, отображаемое в Discord
     * @property type тип опции Discord; по умолчанию [OptionType.STRING]
     * @property required обязательна ли опция; по умолчанию `true`
     */
    data class Option(
        val name: String,
        val description: String,
        val type: OptionType = OptionType.STRING,
        val required: Boolean = true,
    )
}