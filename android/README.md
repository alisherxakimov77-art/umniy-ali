# Android-приложение Умного Али

## Стек

- Kotlin
- Jetpack Compose
- Hilt (DI)
- Room (локальная БД)
- Retrofit + Kotlinx Serialization
- Android SpeechRecognizer
- Android TextToSpeech

## Запуск

1. Открыть папку `android` в Android Studio.
2. В `data/remote/RetrofitClient.kt` заменить `BASE_URL` на свой сервер.
3. Собрать и запустить.

## Разрешения

Приложение запрашивает:
- `RECORD_AUDIO` — голосовой ввод
- `CAMERA` — фото еды для подсчёта калорий
- `SEND_SMS`, `READ_SMS`, `CALL_PHONE` — этап 2
- `READ_CONTACTS` — поиск контактов
- `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM` — напоминания

## Функции

- Голосовой чат с LLM
- Подсчёт калорий по фото еды (камера/галерея)
- Настройки ассистента (имя, характер, язык, голос)

## Структура

- `data/` — локальная БД, сетевой слой, репозитории
- `domain/` — модели
- `ui/` — экраны, ViewModel, тема
- `service/` — STT, TTS, напоминания
