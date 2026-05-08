from flask import Flask, request, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
main
from services.groq_client import GroqClient
import os
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)

limiter = Limiter(
    app=app,
    key_func=get_remote_address,
    default_limits=["30 per minute"]
)

groq_client = GroqClient()

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "healthy"})

@app.route('/analyze', methods=['POST'])
@limiter.limit("10 per minute")
def analyze_transfer():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "No data provided"}), 400

        result = groq_client.analyze_compliance(data)
        return jsonify({"analysis": result})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
    
import re

from services.groq_client import GroqClient

app = Flask(__name__)

# Rate limiter (30 req/min per IP)
limiter = Limiter(get_remote_address, app=app, default_limits=["30 per minute"])

client = GroqClient()

# -----------------------------
# Input Sanitization Functions
# -----------------------------

def strip_html(text):
    """Remove HTML tags"""
    return re.sub(r'<.*?>', '', text)

def detect_prompt_injection(text):
    """Basic prompt injection detection"""
    suspicious_patterns = [
        "ignore previous instructions",
        "disregard above",
        "act as system",
        "you are now",
        "override rules"
    ]

    text_lower = text.lower()
    return any(pattern in text_lower for pattern in suspicious_patterns)

# -----------------------------
# API Endpoint
# -----------------------------

@app.route("/generate", methods=["POST"])
@limiter.limit("30 per minute")
def generate():
    data = request.get_json()

    if not data or "prompt" not in data:
        return jsonify({"error": "Missing prompt"}), 400

    prompt = data["prompt"]

    # Strip HTML
    clean_prompt = strip_html(prompt)

    # Detect injection
    if detect_prompt_injection(clean_prompt):
        return jsonify({"error": "Potential prompt injection detected"}), 400

    # Call AI
    response = client.generate_response(clean_prompt)

    return jsonify({"response": response})


@app.route("/health", methods=["GET"])
def health():
    return {"status": "ok"}


@app.route("/", methods=["GET"])
def home():
    return {"message": "AI Service Running"}

@app.after_request
def add_security_headers(response):
    response.headers["Content-Security-Policy"] = "default-src 'self'"
    response.headers["X-Content-Type-Options"] = "nosniff"
    response.headers["X-Frame-Options"] = "DENY"
    response.headers["X-XSS-Protection"] = "1; mode=block"
    return response


# -----------------------------
# Run App
# -----------------------------

if __name__ == "__main__":
    app.run(debug=True)
main
