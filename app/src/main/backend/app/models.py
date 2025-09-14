from sqlalchemy import Column, Integer, String, Boolean
from .database import Base

class BlockedApp(Base):
    __tablename__ = "blocked_apps"

    id = Column(Integer, primary_key=True, index=True)
    package_name = Column(String, index=True, nullable=False)
    app_name = Column(String, nullable=False)
    duration_minutes = Column(Integer, nullable=False)
    is_active = Column(Boolean, default=True)