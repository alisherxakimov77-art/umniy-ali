from fastapi import APIRouter, HTTPException
from fastapi.responses import StreamingResponse
import json
import asyncio

from app.models.schemas import ChatRequest, ChatResponse
from app.core.llm import LLMClient
from app.core.system_prompt import build_system_prompt
from app.config import get_settings

router = APIRouter()
llm_client = LLMClient(get_settings())


@router.post("/", response_model=ChatResponse)
async def chat(request: ChatRequest):
    try:
        system_prompt = build_system_prompt(request.settings)
        response_text = await llm_client.chat(system_prompt, request.history, request.message)

        # Пытаемся распарсить JSON-команду
        action = llm_client.try_parse_action(response_text)
        if action:
            return ChatResponse(type="action", action=action)

        return ChatResponse(type="text", text=response_text)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/stream")
async def chat_stream(request: ChatRequest):
    async def event_generator():
        system_prompt = build_system_prompt(request.settings)
        async for chunk in llm_client.chat_stream(system_prompt, request.history, request.message):
            yield f"data: {json.dumps({'chunk': chunk}, ensure_ascii=False)}\n\n"
        yield "data: [DONE]\n\n"

    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
    )
