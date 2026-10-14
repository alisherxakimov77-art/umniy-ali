import json
import httpx
from app.config import Settings
from app.models.schemas import ChatMessage, Action


class LLMClient:
    def __init__(self, settings: Settings):
        self.settings = settings
        self.client = httpx.AsyncClient(timeout=60.0)

    def _headers(self):
        return {
            "Authorization": f"Bearer {self.settings.llm_api_key}",
            "Content-Type": "application/json",
        }

    def _body(self, system_prompt: str, history: list[ChatMessage], message: str, stream: bool = False):
        messages = [{"role": "system", "content": system_prompt}]
        for m in history[-20:]:
            messages.append({"role": m.role, "content": m.content})
        messages.append({"role": "user", "content": message})
        return {
            "model": self.settings.llm_model,
            "messages": messages,
            "stream": stream,
            "temperature": 0.7,
        }

    async def chat(self, system_prompt: str, history: list[ChatMessage], message: str) -> str:
        body = self._body(system_prompt, history, message, stream=False)
        resp = await self.client.post(self.settings.llm_api_url, headers=self._headers(), json=body)
        resp.raise_for_status()
        data = resp.json()
        return data["choices"][0]["message"]["content"]

    async def chat_stream(self, system_prompt: str, history: list[ChatMessage], message: str):
        body = self._body(system_prompt, history, message, stream=True)
        async with self.client.stream("POST", self.settings.llm_api_url, headers=self._headers(), json=body) as resp:
            resp.raise_for_status()
            async for line in resp.aiter_lines():
                if line.startswith("data: "):
                    chunk = line[6:]
                    if chunk == "[DONE]":
                        break
                    try:
                        obj = json.loads(chunk)
                        delta = obj["choices"][0]["delta"].get("content", "")
                        if delta:
                            yield delta
                    except Exception:
                        continue

    def try_parse_action(self, text: str) -> Action | None:
        text = text.strip()
        if text.startswith("{") and text.endswith("}"):
            try:
                data = json.loads(text)
                if "action" in data:
                    return Action(action=data["action"], params=data.get("params", {}))
            except json.JSONDecodeError:
                return None
        return None

    async def parse_telegram_ad(self, text: str) -> dict | None:
        prompt = (
            "Извлеки из сообщения Telegram о продаже ноутбука следующие поля в JSON: "
            "product, model, price (число), currency, condition (new/used), city. "
            "Если поля нет — null. Ответ только JSON, без пояснений.\n\n"
            f"Сообщение:\n{text}"
        )
        body = {
            "model": self.settings.llm_small_model,
            "messages": [{"role": "user", "content": prompt}],
            "temperature": 0.0,
        }
        resp = await self.client.post(self.settings.llm_api_url, headers=self._headers(), json=body)
        resp.raise_for_status()
        content = resp.json()["choices"][0]["message"]["content"]
        try:
            return json.loads(content.strip().strip("`").replace("json\n", ""))
        except Exception:
            return None
