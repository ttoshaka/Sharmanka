# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Sharmanka** — Discord music bot on Kotlin/JVM. Plays audio from YouTube and Twitch in voice channels, supports AI chat (DeepSeek), TTS playback via a local server, and wake word detection ("OK Google" via Porcupine).

## Build & Run

```bash
# Build
./gradlew build

# Run directly
./gradlew run

# Build fat JAR (for deployment)
./gradlew shadowJar
# Output: build/libs/Sharmanka-1.0-SNAPSHOT-all.jar
```

There are no tests in this project.

## Module Structure

Multi-module Gradle project (Kotlin 2.2, JVM 11):

- **Root (`:`)** — bot entry point, command logic, audio management, wake word
- **`:network`** — Retrofit-based API clients: DeepSeek AI, YouTube Data API v3, TTS (local)
- **`:music`** — pure audio data models: `TrackQueue`, `TrackScheduler`, `AudioItem`, `TrackOrder`

## Architecture

### Startup (`Main.kt`)
API keys are hardcoded. JDA is built with a `libdave` session factory (DAVE E2EE protocol). `EventsHandler` routes slash command interactions to `CommandExecutor`.

### Command Pipeline
```
SlashCommandInteractionEvent
  → EventsHandler (ListenerAdapter)
    → CommandExecutor (builds Event, launches coroutine on Dispatchers.IO)
      → CommandFabric.createCommand(name) → Command subclass
        → Command.invoke(event): Reply
          → Reply.invoke(interaction, isLong).complete()
```

- **`Command`** — abstract base; `isLongCommand = true` causes `deferReply()` before the coroutine launches (needed for slow AI/network calls), then `editOriginal()` on the hook.
- **`Reply`** — sealed class (`Text`, `Embed`, `File`); handles both normal and deferred reply variants.
- **`Commands` enum** — single source of truth for slash command names, descriptions, and options. Adding a new command requires: adding an entry here, a case in `CommandFabric`, and a `Command` subclass.

### Audio Architecture

Two separate `AudioPlayerManager` instances per bot:
1. **Main player** — YouTube + Twitch streams
2. **Background player** — local WAV files (TTS output and looped background tracks)

Per guild, a `GuildMusicManager` holds:
- `player` (main, volume 30) + `scheduler: TrackScheduler`
- `backgroundPlayer` (volume 100) + a TTS queue (`LinkedBlockingQueue`)
- `sendHandler: AudioPlayerSendHandler` — mixes both players into one JDA audio send handler

`BotAudioPlayer` is a singleton that manages the `Map<guildId, GuildMusicManager>` and exposes all audio operations to commands.

### Queue Ordering (`TrackOrder`)

| Value | Behavior |
|---|---|
| `NORMAL` | Append to end |
| `TOP` | Insert at front (`addFirst`) |
| `TOP_SEPARATE` | Insert after the last `TOP_SEPARATE` item (preserves a "priority block") |

### Wake Word & Voice Recording
`PorcupineReceiveHandler` (registered as JDA's audio receive handler) listens for "OK Google". On detection it records audio until 1.5s of silence, then saves to `recorded.wav` in the working directory. The `BackgroundCommand` then plays this file back as a background track.

### Network Layer (`:network`)
All APIs use Retrofit via `RetrofitFactory`. Three clients:
- `AiNetwork` → `https://api.deepseek.com/` — chat completions (`deepseek-chat` model)
- `YoutubeNetwork` → YouTube Data API v3 — keyword → video URL lookup
- `TtsNetwork` → `http://localhost:8000/` — local Silero TTS server (speaker `"baya"`)

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.2 (JVM 11) |
| Build | Gradle (Kotlin DSL), Shadow plugin для fat JAR |
| Discord | JDA 6, libdave (DAVE E2EE audio protocol) |
| Audio playback | LavaPlayer (devoxin fork) + YouTube audio source (lavalink) |
| Audio mixing | TarsosDSP — ресемплинг 48kHz stereo → 16kHz mono |
| AI | DeepSeek API (`deepseek-chat`) через Retrofit |
| TTS | Локальный Silero-сервер (`localhost:8000`), speaker `baya` |
| Wake word | Porcupine (Picovoice), ключевое слово "OK Google" |
| HTTP | Retrofit 2 + OkHttp |
| Concurrency | Kotlin Coroutines (`Dispatchers.IO`, `SupervisorJob`) |

## Key External Dependencies

| Dependency | Purpose |
|---|---|
| `net.dv8tion:JDA:6.4.1` | Discord API |
| `com.github.devoxin.lavaplayer` | Audio playback engine |
| `dev.lavalink.youtube:common` | YouTube audio source |
| `ai.picovoice:porcupine-java` | Wake word detection |
| `moe.kyokobot.libdave` | Discord DAVE E2EE audio protocol |
| `be.tarsos.dsp` | Audio resampling (48kHz stereo → 16kHz mono) |

## Naming

| Тип                       | Шаблон                                                      | Пример           |
|---------------------------|-------------------------------------------------------------|------------------|
| Модель запроса в сеть     | [Name]Request                                               | DeepSeekRequest  |
| Модель ответа вызова сети | [Name]Response                                              | DeepSeekResponse |
| Команда бота              | [Name]Command                                               | QueueCommand     |

## Bot commands

- Commands are separate classes.
- Each command has a single responsibility.
- Commands can be combined to create chains.
- Must not contain business logic outside domain layer.

## Documentation

- Every `public` or `protected` method, field, property must be specified in the KD

## Code style

- First comes `override` methods and field, then `public` fields and methods, then `protected`, and then `private`.
- Use trailing comma
- Use naming parameters for two and more function.
  Example:
  ```kotlin
    fun example() {
        val result = anotherMethod(
            name = "Name example",
            count = 3,
        )
    }
  ```

## Class code template

```kotlin
package core.command

import core.models.Event
import core.models.Reply
import core.music.BotAudioPlayer

/**
 * Команда очистки очереди трэков
 * 
 * @property botAudioPlayer плэйер для проигрывание аудио в discord
 */
class ClearQueueCommand(
    private val botAudioPlayer: BotAudioPlayer
): Command(false) {
    
    override suspend fun invoke(event: Event): Reply {
        botAudioPlayer.clearQueue(event.guild)
        return Reply.Text("Cleared")
    }

    /**
     * Public method for example
     */
    fun publicExampleFun() {

    }
    
    private fun privateExampleFun() {
        
    }
}
```

## Agent Selection Rules

- If task is about creating commands → use skill `new-command` (it collects info from user, then delegates to developer-command agent)
- If task involves writing tests, improving test coverage, refactoring code to make it testable, or following up on test coverage recommendations → use skill `test-coverage`

Always select the most specific agent before performing a task.