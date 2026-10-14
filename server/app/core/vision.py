import base64
import httpx
import json
from app.config import Settings


class VisionClient:
    def __init__(self, settings: Settings):
        self.settings = settings
        self.client = httpx.AsyncClient(timeout=60.0)

    def _headers(self):
        return {
            "Authorization": f"Bearer {self.settings.llm_api_key}",
            "Content-Type": "application/json",
        }

    async def analyze_food(self, image_bytes: bytes, mime_type: str = "image/jpeg") -> dict:
        """
        Анализирует фото еды и возвращает оценку калорий и БЖУ.
        Работает с vision-совместимыми LLM (OpenAI GPT-4o и аналоги).
        """
        if not self.settings.llm_api_key:
            return self._mock_response()

        b64 = base64.b64encode(image_bytes).decode("utf-8")
        data_url = f"data:{mime_type};base64,{b64}"

        prompt = (
            "Ты — диетолог. Посмотри на фото еды и оцени примерную калорийность и БЖУ. "
            "Ответь строго в JSON формате без пояснений:\n"
            '{"dish_name": "название блюда", "calories": число, "protein_g": число, "fat_g": число, "carbs_g": число, "comment": "короткий комментарий"}'
        )

        body = {
            "model": self.settings.llm_model,
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {"type": "text", "text": prompt},
                        {"type": "image_url", "image_url": {"url": data_url}},
                    ],
                }
            ],
            "temperature": 0.3,
        }

        try:
            resp = await self.client.post(
                self.settings.llm_api_url,
                headers=self._headers(),
                json=body,
            )
            resp.raise_for_status()
            content = resp.json()["choices"][0]["message"]["content"]
            parsed = json.loads(content.strip().strip("`").replace("json\n", ""))
            return parsed
        except Exception as e:
            return {
                "dish_name": "Не удалось распознать",
                "calories": 0,
                "protein_g": 0,
                "fat_g": 0,
                "carbs_g": 0,
                "comment": f"Ошибка анализа: {e}",
            }

    def _mock_response(self) -> dict:
        return {
            "dish_name": "Блюдо (заглушка)",
            "calories": 420,
            "protein_g": 25,
            "fat_g": 18,
            "carbs_g": 42,
            "comment": "Режим заглушки: API ключ не настроен. Добавь LLM_API_KEY в .env",
        }
