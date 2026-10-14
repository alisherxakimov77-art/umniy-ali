"""Telegram channel monitor: finds products (e.g. notebooks) and their prices.

SETUP (one time):
1. Get api_id and api_hash: https://my.telegram.org  (free, 2 minutes)
2. Put them into server/.env:
     TG_API_ID=123456
     TG_API_HASH=your_hash
     TG_PHONE=+7700123456
3. Add channels to CHANNELS list below
4. Run:  python -m app.services.telegram_monitor

NOTE: run once interactively to login (code from Telegram SMS),
after that the session file keeps you logged in.
"""
import asyncio
import os
import re

# from telethon import TelegramClient, events  # uncomment after: pip install telethon

CHANNELS = [
    "https://t.me/example_tech_channel",
]

PRICE_RE = re.compile(r"(\d[\d\s]{2,12})\s*(тг|тенге|₸|kzt|rub|руб)", re.IGNORECASE)


def extract_price(text: str):
    m = PRICE_RE.search(text)
    if not m:
        return None
    digits = re.sub(r"\D", "", m.group(1))
    try:
        return int(digits)
    except ValueError:
        return None


async def main():
    api_id = int(os.getenv("TG_API_ID", "0"))
    api_hash = os.getenv("TG_API_HASH", "")
    if not api_id or not api_hash:
        print("Set TG_API_ID and TG_API_HASH in server/.env")
        return
    # client = TelegramClient("umniy_ali_session", api_id, api_hash)
    # await client.start(os.getenv("TG_PHONE", ""))
    #
    # @client.on(events.NewMessage(chats=CHANNELS))
    # async def handler(event):
    #     text = event.message.message or ""
    #     price = extract_price(text)
    #     if price and ("ноутбук" in text.lower() or "laptop" in text.lower() or "noutbuk" in text.lower()):
    #         link = f"https://t.me/{event.chat.username}/{event.message.id}" if event.chat.username else ""
    #         print(f"FOUND price={price} | {text[:80]}... | {link}")
    #         # TODO: save to DB, compare with previous prices, notify user via bot
    #
    # print("Monitoring started...")
    # await client.run_until_disconnected()
    print("Telethon part is commented out. Uncomment and configure to start monitoring.")


if __name__ == "__main__":
    asyncio.run(main())
