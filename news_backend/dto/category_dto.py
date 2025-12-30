from pydantic import BaseModel, Field


class CategoryListDTO(BaseModel):
    id: int = Field(..., serialization_alias="category_id")
    name: str = Field(..., serialization_alias="category_name")

    class Config:
        from_attributes = True
        populate_by_name = True
