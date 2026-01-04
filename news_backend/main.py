from fastapi import FastAPI, Depends
from sqlalchemy.orm import Session
from database.database import get_db, create_tables
from contextlib import asynccontextmanager
import logging
from sqlalchemy import text


from api.news_api import router as news_router
from api.category_api import router as category_router
from api.image_api import router as image_router

# Setup logging
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Create tables on startup
    logger.info("Starting News API Server...")
    create_tables()
    logger.info("Database tables created successfully")
    yield
    # Cleanup on shutdown if needed
    logger.info("Shutting down News API Server...")


app = FastAPI(
    title="News Website Mock API",
    description="A mock API server for news website",
    version="1.0.0",
    lifespan=lifespan,
)

app.include_router(news_router)
app.include_router(category_router)
app.include_router(image_router)


@app.get("/")
async def root():
    return {
        "message": "Welcome to News API Server",
        "version": "1.0.0",
        "endpoints": {
            "news": {
                "create_news": "POST /api/news/",
                "search_news": "GET /api/news/search?q={query}",
                "titles_by_category": "GET /api/news/by-category/{category}/titles",
                "titles_by_multiple_categories": "POST /api/news/by-multiple-categories/titles",
                "newest_titles": "GET /api/news/newest/titles",
                "news_by_id": "GET /api/news/{news_id}",
                "newest_full": "GET /api/news/newest/full",
                "full_by_category": "GET /api/news/by-category/{category}/full",
            },
            "images": {
                "upload_image": "POST /api/images/upload",
                "get_image_by_filename": "GET /api/images/{filename}",
                "get_image_by_id": "GET /api/images/by-id/{image_id}",
                "get_image_info": "GET /api/images/info/{image_id}",
            },
            "health": "/health",
            "docs": "/docs",
            "redoc": "/redoc",
        },
    }


@app.get("/health")
async def health_check(db: Session = Depends(get_db)):
    try:
        # FIXED: Use text() wrapper for raw SQL in SQLAlchemy 2.0+
        db.execute(text("SELECT 1"))
        return {"status": "healthy", "database": "connected"}
    except Exception as e:
        logger.error(f"Health check failed: {str(e)}")
        return {"status": "unhealthy", "database": "disconnected", "error": str(e)}
