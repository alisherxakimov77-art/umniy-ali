from contextlib import asynccontextmanager
import asyncio

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import get_settings
from app.api import chat, telegram, vision
from app.services.telegram_monitor import TelegramMonitor


settings = get_settings()
monitor: TelegramMonitor | None = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    global monitor
    monitor = TelegramMonitor(settings)
    # Запускаем мониторинг в фоне, если указаны Telegram API credentials
    if settings.telegram_api_id and settings.telegram_api_hash:
        asyncio.create_task(monitor.start())
    yield
    if monitor:
        await monitor.stop()


app = FastAPI(
    title="Умный Али API",
    description="Серверная часть голосового ассистента",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(chat.router, prefix="/api/v1/chat", tags=["chat"])
app.include_router(telegram.router, prefix="/api/v1/telegram", tags=["telegram"])
app.include_router(vision.router, prefix="/api/v1/vision", tags=["vision"])


@app.get("/health")
async def health():
    return {"status": "ok"}
