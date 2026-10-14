from typing import List, Dict, Optional

from pydantic import BaseModel


class ChatRequest(BaseModel):
    message: str
    user_name: str = ""
    assistant_name: str = "Umniy Ali"
    character: str = "business"
    language: str = "ru-RU"
    history: List[Dict[str, str]] = []


class ChatResponse(BaseModel):
    reply: str
    action: Optional[dict] = None
