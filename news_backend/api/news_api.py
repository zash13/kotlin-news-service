from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from sqlalchemy import desc, func
from typing import List, Optional
import logging
from datetime import datetime

from database.database import get_db
from database.models import News, Image, CategoryEnum
from models.news import Category, NewsCreate
from dto.news_dto import (
    CreateNewsDTO,
    NewsTitleDTO,
    NewsListItemDTO,
    NewsDetailDTO,
    CategoryRequestDTO,
    MultipleCategoriesRequestDTO,
    PaginationDTO,
)
from dto.response_dto import SuccessResponseDTO, ErrorResponseDTO

# Setup logging
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api/news", tags=["news"])


@router.post(
    "/",
    response_model=SuccessResponseDTO,
    status_code=status.HTTP_201_CREATED,
    summary="Create a new news article",
)
async def create_news(news_data: CreateNewsDTO, db: Session = Depends(get_db)):
    """
    Create a new news article.

    - **title**: News title (max 255 chars)
    - **description**: Full news description
    - **category**: News category (politics, technology, sports, etc.)
    - **source**: News source (max 255 chars)
    - **image_id**: Optional ID of associated image
    """
    try:
        logger.info(f"Creating new news article: {news_data.title}")

        # Validate image_id if provided
        if news_data.image_id is not None:
            image = db.query(Image).filter(Image.id == news_data.image_id).first()
            if not image:
                logger.warning(f"Image ID {news_data.image_id} not found")
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail=f"Image with ID {news_data.image_id} not found",
                )

        # Convert Pydantic model to SQLAlchemy model
        db_news = News(
            title=news_data.title,
            description=news_data.description,
            category=CategoryEnum[
                news_data.category.name
            ],  # Convert to SQLAlchemy Enum
            source=news_data.source,
            image_id=news_data.image_id,
            timestamp=datetime.utcnow(),
        )

        db.add(db_news)
        db.commit()
        db.refresh(db_news)

        logger.info(f"News article created successfully with ID: {db_news.id}")

        return SuccessResponseDTO(
            message="News article created successfully",
            data={
                "id": db_news.id,
                "title": db_news.title,
                "created_at": db_news.created_at,
            },
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error creating news article: {str(e)}")
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to create news article: {str(e)}",
        )


@router.get(
    "/by-category/{category}/titles",
    response_model=SuccessResponseDTO,
    summary="Get news titles by category",
)
async def get_news_titles_by_category(
    category: Category,
    limit: int = Query(10, ge=1, le=50, description="Number of results to return"),
    db: Session = Depends(get_db),
):
    """
    Get news titles by category (first 10 newest by default).

    - **category**: News category to filter by
    - **limit**: Number of results (1-50, default 10)
    """
    try:
        logger.info(f"Fetching news titles for category: {category}")

        # Convert to SQLAlchemy enum
        category_enum = CategoryEnum[category.name]

        news_items = (
            db.query(News)
            .filter(News.category == category_enum)
            .order_by(desc(News.timestamp))
            .limit(limit)
            .all()
        )

        titles = [NewsTitleDTO(id=item.id, title=item.title) for item in news_items]

        logger.info(f"Found {len(titles)} news titles for category: {category}")

        return SuccessResponseDTO(
            message=f"Found {len(titles)} news titles", data=titles
        )

    except Exception as e:
        logger.error(f"Error fetching news titles by category: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch news titles: {str(e)}",
        )


@router.post(
    "/by-multiple-categories/titles",
    response_model=SuccessResponseDTO,
    summary="Get news titles by multiple categories",
)
async def get_news_titles_by_multiple_categories(
    request: MultipleCategoriesRequestDTO, db: Session = Depends(get_db)
):
    """
    Get news titles from multiple categories.

    - **categories**: List of categories to include
    - **limit_per_category**: Number of items per category (1-50, default 10)

    Returns newest items across all specified categories, sorted by timestamp.
    """
    try:
        logger.info(f"Fetching news for categories: {request.categories}")

        all_news = []

        for category in request.categories:
            category_enum = CategoryEnum[category.name]

            news_items = (
                db.query(News)
                .filter(News.category == category_enum)
                .order_by(desc(News.timestamp))
                .limit(request.limit_per_category)
                .all()
            )

            # Convert to DTOs
            for item in news_items:
                all_news.append(
                    NewsListItemDTO(
                        id=item.id,
                        title=item.title,
                        category=category,
                        timestamp=item.timestamp,
                        source=item.source,
                    )
                )

        # Sort all news by timestamp (newest first)
        all_news.sort(key=lambda x: x.timestamp, reverse=True)

        logger.info(
            f"Found {len(all_news)} news items across {len(request.categories)} categories"
        )

        return SuccessResponseDTO(
            message=f"Found {len(all_news)} news items", data=all_news
        )

    except Exception as e:
        logger.error(f"Error fetching news by multiple categories: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch news: {str(e)}",
        )


@router.get(
    "/newest/titles",
    response_model=SuccessResponseDTO,
    summary="Get newest news titles",
)
async def get_newest_news_titles(
    limit: int = Query(10, ge=1, le=50, description="Number of results to return"),
    db: Session = Depends(get_db),
):
    """
    Get newest news titles across all categories.

    - **limit**: Number of results (1-50, default 10)
    """
    try:
        logger.info(f"Fetching {limit} newest news titles")

        news_items = db.query(News).order_by(desc(News.timestamp)).limit(limit).all()

        titles = [NewsTitleDTO(id=item.id, title=item.title) for item in news_items]

        logger.info(f"Found {len(titles)} newest news titles")

        return SuccessResponseDTO(
            message=f"Found {len(titles)} newest news titles", data=titles
        )

    except Exception as e:
        logger.error(f"Error fetching newest news titles: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch newest news: {str(e)}",
        )


@router.get(
    "/{news_id}",
    response_model=SuccessResponseDTO,
    summary="Get full news article by ID",
)
async def get_news_by_id(news_id: int, db: Session = Depends(get_db)):
    """
    Get full news article details by ID.

    - **news_id**: ID of the news article
    """
    try:
        logger.info(f"Fetching news article with ID: {news_id}")

        news_item = db.query(News).filter(News.id == news_id).first()

        if not news_item:
            logger.warning(f"News article with ID {news_id} not found")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"News article with ID {news_id} not found",
            )

        # Convert to DTO
        news_detail = NewsDetailDTO(
            id=news_item.id,
            title=news_item.title,
            description=news_item.description,
            category=Category(news_item.category.value),
            timestamp=news_item.timestamp,
            source=news_item.source,
            created_at=news_item.created_at,
            updated_at=news_item.updated_at,
            image_id=news_item.image_id,
        )

        logger.info(f"Successfully fetched news article: {news_item.title}")

        return SuccessResponseDTO(
            message="News article retrieved successfully", data=news_detail
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error fetching news article {news_id}: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch news article: {str(e)}",
        )


@router.get(
    "/newest/full",
    response_model=SuccessResponseDTO,
    summary="Get newest full news articles",
)
async def get_newest_full_news(
    limit: int = Query(10, ge=1, le=50, description="Number of results to return"),
    db: Session = Depends(get_db),
):
    """
    Get newest full news articles across all categories.

    - **limit**: Number of results (1-50, default 10)
    """
    try:
        logger.info(f"Fetching {limit} newest full news articles")

        news_items = db.query(News).order_by(desc(News.timestamp)).limit(limit).all()

        news_list = []
        for item in news_items:
            news_list.append(
                NewsDetailDTO(
                    id=item.id,
                    title=item.title,
                    description=item.description,
                    category=Category(item.category.value),
                    timestamp=item.timestamp,
                    source=item.source,
                    created_at=item.created_at,
                    updated_at=item.updated_at,
                    image_id=item.image_id,
                )
            )

        logger.info(f"Found {len(news_list)} newest full news articles")

        return SuccessResponseDTO(
            message=f"Found {len(news_list)} newest news articles", data=news_list
        )

    except Exception as e:
        logger.error(f"Error fetching newest full news articles: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch news articles: {str(e)}",
        )


@router.get(
    "/by-category/{category}/full",
    response_model=SuccessResponseDTO,
    summary="Get full news articles by category",
)
async def get_full_news_by_category(
    category: Category,
    limit: int = Query(10, ge=1, le=50, description="Number of results to return"),
    db: Session = Depends(get_db),
):
    """
    Get full news articles by category (newest first).

    - **category**: News category to filter by
    - **limit**: Number of results (1-50, default 10)
    """
    try:
        logger.info(f"Fetching full news articles for category: {category}")

        category_enum = CategoryEnum[category.name]

        news_items = (
            db.query(News)
            .filter(News.category == category_enum)
            .order_by(desc(News.timestamp))
            .limit(limit)
            .all()
        )

        news_list = []
        for item in news_items:
            news_list.append(
                NewsDetailDTO(
                    id=item.id,
                    title=item.title,
                    description=item.description,
                    category=Category(item.category.value),
                    timestamp=item.timestamp,
                    source=item.source,
                    created_at=item.created_at,
                    updated_at=item.updated_at,
                    image_id=item.image_id,
                )
            )

        logger.info(
            f"Found {len(news_list)} full news articles for category: {category}"
        )

        return SuccessResponseDTO(
            message=f"Found {len(news_list)} news articles", data=news_list
        )

    except Exception as e:
        logger.error(f"Error fetching full news by category: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch news articles: {str(e)}",
        )
