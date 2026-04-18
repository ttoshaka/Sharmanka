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
