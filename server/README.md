# Сервер Умного Али

## Установка

```bash
python -m venv .venv
source .venv/bin/activate  # Windows: .venv\Scripts\activate
pip install -r requirements.txt
```

## Настройка

```bash
cp .env.example .env
```

Заполнить в `.env`:
- `LLM_API_KEY` — ключ от OpenAI-совместимого API
- `LLM_API_URL` — URL API
- `TELEGRAM_API_ID` и `TELEGRAM_API_HASH` — с https://my.telegram.org/apps
- `TELEGRAM_BOT_TOKEN` — для отправки уведомлений

## Запуск

```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

## Эндпоинты

- `POST /api/v1/chat/` — диалог с ассистентом
- `POST /api/v1/chat/stream` — потоковый ответ
- `POST /api/v1/telegram/search` — поиск по кэшированным объявлениям
- `GET /api/v1/telegram/channels` — список каналов
- `POST /api/v1/telegram/channels/{channel}` — добавить канал
- `POST /api/v1/vision/food-calories` — анализ фото еды и калорийность
- `GET /health` — проверка работы

## Архитектура

- `app/api/` — роутеры FastAPI
- `app/core/` — LLM-клиент и системный промпт
- `app/services/` — Telegram Monitor (Telethon)
- `app/models/` — Pydantic-схемы
