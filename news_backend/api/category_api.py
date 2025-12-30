from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List
import logging

from database.database import get_db
from database.models import Category
from dto.category_dto import CategoryListDTO
from dto.response_dto import SuccessResponseDTO

logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api/categories", tags=["categories"])


@router.get(
    "/",
    response_model=SuccessResponseDTO,
    summary="Get all categories",
)
async def get_all_categories(db: Session = Depends(get_db)):
    """
    Get all categories.

    Returns a list of all categories with category_id and category_name.
    """
    try:
        logger.info("Fetching all categories")

        categories = db.query(Category).order_by(Category.id).all()

        categories_list = [
            CategoryListDTO.model_validate(category) for category in categories
        ]

        logger.info(f"Found {len(categories_list)} categories")

        return SuccessResponseDTO(
            message=f"Found {len(categories_list)} categories", data=categories_list
        )

    except Exception as e:
        logger.error(f"Error fetching categories: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch categories: {str(e)}",
        )
