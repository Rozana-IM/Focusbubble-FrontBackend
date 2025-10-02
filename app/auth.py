from google.oauth2 import id_token as google_id_token
from google.auth.transport import requests as grequests
from fastapi import FastAPI, HTTPException, Body

# Your Android OAuth Client ID
ANDROID_CLIENT_ID = "464315770315-gstn8esmrr626nbmdrmkafc5mko19rq2.apps.googleusercontent.com"
ALLOWED_CLIENT_IDS = [ANDROID_CLIENT_ID]

def verify_google_token(token: str):
    """
    Verifies Google ID token for Android client.
    Returns payload dict if valid.
    Raises HTTPException(401) if invalid.
    """
    try:
        request = grequests.Request()
        # Verify token using Google library
        info = google_id_token.verify_oauth2_token(token, request, audience=None)

        # Check that token's audience matches allowed client ID
        if info.get("aud") not in ALLOWED_CLIENT_IDS:
            raise ValueError(f"Token audience {info.get('aud')} is not valid")

        # Optional: verify email is verified
        if not info.get("email_verified", False):
            raise ValueError("Email not verified")

        return info

    except Exception as e:
        raise HTTPException(status_code=401, detail=f"Invalid Google token: {e}")

# ------------------ FastAPI app ------------------

app = FastAPI()

@app.post("/auth/google")
async def google_login(id_token: str = Body(..., embed=True)):
    """
    Accepts a Google ID token from Android app and returns user info.
    """
    user_info = verify_google_token(id_token)
    return {
        "id": user_info["sub"],
        "email": user_info["email"],
        "name": user_info.get("name"),
        "picture": user_info.get("picture"),
    }
