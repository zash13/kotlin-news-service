from datetime import datetime
from pydantic import BaseModel
from typing import Optional, List


class CategoryResponse(BaseModel):
    id: int
    name: str

    class Config:
        from_attributes = True


class NewsBase(BaseModel):
    title: str
    description: str
    source: str
    timestamp: Optional[datetime] = None


class NewsCreate(NewsBase):
    category_ids: List[int]
    image_id: Optional[int] = None


class NewsUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    category_ids: Optional[List[int]] = None
    source: Optional[str] = None
    image_id: Optional[int] = None


class NewsInDB(NewsBase):
    id: int
    image_id: Optional[int] = None
    created_at: datetime
    updated_at: datetime
    categories: List[CategoryResponse]

    class Config:
        from_attributes = True


class NewsResponse(NewsInDB):
    pass
