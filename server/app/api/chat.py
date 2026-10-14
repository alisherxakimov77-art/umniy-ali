from fastapi import APIRouter, HTTPException

from app.config import settings
from app.core.llm import ask_llm
from app.schemas import ChatRequest, ChatResponse

router = APIRouter()


@router.get("/health")
def health():
    return {"status": "ok", "assistant": "Umniy Ali"}


@router.post("/chat", response_model=ChatResponse)
async def chat(req: ChatRequest):
    if not settings.llm_api_key:
        raise HTTPException(
            status_code=500,
            detail="LLM_API_KEY is not set. Copy server/.env.example to server/.env and add your key.",
        )
    try:
        reply = await ask_llm(req)
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"LLM error: {e}")
    return ChatResponse(reply=reply, action=None)
