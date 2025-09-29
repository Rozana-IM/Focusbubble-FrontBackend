from pydantic import BaseModel

class BlockedAppBase(BaseModel):
    package_name: str
    app_name: str
    duration_minutes: int
    is_active: bool = True

class BlockedAppCreate(BlockedAppBase):
    pass

class BlockedAppResponse(BlockedAppBase):
    id: int

    class Config:
        from_attributes = True