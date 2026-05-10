import json
import logging
from datetime import datetime, timezone
from flask import Blueprint, request, jsonify
from services.sanitizer import sanitize_all
from services.groq_client import call_groq

recommend_bp = Blueprint("recommend", __name__)
logger = logging.getLogger(__name__)

FALLBACK = {
    "recommendations": [
        {"action_type": "IMMEDIATE", "priority": "HIGH",
         "title": "Manual Compliance Review",
         "description": "AI service temporarily unavailable. Conduct manual compliance review.",
         "expected_impact": "Identify compliance gaps through manual process"},
    ],
    "overall_guidance": "AI analysis temporarily unavailable. Please consult your compliance team.",
    "is_fallback": True,
}

def load_prompt():
    with open("prompts/recommend.txt", "r") as f:
        return f.read()

@recommend_bp.route("/recommend", methods=["POST"])
def recommend():
    data = request.get_json(silent=True)
    if not data:
        return jsonify({"error": "Request body must be JSON"}), 400

    required = ["title", "source_country", "destination_country", "data_category", "transfer_mechanism"]
    for field in required:
        if not data.get(field):
            return jsonify({"error": f"Field '{field}' is required"}), 400

    clean, injected = sanitize_all({k: v for k, v in data.items() if isinstance(v, str)})
    if injected:
        logger.warning("Prompt injection detected in /recommend request")
        return jsonify({"error": "Invalid input detected"}), 400

    try:
        prompt = load_prompt().format(
            title=clean.get("title", data["title"]),
            source_country=clean.get("source_country", data["source_country"]),
            destination_country=clean.get("destination_country", data["destination_country"]),
            data_category=clean.get("data_category", data["data_category"]),
            transfer_mechanism=clean.get("transfer_mechanism", data["transfer_mechanism"]),
            compliance_score=data.get("compliance_score", 0),
            generated_at=datetime.now(timezone.utc).isoformat(),
        )
        raw = call_groq([{"role": "user", "content": prompt}], temperature=0.5)
        result = json.loads(raw)
        result["is_fallback"] = False
        return jsonify(result), 200
    except Exception as e:
        logger.error("/recommend error: %s", str(e))
        return jsonify(FALLBACK), 200
