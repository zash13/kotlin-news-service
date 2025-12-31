from pydantic import BaseModel, Field
from datetime import datetime
from typing import Optional, List


class CreateNewsDTO(BaseModel):
    title: str = Field(..., max_length=255)
    description: str
    category_ids: List[int] = Field(..., min_length=1)
    source: str = Field(..., max_length=255)
    image_id: Optional[int] = None


class NewsTitleDTO(BaseModel):
    id: int
    title: str


class CategoryInfoDTO(BaseModel):
    id: int = Field(..., serialization_alias="category_id")
    name: str = Field(..., serialization_alias="category_name")

    class Config:
        from_attributes = True
        populate_by_name = True


class NewsListItemDTO(BaseModel):
    id: int
    title: str
    categories: List[CategoryInfoDTO]
    timestamp: datetime
    source: str


class NewsDetailDTO(BaseModel):
    id: int
    title: str
    description: str
    categories: List[CategoryInfoDTO]
    timestamp: datetime
    source: str
    created_at: datetime
    image_id: Optional[int] = None


class CategoryRequestDTO(BaseModel):
    category_id: int


class MultipleCategoriesRequestDTO(BaseModel):
    category_ids: List[int] = Field(..., min_length=1)
    limit_per_category: Optional[int] = Field(10, ge=1, le=50)


class PaginationDTO(BaseModel):
    limit: Optional[int] = Field(10, ge=1, le=100)
    offset: Optional[int] = Field(0, ge=0)
