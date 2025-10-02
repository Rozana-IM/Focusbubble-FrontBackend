import os
from pathlib import Path
from dotenv import load_dotenv

# Absolute path to .env file
BASE_DIR = Path(__file__).resolve().parent.parent  # goes from app/ to backend/
ENV_PATH = BASE_DIR / ".env"

if not ENV_PATH.exists():
    raise FileNotFoundError(f".env file not found at {ENV_PATH}")

load_dotenv(dotenv_path=ENV_PATH, override=True)

GOOGLE_CLIENT_ID = os.getenv("GOOGLE_CLIENT_ID")
SECRET_KEY = os.getenv("SECRET_KEY")
DATABASE_URL = os.getenv("DATABASE_URL")

print("Loaded GOOGLE_CLIENT_ID:", GOOGLE_CLIENT_ID)
print("Loaded SECRET_KEY:", "*****" if SECRET_KEY else None)
print("Loaded DATABASE_URL:", DATABASE_URL)



# -------------------------------
# FastAPI and database setup
# -------------------------------
from fastapi import FastAPI, Depends, HTTPException, Body
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session

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
