import os
import uuid
from pathlib import Path
from fastapi import APIRouter, Depends, HTTPException, status, UploadFile, File
from sqlalchemy.orm import Session
from fastapi.responses import FileResponse
from typing import Optional
import logging
from PIL import Image as PILImage
import io

from database.database import get_db
from database.models import Image
from dto.response_dto import SuccessResponseDTO

logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api/images", tags=["images"])

IMAGE_STORAGE_LOCATION = os.getenv("IMAGE_STORAGE_LOCATION", "./images")
UPLOAD_DIR = Path(IMAGE_STORAGE_LOCATION)
UPLOAD_DIR.mkdir(parents=True, exist_ok=True)

ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"}
MAX_FILE_SIZE = 10 * 1024 * 1024


@router.post(
    "/upload",
    response_model=SuccessResponseDTO,
    status_code=status.HTTP_201_CREATED,
    summary="Upload an image",
)
async def upload_image(
    file: UploadFile = File(...),
    alt_text: Optional[str] = None,
    db: Session = Depends(get_db),
):
    try:
        logger.info(f"Received upload request for file: {file.filename}")

        file_ext = Path(file.filename).suffix.lower()
        if file_ext not in ALLOWED_EXTENSIONS:
            logger.warning(f"Invalid file extension: {file_ext}")
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Invalid file type. Allowed types: {', '.join(ALLOWED_EXTENSIONS)}",
            )

        contents = await file.read()
        if len(contents) > MAX_FILE_SIZE:
            logger.warning(f"File too large: {len(contents)} bytes")
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"File too large. Maximum size: {MAX_FILE_SIZE / (1024*1024):.0f}MB",
            )

        try:
            pil_image = PILImage.open(io.BytesIO(contents))
            pil_image.verify()
        except Exception as e:
            logger.warning(f"Invalid image file: {str(e)}")
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid image file",
            )

        filename = f"{uuid.uuid4()}{file_ext}"
        file_path = UPLOAD_DIR / filename

        pil_image = PILImage.open(io.BytesIO(contents))
        pil_image.save(file_path, format=pil_image.format)

        location = f"/api/images/{filename}"

        db_image = Image(
            location=location,
            filename=filename,
            alt_text=alt_text,
        )

        db.add(db_image)
        db.commit()
        db.refresh(db_image)

        logger.info(f"Image uploaded successfully with ID: {db_image.id}")

        return SuccessResponseDTO(
            message="Image uploaded successfully",
            data={
                "image_id": db_image.id,
                "location": db_image.location,
                "filename": db_image.filename,
                "alt_text": db_image.alt_text,
            },
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error uploading image: {str(e)}")
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to upload image: {str(e)}",
        )


@router.get(
    "/{filename}",
    summary="Get image by filename",
)
async def get_image(filename: str):
    try:
        logger.info(f"Fetching image: {filename}")

        file_path = UPLOAD_DIR / filename

        if not file_path.exists():
            logger.warning(f"Image not found: {filename}")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Image not found",
            )

        file_ext = Path(filename).suffix.lower()
        media_type = {
            ".jpg": "image/jpeg",
            ".jpeg": "image/jpeg",
            ".png": "image/png",
            ".gif": "image/gif",
            ".webp": "image/webp",
        }.get(file_ext, "application/octet-stream")

        logger.info(f"Image retrieved successfully: {filename}")

        return FileResponse(
            path=str(file_path),
            media_type=media_type,
            filename=filename,
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error fetching image {filename}: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch image: {str(e)}",
        )


@router.get(
    "/info/{image_id}",
    response_model=SuccessResponseDTO,
    summary="Get image metadata by ID",
)
async def get_image_info(image_id: int, db: Session = Depends(get_db)):
    try:
        logger.info(f"Fetching image info for ID: {image_id}")

        image = db.query(Image).filter(Image.id == image_id).first()

        if not image:
            logger.warning(f"Image with ID {image_id} not found")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Image with ID {image_id} not found",
            )

        return SuccessResponseDTO(
            message="Image info retrieved successfully",
            data={
                "image_id": image.id,
                "location": image.location,
                "filename": image.filename,
                "alt_text": image.alt_text,
                "created_at": image.created_at,
            },
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error fetching image info {image_id}: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch image info: {str(e)}",
        )


@router.get(
    "/by-id/{image_id}",
    summary="Get image by ID",
)
async def get_image_by_id(image_id: int, db: Session = Depends(get_db)):
    try:
        logger.info(f"Fetching image with ID: {image_id}")

        image = db.query(Image).filter(Image.id == image_id).first()

        if not image:
            logger.warning(f"Image with ID {image_id} not found")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Image not found",
            )

        file_path = UPLOAD_DIR / image.filename

        if not file_path.exists():
            logger.warning(f"Image file not found: {image.filename}")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Image file not found",
            )

        file_ext = Path(image.filename).suffix.lower()
        media_type = {
            ".jpg": "image/jpeg",
            ".jpeg": "image/jpeg",
            ".png": "image/png",
            ".gif": "image/gif",
            ".webp": "image/webp",
        }.get(file_ext, "application/octet-stream")

        logger.info(f"Image retrieved successfully: {image.filename}")

        return FileResponse(
            path=str(file_path),
            media_type=media_type,
            filename=image.filename,
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error fetching image by ID {image_id}: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to fetch image: {str(e)}",
        )
