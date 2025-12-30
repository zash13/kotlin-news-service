from pydantic import BaseModel, Field
from datetime import datetime
from typing import Optional, List
from models.news import Category


# DTO for creating news
class CreateNewsDTO(BaseModel):
    title: str = Field(..., max_length=255)
    description: str
    category: Category
    source: str = Field(..., max_length=255)
    image_id: Optional[int] = None


# DTO for news title response
class NewsTitleDTO(BaseModel):
    id: int
    title: str


# DTO for news listing (with timestamps)
class NewsListItemDTO(BaseModel):
    id: int
    title: str
    category: Category
    timestamp: datetime
    source: str


# DTO for single news details
class NewsDetailDTO(BaseModel):
    id: int
    title: str
    description: str
    category: Category
    timestamp: datetime
    source: str
    created_at: datetime
    updated_at: datetime
    image_id: Optional[int] = None


# DTO for category-based requests
class CategoryRequestDTO(BaseModel):
    category: Category


# DTO for multiple categories request
class MultipleCategoriesRequestDTO(BaseModel):
    categories: List[Category]
    limit_per_category: Optional[int] = Field(10, ge=1, le=50)


# DTO for pagination
class PaginationDTO(BaseModel):
    limit: Optional[int] = Field(10, ge=1, le=100)
    offset: Optional[int] = Field(0, ge=0)
