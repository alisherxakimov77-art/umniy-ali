from fastapi import APIRouter, HTTPException, Depends
from typing import Optional

from app.models.schemas import TelegramSearchRequest, ParsedAd
from app.services.telegram_monitor import TelegramMonitor
from app.config import get_settings

router = APIRouter()


def get_monitor() -> TelegramMonitor:
    # В реальном приложении лучше использовать dependency injection
    from app.main import monitor
    if monitor is None:
        raise HTTPException(status_code=503, detail="Telegram monitor not initialized")
    return monitor


@router.post("/search", response_model=list[ParsedAd])
async def search_telegram(request: TelegramSearchRequest, monitor: TelegramMonitor = Depends(get_monitor)):
    results = monitor.search_cached(request.query, request.max_price, request.city, request.condition)
    return results[: request.limit]


@router.get("/channels")
async def list_channels(monitor: TelegramMonitor = Depends(get_monitor)):
    return {"channels": monitor.channels}


@router.post("/channels/{channel}")
async def add_channel(channel: str, monitor: TelegramMonitor = Depends(get_monitor)):
    await monitor.add_channel(channel)
    return {"status": "added", "channel": channel}
