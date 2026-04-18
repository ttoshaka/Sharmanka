# Changelog

## 2026-04-18

### Безопасность
- **#1** Вынесены API-ключи (`DISCORD_TOKEN`, `YOUTUBE_KEY`, `DEEPSEEK_KEY`, `PORCUPINE_KEY`) из `Main.kt` в переменные окружения через `System.getenv()`. Добавлена функция `requireEnv()` с понятным исключением при отсутствии переменной. Создан файл `.env.example` с шаблоном переменных.
- **#2** Уровень HTTP-логирования в `RetrofitFactory` вынесен в переменную окружения `HTTP_LOG_LEVEL`. По умолчанию — `NONE` (секреты не утекают). Допустимые значения: `NONE`, `BASIC`, `HEADERS`, `BODY`.

### Обработка ошибок
- **#3** Добавлена обработка ошибок в `AiNetwork.chat()`: при HTTP 4xx/5xx бросается `NetworkException`. В `AiChatCommand`, `SuggestMusicCommand`, `SuggestPlaylistCommand` добавлен `try/catch`.
- **#4** Добавлена обработка ошибок в `YoutubeNetwork.findVideo()`: при ошибке квоты или сети бросается `NetworkException`. В `BotAudioPlayer` конвертируется в `AudioScheduleResult.Error`.
- **#5** Добавлен `CoroutineExceptionHandler` в `CommandExecutor`. Deferred-reply больше не зависает при ошибке.
- **#6** Заменены `println`/`e.printStackTrace()` на SLF4J `Logger` в `BotAudioPlayer`, `PorcupineReceiveHandler`, `CommandExecutor`, `RetrofitFactory`.
- **#7** В `CommandExecutor` добавлена null-проверка `guild` до запуска корутины. При DM-команде — ephemeral-ответ.

### Многопоточность
- **#8** В `BotAudioPlayer` заменён `HashMap` на `ConcurrentHashMap` с `computeIfAbsent`. Устранена гонка при создании `GuildMusicManager`.
- **#9** В `PorcupineReceiveHandler` добавлен `synchronized(this)` вокруг операций с `monoBuffer`.
- **#10** В `AudioPlayerSendHandler` буферы переиспользуются через `clear()`. Устранено выделение `ByteBuffer` 50 раз/сек на гильдию.

### Качество кода
- **#11** Устранено дублирование `formatDuration`: перемещён в `music.DateUtils`, удалён `core.utils.DateUtils`.
- **#12** Дублирующиеся `when (AudioScheduleResult)` вынесены в `AudioScheduleResultHandler.kt` с `toText()` и `toStatusLine()`.
- **#13** Все пользовательские ответы команд приведены к русскому языку.
- **#14** Удалён отладочный `println(event.commandId)` из `EventsHandler`.
- **#15** Упрощён `DateUtils.formatDuration()`: `StringBuilder` заменён прямым `return if/else`.

### Архитектура
- **#16** `TtsNetwork` вынесен в DI: создаётся в `Main.kt` и передаётся через конструктор `CommandFabric`.
- **#17** Путь `"recorded.wav"` вынесен в `Constants.RECORDED_AUDIO_FILE`.
- **#18** Исправлена опечатка пакета `core.core.music` → `core.music`.
- **#19** В `CommandInfo.Option` добавлены `type: OptionType` и `required: Boolean`. `SUGGEST_PLAYLIST.count` → `INTEGER`.
- **#20** `jvmToolchain(11)` установлен во всех модулях (`:music`, `:network`, root).

### Баги
- **#21** `FindByKeywordResponse.nextPageToken` сделан nullable (`String?`).
- **#22** Добавлена `@SerializedName("max_tokens")` в `DeepSeekRequest`.
- **#23** Исправлен `writeTimeout` в `RetrofitFactory`: `TimeUnit.MINUTES` → `TimeUnit.SECONDS`.
- **#24** Исправлен сдвиг буфера в `PorcupineReceiveHandler.processBuffer()`: `subList(0, overlap)` → `subList(0, frameSize)`.
- **#25** В `GuildMusicManager` добавлен флаг `backgroundTrackActive` для разделения TTS и фонового трека.
- **#26** В `AiChatCommand` добавлена проверка `answer.isBlank()` — TTS не вызывается при пустом ответе.
- **#27** В `SuggestPlaylistCommand.extractSongsFromResponse()` фильтр `contains("-")` заменён на regex `^\d+[.\-)\s]+(.+)$`.

### Зависимости и сборка
- **#28** Из `gradle.properties` удалены неиспользуемые и устаревшие версии зависимостей.
- **#29** `kotlinx-coroutines-core` в `:network` обновлён с `1.8.0-RC` до стабильной `1.8.1`.
- **#30** Удалена неиспользуемая зависимость `jackson-module-kotlin` из `:network` — весь слой использует Gson.
- **#31** Удалены неиспользуемые зависимости `be.tarsos.dsp:core` и `be.tarsos.dsp:jvm` и их Maven-репозиторий. Ресемплинг работает через `javax.sound.sampled.AudioSystem`. Оставлена только настройка `kotlin.code.style`.
