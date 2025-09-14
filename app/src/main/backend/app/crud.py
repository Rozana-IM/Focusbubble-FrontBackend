from sqlalchemy.orm import Session
from . import models, schemas

def get_blocked_apps(db: Session):
    return db.query(models.BlockedApp).all()

def get_blocked_app(db: Session, app_id: int):
    return db.query(models.BlockedApp).filter(models.BlockedApp.id == app_id).first()

def create_blocked_app(db: Session, app: schemas.BlockedAppCreate):
    db_app = models.BlockedApp(
        package_name=app.package_name,
        app_name=app.app_name,
        duration_minutes=app.duration_minutes,
        is_active=app.is_active
    )
    db.add(db_app)
    db.commit()
    db.refresh(db_app)
    return db_app

def delete_blocked_app(db: Session, app_id: int):
    app = db.query(models.BlockedApp).filter(models.BlockedApp.id == app_id).first()
    if app:
        db.delete(app)
        db.commit()
        return True
    return False
