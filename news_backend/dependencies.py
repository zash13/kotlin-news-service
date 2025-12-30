from database.database import get_db
from sqlalchemy.orm import Session


def get_database() -> Session:
    return next(get_db())
