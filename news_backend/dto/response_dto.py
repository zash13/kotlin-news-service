from pydantic import BaseModel
from typing import Optional, Any, List
from datetime import datetime


class SuccessResponseDTO(BaseModel):
    success: bool = True
    message: Optional[str] = None
    data: Optional[Any] = None
    timestamp: datetime = datetime.now()


class ErrorResponseDTO(BaseModel):
    success: bool = False
    error: str
    details: Optional[str] = None
    timestamp: datetime = datetime.now()
