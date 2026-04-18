# Changelog

## 2026-04-18

### Безопасность
- **#1** Вынесены API-ключи (`DISCORD_TOKEN`, `YOUTUBE_KEY`, `DEEPSEEK_KEY`, `PORCUPINE_KEY`) из `Main.kt` в переменные окружения через `System.getenv()`. Добавлена функция `requireEnv()` с понятным исключением при отсутствии переменной. Создан файл `.env.example` с шаблоном переменных.
- **#2** Уровень HTTP-логирования в `RetrofitFactory` вынесен в переменную окружения `HTTP_LOG_LEVEL`. По умолчанию — `NONE` (секреты не утекают). Допустимые значения: `NONE`, `BASIC`, `HEADERS`, `BODY`.
- **#3** Добавлена обработка ошибок в `AiNetwork.chat()`: при HTTP 4xx/5xx бросается `NetworkException` с кодом и телом ошибки. В `AiChatCommand`, `SuggestMusicCommand`, `SuggestPlaylistCommand` добавлен `try/catch` — пользователь видит понятное сообщение вместо пустого ответа.
- **#4** Добавлена обработка ошибок в `YoutubeNetwork.findVideo()`: при ошибке квоты или сети бросается `NetworkException`. В `BotAudioPlayer.loadTrackByKeyword()` конвертируется в `AudioScheduleResult.Error`, команды показывают пользователю понятное сообщение.
- **#5** Добавлен `CoroutineExceptionHandler` в `CommandExecutor`. Необработанные исключения теперь логируются и отправляют пользователю понятное сообщение; deferred-reply больше не зависает.
- **#6** Заменены `println`/`e.printStackTrace()` на SLF4J `Logger` в `BotAudioPlayer`, `PorcupineReceiveHandler`, `CommandExecutor`, `RetrofitFactory`. В `:network` добавлена зависимость `slf4j-api`.
- **#14** Удалён отладочный `println(event.commandId)` из `EventsHandler`.
- **#7** В `CommandExecutor` добавлена null-проверка `guild` до запуска корутины. При вызове команды в DM пользователь получает ephemeral-ответ `"Эта команда доступна только на серверах."` вместо молчаливого NPE.
- **#8** В `BotAudioPlayer` заменён `HashMap` на `ConcurrentHashMap` с `computeIfAbsent`. Ключ сменён с `String` на `Long` (`guild.idLong`). Атомарное создание `GuildMusicManager` исключает гонку потоков.
- **#9** В `PorcupineReceiveHandler` добавлена синхронизация `synchronized(this)` вокруг составных операций с `monoBuffer`. Устранена гонка данных при одновременном аудио от нескольких пользователей.
- **#10** В `AudioPlayerSendHandler` буферы вынесены в поля класса и переиспользуются через `clear()`. Устранено выделение `ByteBuffer` 50 раз/сек на гильдию.
- **#11** Устранено дублирование `formatDuration`: метод перемещён в `music.DateUtils` (модуль `:music`), `TrackScheduler` использует общую утилиту. Старый `core.utils.DateUtils` удалён.
- **#12** Дублирующиеся блоки `when (result: AudioScheduleResult)` вынесены в `AudioScheduleResultHandler.kt`. Созданы extension-функции `toText()` и `toStatusLine()`, используемые в `SuggestMusicCommand` и `SuggestPlaylistCommand`.
- **#13** Все пользовательские ответы команд приведены к русскому языку.
- **#15** Упрощён `DateUtils.formatDuration()`: избыточный `StringBuilder` заменён прямым `return if/else`.
- **#16** `TtsNetwork` вынесен в DI: создаётся в `Main.kt` и передаётся через конструктор `CommandFabric` наравне с остальными зависимостями.
- **#17** Путь `"recorded.wav"` вынесен в `Constants.RECORDED_AUDIO_FILE`.
- **#18** Исправлена опечатка пакета `core.core.music` → `core.music` в `PorcupineReceiveHandler`, `PorcupineFeeder`, `Downsampler48kTo16k`. Обновлены все импорты.
- **#19** В `CommandInfo.Option` добавлены поля `type: OptionType` и `required: Boolean` с дефолтами.
- **#20** Версия JVM приведена к единой: `jvmToolchain(11)` во всех модулях (`:music`, `:network`, root).
- **#21** `FindByKeywordResponse.nextPageToken` сделан nullable (`String?`).
- **#22** Добавлена `@SerializedName("max_tokens")` в `DeepSeekRequest`.
- **#23** Исправлен `writeTimeout` в `RetrofitFactory`: `TimeUnit.MINUTES` → `TimeUnit.SECONDS`.
- **#24** Исправлен сдвиг буфера в `PorcupineReceiveHandler.processBuffer()`: `subList(0, overlap)` → `subList(0, frameSize)`.
- **#25** В `GuildMusicManager` добавлен флаг `backgroundTrackActive` для разделения TTS и фонового трека. `isBackgroundPlaying()` теперь не возвращает `true` во время TTS. Буфер больше не растёт бесконечно при долгой записи. Таймаут записи теперь 5 секунд, а не 5 минут. Gson теперь корректно сериализует лимит токенов для DeepSeek API. Gson больше не кинет NPE на последней странице результатов YouTube. Опция `count` у `SUGGEST_PLAYLIST` теперь типа `INTEGER` — Discord показывает числовой виджет. Парсинг заменён на `asInt`. `BotAudioPlayer` и `PorcupineReceiveHandler` используют одну константу. Переведены: `BackgroundCommand`, `ClearQueueCommand`, `ConnectVoiceChannelCommand`, `PausePlayerCommand`, `ResumePlayerCommand`, `ShuffleCommand`, `SkipTrackCommand`, `NowPlayingCommand`, `AiChatCommand`, `PlayMusicCommand`.
