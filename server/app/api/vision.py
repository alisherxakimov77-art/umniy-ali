from fastapi import APIRouter, File, UploadFile, HTTPException
from app.core.vision import VisionClient
from app.config import get_settings

router = APIRouter()
vision_client = VisionClient(get_settings())


@router.post("/food-calories")
async def food_calories(file: UploadFile = File(...)):
    """
    Принимает фото еды, возвращает оценку калорий и БЖУ.
    """
    if not file.content_type or not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="Only image files allowed")

    image_bytes = await file.read()
    if len(image_bytes) > 10 * 1024 * 1024:
        raise HTTPException(status_code=413, detail="Image too large (max 10 MB)")

    result = await vision_client.analyze_food(image_bytes, file.content_type)
    return result
