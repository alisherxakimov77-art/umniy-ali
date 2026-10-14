from pydantic import BaseModel, Field
from typing import Literal, Optional
from datetime import datetime


class SettingsDto(BaseModel):
    assistant_name: str = "Умный Али"
    user_name: str = ""
    character: Literal["business", "jarvis", "friendly"] = "jarvis"
    language: str = "ru"
    tts_voice: str = "ru-RU"


class ChatMessage(BaseModel):
    role: Literal["system", "user", "assistant"]
    content: str


class ChatRequest(BaseModel):
    message: str
    settings: SettingsDto
    history: list[ChatMessage] = Field(default_factory=list)


class Action(BaseModel):
    action: str
    params: dict = Field(default_factory=dict)


class ChatResponse(BaseModel):
    type: Literal["text", "action"] = "text"
    text: Optional[str] = None
    action: Optional[Action] = None


class TelegramSearchRequest(BaseModel):
    query: str
    max_price: Optional[float] = None
    city: Optional[str] = None
    condition: Optional[Literal["new", "used"]] = None
    limit: int = 5


class ParsedAd(BaseModel):
    product: str
    model: Optional[str] = None
    price: Optional[float] = None
    currency: Optional[str] = None
    condition: Optional[str] = None
    city: Optional[str] = None
    channel: str
    message_link: str
    posted_at: Optional[datetime] = None


class FoodCaloriesResponse(BaseModel):
    dish_name: str
    calories: int
    protein_g: float
    fat_g: float
    carbs_g: float
    comment: str
