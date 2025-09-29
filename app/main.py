from dotenv import load_dotenv
import os

load_dotenv()
GOOGLE_CLIENT_ID = os.getenv("GOOGLE_CLIENT_ID")
print("Loaded GOOGLE_CLIENT_ID:", GOOGLE_CLIENT_ID)

from fastapi import FastAPI, Depends, HTTPException, Body
from sqlalchemy.orm import Session
from fastapi.middleware.cors import CORSMiddleware

# Correct package imports
from app import models, schemas, crud
from app.database import SessionLocal, engine
from app.auth import verify_google_token

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

# -------------------------------
# Health check
# -------------------------------
@app.get("/")
def read_root():
    return {"message": "FocusBubble API is running"}

# -------------------------------
# Blocked Apps Endpoints
# -------------------------------
@app.get("/blocked_apps", response_model=list[schemas.BlockedAppResponse])
def read_blocked_apps(db: Session = Depends(get_db)):
    return crud.get_blocked_apps(db)

@app.get("/blocked_apps/{app_id}", response_model=schemas.BlockedAppResponse)
def read_blocked_app(app_id: int, db: Session = Depends(get_db)):
    app_record = crud.get_blocked_app(db, app_id)
    if app_record is None:
        raise HTTPException(status_code=404, detail="Blocked app not found")
    return app_record

@app.post("/blocked_apps", response_model=schemas.BlockedAppResponse)
def create_blocked_app(app: schemas.BlockedAppCreate, db: Session = Depends(get_db)):
    return crud.create_blocked_app(db, app)

@app.delete("/blocked_apps/{app_id}")
def delete_blocked_app(app_id: int, db: Session = Depends(get_db)):
    deleted = crud.delete_blocked_app(db, app_id)
    if not deleted:
        raise HTTPException(status_code=404, detail="Blocked app not found")
    return {"message": "Blocked app deleted successfully"}

# -------------------------------
# Google Auth Endpoint
# -------------------------------
@app.post("/auth/google")
def google_auth(id_token: str = Body(..., embed=True)):
    """
    Verify Google ID token and return user info.
    Body example:
    {
        "id_token": "<GOOGLE_ID_TOKEN>"
    }
    """
    try:
        user_info = verify_google_token(id_token, GOOGLE_CLIENT_ID)
        return user_info
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Invalid token: {str(e)}")
