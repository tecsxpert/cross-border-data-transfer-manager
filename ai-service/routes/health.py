import time
from flask import Blueprint, jsonify

health_bp = Blueprint("health", __name__)

# Set at import time — avoids circular import with app.py
_START_TIME = time.time()

@health_bp.route("/health", methods=["GET"])
def health():
    uptime_seconds = int(time.time() - _START_TIME)
    return jsonify({
        "status": "healthy",
        "model": "llama-3.3-70b-versatile",
        "uptime_seconds": uptime_seconds,
        "endpoints": ["/describe", "/recommend", "/generate-report", "/health"],
        "rate_limit": "30 requests/minute per IP",
    }), 200
