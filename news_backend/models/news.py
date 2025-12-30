from enum import Enum
from datetime import datetime
from pydantic import BaseModel, HttpUrl
from typing import Optional, List


class Category(str, Enum):
    POLITICS = "politics"
    TECHNOLOGY = "technology"
    SPORTS = "sports"
    ENTERTAINMENT = "entertainment"
    BUSINESS = "business"
    HEALTH = "health"
    SCIENCE = "science"
    WORLD = "world"


class NewsBase(BaseModel):
    title: str
    description: str
    category: Category
    source: str
    timestamp: Optional[datetime] = None


class NewsCreate(NewsBase):
    image_id: Optional[int] = None


class NewsUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    category: Optional[Category] = None
    source: Optional[str] = None
    image_id: Optional[int] = None


class NewsInDB(NewsBase):
    id: int
    image_id: Optional[int] = None
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True


class NewsResponse(NewsInDB):
    pass
