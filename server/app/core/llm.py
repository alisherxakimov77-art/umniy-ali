import httpx

from app.config import settings
from app.core.system_prompt import build_prompt
from app.schemas import ChatRequest


async def ask_llm(req: ChatRequest) -> str:
    messages = [{"role": "system", "content": build_prompt(req)}]
    messages += req.history[-20:]
    messages.append({"role": "user", "content": req.message})

    async with httpx.AsyncClient(timeout=60) as client:
        r = await client.post(
            settings.llm_base_url.rstrip("/") + "/chat/completions",
            headers={"Authorization": f"Bearer {settings.llm_api_key}"},
            json={
                "model": settings.llm_model,
                "messages": messages,
                "temperature": 0.4,
            },
        )
        r.raise_for_status()
        data = r.json()
        return data["choices"][0]["message"]["content"].strip()
