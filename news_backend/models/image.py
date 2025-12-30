from pydantic import BaseModel
from datetime import datetime


class ImageBase(BaseModel):
    location: str
    filename: str
    alt_text: Optional[str] = None


class ImageCreate(ImageBase):
    pass


class ImageInDB(ImageBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True


class ImageResponse(ImageInDB):
    pass
