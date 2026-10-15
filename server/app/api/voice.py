"""Voice: speech-to-text via Groq Whisper."""
import httpx
from fastapi import APIRouter, UploadFile, File

from app.config import settings

router = APIRouter()

WHISPER_URL = "https://api.groq.com/openai/v1/audio/transcriptions"


@router.post("/stt")
async def stt(file: UploadFile = File(...)):
    audio = await file.read()
    if not audio:
        return {"text": ""}
    try:
        async with httpx.AsyncClient(timeout=180) as client:
            r = await client.post(
                WHISPER_URL,
                headers={"Authorization": f"Bearer {settings.llm_api_key}"},
                files={"file": ("voice.m4a", audio, "audio/mp4")},
                data={"model": "whisper-large-v3-turbo"},
            )
            if r.status_code != 200:
                print("STT ERROR:", r.status_code, r.text[:300])
                return {"text": ""}
            return {"text": r.json().get("text", "")}
    except Exception as e:
        print("STT EXCEPTION:", repr(e))
        return {"text": ""}
