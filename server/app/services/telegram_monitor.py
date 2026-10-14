import asyncio
import json
from datetime import datetime
from typing import Optional

from telethon import TelegramClient, events
from telethon.tl.types import Channel

from app.config import Settings
from app.core.llm import LLMClient
from app.models.schemas import ParsedAd


class TelegramMonitor:
    def __init__(self, settings: Settings):
        self.settings = settings
        self.client: Optional[TelegramClient] = None
        self.channels: list[str] = [
            "@laptop_tashkent",
            "@notebooks_uz",
            # добавлять через API
        ]
        self.ads: list[ParsedAd] = []
        self.llm = LLMClient(settings)
        self._running = False

    async def start(self):
        if self._running:
            return
        self._running = True
        self.client = TelegramClient(
            self.settings.telegram_session_name,
            self.settings.telegram_api_id,
            self.settings.telegram_api_hash,
        )
        await self.client.start()
        self._register_handlers()
        print("Telegram monitor started")

    async def stop(self):
        self._running = False
        if self.client:
            await self.client.disconnect()

    def _register_handlers(self):
        @self.client.on(events.NewMessage(chats=self.channels))
        async def handler(event):
            await self._process_message(event)

    async def _process_message(self, event):
        try:
            text = event.message.text or ""
            if not text or "ноутбук" not in text.lower():
                return

            parsed = await self.llm.parse_telegram_ad(text)
            if not parsed or not parsed.get("price"):
                return

            chat = await event.get_chat()
            channel_name = chat.username if isinstance(chat, Channel) and chat.username else str(chat.id)
            link = f"https://t.me/{channel_name}/{event.message.id}" if channel_name else ""

            ad = ParsedAd(
                product=parsed.get("product", ""),
                model=parsed.get("model"),
                price=float(parsed["price"]) if parsed.get("price") else None,
                currency=parsed.get("currency"),
                condition=parsed.get("condition"),
                city=parsed.get("city"),
                channel=channel_name,
                message_link=link,
                posted_at=event.message.date,
            )
            self.ads.append(ad)
            # TODO: push уведомление через FCM / внутренний API
            print(f"[TG] Parsed ad: {ad.model} {ad.price}")
        except Exception as e:
            print(f"[TG] Error processing message: {e}")

    def search_cached(
        self,
        query: str,
        max_price: Optional[float] = None,
        city: Optional[str] = None,
        condition: Optional[str] = None,
    ) -> list[ParsedAd]:
        results = []
        q = query.lower()
        for ad in sorted(self.ads, key=lambda x: (x.price or float("inf"))):
            text = f"{ad.product} {ad.model or ''}".lower()
            if q not in text:
                continue
            if max_price is not None and (ad.price is None or ad.price > max_price):
                continue
            if city and (ad.city or "").lower() != city.lower():
                continue
            if condition and (ad.condition or "").lower() != condition.lower():
                continue
            results.append(ad)
        return results

    async def add_channel(self, channel: str):
        channel = channel if channel.startswith("@") or channel.startswith("-") else f"@{channel}"
        if channel not in self.channels:
            self.channels.append(channel)
            if self.client:
                self._register_handlers()
