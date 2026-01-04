from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from sqlalchemy import desc, func
from typing import List, Optional
import logging
from datetime import datetime

from database.database import get_db
from database.models import News, Image, Category
from dto.news_dto import (
    CreateNewsDTO,
    NewsTitleDTO,
    NewsListItemDTO,
    NewsDetailDTO,
    MultipleCategoriesRequestDTO,
    PaginationDTO,
    CategoryInfoDTO,
)
from dto.response_dto import SuccessResponseDTO, ErrorResponseDTO

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
    try:
        logger.info(f"Creating new news article: {news_data.title}")

        if news_data.image_id is not None:
            image = db.query(Image).filter(Image.id == news_data.image_id).first()
            if not image:
                logger.warning(f"Image ID {news_data.image_id} not found")
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail=f"Image with ID {news_data.image_id} not found",
                )

        categories = (
            db.query(Category).filter(Category.id.in_(news_data.category_ids)).all()
        )
        if len(categories) != len(news_data.category_ids):
            found_ids = {c.id for c in categories}
            missing_ids = set(news_data.category_ids) - found_ids
            logger.warning(f"Category IDs not found: {missing_ids}")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Categories with IDs {missing_ids} not found",
            )

        db_news = News(
            title=news_data.title,
            description=news_data.description,
            short_description=news_data.short_description,
            source=news_data.source,
            image_id=news_data.image_id,
            timestamp=datetime.utcnow(),
            categories=categories,
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
    "/by-category/{category_id}/titles",
    response_model=SuccessResponseDTO,
    summary="Get news titles by category",
)
async def get_news_titles_by_category(
    category_id: int,
    limit: int = Query(10, ge=1, le=50, description="Number of results to return"),
    db: Session = Depends(get_db),
):
    try:
        logger.info(f"Fetching news titles for category ID: {category_id}")

        category = db.query(Category).filter(Category.id == category_id).first()
        if not category:
            logger.warning(f"Category ID {category_id} not found")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Category with ID {category_id} not found",
            )

        news_items = (
            db.query(News)
            .join(News.categories)
            .filter(Category.id == category_id)
            .order_by(desc(News.timestamp))
            .limit(limit)
            .all()
        )

        titles = [
            NewsTitleDTO(
                id=item.id,
                title=item.title,
                short_description=item.short_description,
                image_id=item.image_id,
            )
            for item in news_items
        ]

        logger.info(f"Found {len(titles)} news titles for category ID: {category_id}")

        return SuccessResponseDTO(
            message=f"Found {len(titles)} news titles", data=titles
        )

    except HTTPException:
        raise
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
    try:
        logger.info(f"Fetching news for category IDs: {request.category_ids}")

        categories = (
            db.query(Category).filter(Category.id.in_(request.category_ids)).all()
        )
        if len(categories) != len(request.category_ids):
            found_ids = {c.id for c in categories}
            missing_ids = set(request.category_ids) - found_ids
            logger.warning(f"Category IDs not found: {missing_ids}")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Categories with IDs {missing_ids} not found",
            )

        all_news = []

        for category in categories:
            news_items = (
                db.query(News)
                .join(News.categories)
                .filter(Category.id == category.id)
                .order_by(desc(News.timestamp))
                .limit(request.limit_per_category)
                .all()
            )

            for item in news_items:
                categories_info = [
                    CategoryInfoDTO.model_validate(c) for c in item.categories
                ]
                all_news.append(
                    NewsListItemDTO(
                        id=item.id,
                        title=item.title,
                        short_description=item.short_description,
                        categories=categories_info,
                        timestamp=item.timestamp,
                        source=item.source,
                    )
                )

        all_news.sort(key=lambda x: x.timestamp, reverse=True)

        logger.info(
            f"Found {len(all_news)} news items across {len(categories)} categories"
        )

        return SuccessResponseDTO(
            message=f"Found {len(all_news)} news items", data=all_news
        )

    except HTTPException:
        raise
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
    try:
        logger.info(f"Fetching {limit} newest news titles")

        news_items = db.query(News).order_by(desc(News.timestamp)).limit(limit).all()

        titles = [
            NewsTitleDTO(
                id=item.id,
                title=item.title,
                short_description=item.short_description,
                image_id=item.image_id,
            )
            for item in news_items
        ]

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
    "/search",
    response_model=SuccessResponseDTO,
    summary="Search news articles by title (fuzzy search)",
)
async def search_news(
    q: str = Query(..., min_length=1, description="Search query string"),
    limit: int = Query(10, ge=1, le=50, description="Number of results to return"),
    db: Session = Depends(get_db),
):
    try:
        logger.info(f"Searching news with query: {q}")

        news_items = (
            db.query(News)
            .filter(News.title.ilike(f"%{q}%"))
            .order_by(desc(News.timestamp))
            .limit(limit)
            .all()
        )

        titles = [
            NewsTitleDTO(
                id=item.id,
                title=item.title,
                short_description=item.short_description,
                image_id=item.image_id,
            )
            for item in news_items
        ]

        logger.info(f"Found {len(titles)} news items matching query: {q}")

        return SuccessResponseDTO(
            message=f"Found {len(titles)} news items matching '{q}'", data=titles
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error searching news: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to search news: {str(e)}",
        )


@router.get(
    "/{news_id}",
    response_model=SuccessResponseDTO,
    summary="Get full news article by ID",
)
async def get_news_by_id(news_id: int, db: Session = Depends(get_db)):
    try:
        logger.info(f"Fetching news article with ID: {news_id}")

        news_item = db.query(News).filter(News.id == news_id).first()

        if not news_item:
            logger.warning(f"News article with ID {news_id} not found")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"News article with ID {news_id} not found",
            )

        categories_info = [
            CategoryInfoDTO.model_validate(c) for c in news_item.categories
        ]

        image_location = None
        if news_item.image:
            image_location = news_item.image.location

        news_detail = NewsDetailDTO(
            id=news_item.id,
            title=news_item.title,
            description=news_item.description,
            categories=categories_info,
            timestamp=news_item.timestamp,
            source=news_item.source,
            created_at=news_item.created_at,
            image_id=news_item.image_id,
            image_location=image_location,
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
    try:
        logger.info(f"Fetching {limit} newest full news articles")

        news_items = db.query(News).order_by(desc(News.timestamp)).limit(limit).all()

        news_list = []
        for item in news_items:
            categories_info = [
                CategoryInfoDTO.model_validate(c) for c in item.categories
            ]
            image_location = None
            if item.image:
                image_location = item.image.location
            news_list.append(
                NewsDetailDTO(
                    id=item.id,
                    title=item.title,
                    description=item.description,
                    categories=categories_info,
                    timestamp=item.timestamp,
                    source=item.source,
                    created_at=item.created_at,
                    image_id=item.image_id,
                    image_location=image_location,
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
    "/by-category/{category_id}/full",
    response_model=SuccessResponseDTO,
    summary="Get full news articles by category",
)
async def get_full_news_by_category(
    category_id: int,
    limit: int = Query(10, ge=1, le=50, description="Number of results to return"),
    db: Session = Depends(get_db),
):
    try:
        logger.info(f"Fetching full news articles for category ID: {category_id}")

        category = db.query(Category).filter(Category.id == category_id).first()
        if not category:
            logger.warning(f"Category ID {category_id} not found")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Category with ID {category_id} not found",
            )

        news_items = (
            db.query(News)
            .join(News.categories)
            .filter(Category.id == category_id)
            .order_by(desc(News.timestamp))
            .limit(limit)
            .all()
        )

        news_list = []
        for item in news_items:
            categories_info = [
                CategoryInfoDTO.model_validate(c) for c in item.categories
            ]
            image_location = None
            if item.image:
                image_location = item.image.location
            news_list.append(
                NewsDetailDTO(
                    id=item.id,
                    title=item.title,
                    description=item.description,
                    categories=categories_info,
                    timestamp=item.timestamp,
                    source=item.source,
                    created_at=item.created_at,
                    image_id=item.image_id,
                    image_location=image_location,
                )
            )

        logger.info(
            f"Found {len(news_list)} full news articles for category ID: {category_id}"
        )

        return SuccessResponseDTO(
            message=f"Found {len(news_list)} news articles", data=news_list
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error fetching full news by category: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch news articles: {str(e)}",
        )
