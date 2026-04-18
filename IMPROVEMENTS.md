# Список улучшений проекта Sharmanka

## Безопасность

1. **Вынести API-ключи из `Main.kt` в переменные окружения** — все четыре ключа (`DISCORD_TOKEN`, `YOUTUBE_KEY`, `DEEPSEEK_KEY`, `PORCUPINE_KEY`) захардкожены пустыми строками; заменить на `System.getenv("...")` и добавить `.env.example`.

2. **Убрать логирование тела запросов в проде** — `RetrofitFactory` использует `HttpLoggingInterceptor.Level.BODY` безусловно, что выводит `Authorization`-заголовок с ключом DeepSeek; уровень логирования должен управляться переменной окружения.

---

## Обработка ошибок

3. **Добавить проверку `Response.isSuccessful` в `AiNetwork.chat()`** — при сетевой ошибке или 401/429 метод молча возвращает `""`, пользователь получает пустой ответ без сообщения об ошибке.

4. **Добавить проверку `Response.isSuccessful` в `YoutubeNetwork.findVideo()`** — аналогично: при превышении квоты или сетевой ошибке `null` возвращается без логирования причины.

5. **Добавить `CoroutineExceptionHandler` в `CommandExecutor`** — необработанные исключения в `scope.launch { ... }` молча поглощаются `SupervisorJob`; пользователь не получает никакого ответа, а deferred-reply зависает и таймаутится со стороны Discord.

6. **Заменить `println` на Logger в `BotAudioPlayer` и `PorcupineReceiveHandler`** — `println("Failed to load...")` и `e.printStackTrace()` обходят logback и невидимы в агрегаторах логов.

7. **Заменить `interaction.guild!!` на безопасную проверку в `CommandExecutor`** — NPE при DM-команде поглощается scope молча.

---

## Многопоточность

8. **Заменить `HashMap` на `ConcurrentHashMap` с `computeIfAbsent` в `BotAudioPlayer`** — `musicManagers` не потокобезопасна; одновременные команды из одной гильдии могут создать два `GuildMusicManager` и потерять очередь.

9. **Обеспечить потокобезопасность `monoBuffer` в `PorcupineReceiveHandler`** — `mutableListOf<Short>()` (ArrayList) изменяется из JDA-потоков для разных пользователей канала без синхронизации.

10. **Устранить GC-давление в `AudioPlayerSendHandler.clearFrames()`** — `ByteBuffer.allocate(FRAME_SIZE)` вызывается 50 раз в секунду на каждую гильдию; заменить на переиспользование буферов через `clear()`.

---

## Качество кода

11. **Убрать дублирование `formatDuration` из `TrackScheduler`** — метод идентичен `DateUtils.formatDuration()`; `TrackScheduler` должен вызывать общую утилиту.

12. **Вынести общую логику из `SuggestMusicCommand` и `SuggestPlaylistCommand`** — блок `when (result)` для `AudioScheduleResult` практически скопирован; вынести в shared-хелпер.

13. **Привести язык ответов команд к единому** — часть команд отвечает по-английски (`"Track added."`), часть по-русски (`"Трек добавлен в очередь."`); выбрать один язык и придерживаться его везде.

14. **Убрать дебаг-`println` из `EventsHandler.onSlashCommandInteraction()`** — `println(event.commandId)` выглядит как отладочный артефакт.

15. **Упростить `DateUtils.formatDuration()`** — `StringBuilder` с одним `append` и `toString()` избыточен; заменить прямым `return String.format(...)`.

---

## Архитектура

16. **Передавать `TtsNetwork` через DI, а не создавать внутри `CommandFabric`** — прямой `TtsNetwork()` нарушает паттерн DI, применённый для остальных зависимостей, и делает тестирование невозможным.

17. **Вынести путь к `recorded.wav` в именованную константу** — `BotAudioPlayer` и `PorcupineReceiveHandler` используют этот путь независимо; при расхождении рабочих директорий они молча разойдутся.

18. **Исправить пакет `core.core.music` → `core.music`** — `PorcupineReceiveHandler`, `PorcupineFeeder`, `Downsampler48kTo16k` объявлены в `package core.core.music` (двойной `core`), что является опечаткой.

19. **Добавить поле `OptionType` в `CommandInfo.Option` и использовать `OptionType.INTEGER` для числовых опций** — `SUGGEST_PLAYLIST.count` зарегистрирован как `STRING` и парсится через `toIntOrNull()`; Discord поддерживает числовой тип, который даёт нативный UI-виджет.

20. **Привести целевую версию JVM к единой во всех модулях** — root-модуль использует JVM 11, `:music` и `:network` — JVM 8; смешивание байткода может вызвать скрытые проблемы совместимости.

---

## Баги

21. **Сделать `FindByKeywordResponse.nextPageToken` nullable** — YouTube API не включает это поле на последней странице; Gson кинет NPE при десериализации.

22. **Добавить аннотацию `@SerializedName("max_tokens")` в `DeepSeekRequest`** — без неё Gson сериализует поле как `maxTokens`, API игнорирует лимит и использует значение по умолчанию.

23. **Исправить `TimeUnit.MINUTES` → `TimeUnit.SECONDS` для `writeTimeout` в `RetrofitFactory`** — `WRITE_TIMEOUT = 5L` с `TimeUnit.MINUTES` даёт 5-минутный таймаут вместо 5 секунд, блокируя Retrofit-поток.

24. **Исправить логику сдвига буфера в `PorcupineReceiveHandler.processBuffer()`** — `monoBuffer.subList(0, overlap).clear()` удаляет только половину обработанного фрейма (`overlap = frameSize / 2`), что ведёт к бесконечному росту буфера при долгой записи.

25. **Устранить ложное определение фонового трека при активном TTS** — `GuildMusicManager.isBackgroundPlaying()` возвращает `true` во время TTS-воспроизведения, из-за чего `/background` при активном TTS останавливает несуществующий фоновый трек.

26. **Корректно обработать пустой ответ AI в `AiChatCommand`** — если `answer == ""`, бот воспроизводит тишину и отправляет пустое сообщение пользователю.

27. **Доработать парсинг списка треков в `SuggestPlaylistCommand.extractSongsFromResponse()`** — фильтр `it.contains("-")` даёт ложные срабатывания (нумерованные строки `"1- ..."`); заменить на регулярное выражение.

---

## Зависимости и сборка

28. **Актуализировать версии в `gradle.properties`** — файл содержит устаревшие версии (`JDA 5.1.1` вместо `6.4.1`, `lavaYoutubeVersion 1.15.0` вместо `1.18.0`); либо использовать как единый источник правды, либо удалить.

29. **Заменить `kotlinx-coroutines-core:1.8.0-RC` на стабильную версию** — используется release candidate; заменить на `1.8.1` или новее.

30. **Удалить неиспользуемую зависимость `jackson-module-kotlin` из `:network`** — весь сетевой слой использует Gson; Jackson нигде не применяется.

31. **Проверить и убрать неиспользуемую зависимость TarsosDSP** — ресемплинг в `PorcupineReceiveHandler` выполняется через `javax.sound.sampled.AudioSystem`; TarsosDSP, возможно, не используется.

---

## Документация

32. **Добавить KDoc ко всем `public`/`protected` методам и свойствам** — нарушение правила из `CLAUDE.md`; затронуты все command-классы, `BotAudioPlayer`, `CommandExecutor`, `CommandFabric`, `TrackScheduler`, все Network-классы.

---

## Незавершённые фичи

33. **Реализовать или удалить модели `fusion/`** — `CheckImageStatusResponse.kt` и `GetModelResponse.kt` присутствуют, но `FusionNetwork` и команды для них отсутствуют; код либо завершить, либо убрать.

34. **Завершить пайплайн голосового управления** — `PorcupineReceiveHandler` записывает команду в `recorded.wav`, но нет кода, который автоматически её обрабатывает; пользователь должен вручную вызвать `/background`.

35. **Добавить команду `/volume`** — громкость `player` (30) и `backgroundPlayer` (100) захардкожена; пользователь не может её изменить.

36. **Добавить режим повтора трека в `TrackScheduler`** — фоновый плеер поддерживает loop, основная очередь — нет.

37. **Параллелизовать загрузку треков в `SuggestPlaylistCommand`** — треки загружаются последовательно через `loadMusic()`; использовать `async`/`awaitAll` для ускорения.

38. **Добавить команду `/disconnect`** — бот умеет подключаться к голосовому каналу, но не умеет из него выходить.
