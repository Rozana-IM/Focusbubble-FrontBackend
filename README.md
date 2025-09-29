FocusBubble Backend

Run
python -m venv .venv && . .venv/bin/activate
pip install -r app/requirements.txt
uvicorn app.main:app --reload --port 8080