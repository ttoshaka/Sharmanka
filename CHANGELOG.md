# Changelog

## 2026-04-18

### Безопасность
- **#1** Вынесены API-ключи (`DISCORD_TOKEN`, `YOUTUBE_KEY`, `DEEPSEEK_KEY`, `PORCUPINE_KEY`) из `Main.kt` в переменные окружения через `System.getenv()`. Добавлена функция `requireEnv()` с понятным исключением при отсутствии переменной. Создан файл `.env.example` с шаблоном переменных.
- **#2** Уровень HTTP-логирования в `RetrofitFactory` вынесен в переменную окружения `HTTP_LOG_LEVEL`. По умолчанию — `NONE` (секреты не утекают). Допустимые значения: `NONE`, `BASIC`, `HEADERS`, `BODY`.
