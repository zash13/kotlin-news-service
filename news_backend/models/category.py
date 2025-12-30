from pydantic import BaseModel
from datetime import datetime


class CategoryBase(BaseModel):
    name: str


class CategoryCreate(CategoryBase):
    pass


class CategoryInDB(CategoryBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True


class CategoryResponse(CategoryInDB):
    pass
