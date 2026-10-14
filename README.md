# Умный Али

Мобильный голосовой AI-ассистент для Android.

## Структура проекта

```
UMNIY ALI/
├── server/          # Серверная часть: FastAPI + Telethon + LLM
└── android/         # Android-приложение: Kotlin + Jetpack Compose
```

## MVP (этап 1)

- [x] Голосовой ввод/вывод (ru, uz)
- [x] Диалог через LLM API
- [x] Изменяемое имя ассистента и характер
- [x] Мониторинг Telegram-каналов и парсинг ноутбуков
- [x] Локальные настройки и история (Room)
- [x] Подсчёт калорий по фото еды (vision LLM)

## Быстрый старт

### Сервер (Windows)

Дважды кликни:
```
server/start_server.bat
```

Или вручную:
```bash
cd server
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
copy .env.example .env
# Отредактируй .env: добавь LLM_API_KEY, TELEGRAM_API_ID, TELEGRAM_API_HASH
uvicorn app.main:app --reload
```

### Android

### Перед сборкой Android (обязательно)

1. Скопируй `android/local.properties.example` → `android/local.properties`.
2. В `local.properties` укажи IP своего сервера, например:
   ```properties
   SERVER_URL=http://192.168.1.100:8000/
   ```
   Для эмулятора оставь `http://10.0.2.2:8000/`.

#### Способ 1: готовый APK с GitHub Actions (самое простое)

1. Загрузи проект на GitHub.
2. Перейди во вкладку **Actions** → выбери workflow **Build Android APK**.
3. Нажми **Run workflow**.
4. Через 5–10 минут во вкладке **Actions** появится артефакт `umniy-ali-debug-apk`.
5. Скачай ZIP, разархивируй, файл `app-debug.apk` закинь на телефон и установи.

#### Способ 2: собрать в Android Studio

1. Открыть папку `android` в Android Studio.
2. Убедиться, что создан `local.properties` с `SERVER_URL`.
3. Запустить сборку (`Build → Build APK(s)`).
4. APK будет в `android/app/build/outputs/apk/debug/app-debug.apk`.

## API ключи

Все ключи хранятся на сервере (`.env`). В приложении только URL сервера.

## Следующие шаги

- [ ] Экран списка найденных ноутбуков
- [ ] Push-уведомления о новых находках
- [ ] Офлайн-напоминания
- [ ] SMS/звонки
- [ ] Кодовое слово активации
