from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from fastapi.middleware.cors import CORSMiddleware

from . import models, schemas, crud
from .database import engine, SessionLocal

# Create tables
models.Base.metadata.create_all(bind=engine)

app = FastAPI(title="FocusBubble API")

# CORS settings to allow Android app
origins = [
    "http://localhost",
    "http://10.0.2.2",  # Android emulator
    "*"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Dependency for DB session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Health check
@app.get("/")
def read_root():
    return {"message": "FocusBubble API is running"}

# GET all blocked apps
@app.get("/blocked_apps", response_model=list[schemas.BlockedAppResponse])
def read_blocked_apps(db: Session = Depends(get_db)):
    return crud.get_blocked_apps(db)

# GET single blocked app by ID
@app.get("/blocked_apps/{app_id}", response_model=schemas.BlockedAppResponse)
def read_blocked_app(app_id: int, db: Session = Depends(get_db)):
    app_record = crud.get_blocked_app(db, app_id)
    if app_record is None:
        raise HTTPException(status_code=404, detail="Blocked app not found")
    return app_record

# POST new blocked app
@app.post("/blocked_apps", response_model=schemas.BlockedAppResponse)
def create_blocked_app(app: schemas.BlockedAppCreate, db: Session = Depends(get_db)):
    return crud.create_blocked_app(db, app)

# DELETE blocked app
@app.delete("/blocked_apps/{app_id}")
def delete_blocked_app(app_id: int, db: Session = Depends(get_db)):
    deleted = crud.delete_blocked_app(db, app_id)
    if not deleted:
        raise HTTPException(status_code=404, detail="Blocked app not found")
    return {"message": "Blocked app deleted successfully"}
