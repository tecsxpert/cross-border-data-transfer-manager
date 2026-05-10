import json
import logging
from datetime import datetime, timezone
from flask import Blueprint, request, jsonify
from services.sanitizer import sanitize_all
from services.groq_client import call_groq

describe_bp = Blueprint("describe", __name__)
logger = logging.getLogger(__name__)

FALLBACK = {
    "summary": "AI analysis temporarily unavailable. Please review manually.",
    "compliance_framework": "Manual review required",
    "risk_level": "MEDIUM",
    "compliance_score": 50,
    "key_requirements": ["Manual compliance review required"],
    "data_subject_rights": "Please consult legal team",
    "is_fallback": True,
}

def load_prompt():
    with open("prompts/describe.txt", "r") as f:
        return f.read()

@describe_bp.route("/describe", methods=["POST"])
def describe():
    data = request.get_json(silent=True)
    if not data:
        return jsonify({"error": "Request body must be JSON"}), 400

    required = ["title", "source_country", "destination_country", "data_category", "transfer_mechanism"]
    for field in required:
        if not data.get(field):
            return jsonify({"error": f"Field '{field}' is required"}), 400

    clean, injected = sanitize_all(data)
    if injected:
        logger.warning("Prompt injection detected in /describe request")
        return jsonify({"error": "Invalid input detected"}), 400

    try:
        prompt = load_prompt().format(
            title=clean["title"],
            source_country=clean["source_country"],
            destination_country=clean["destination_country"],
            data_category=clean["data_category"],
            transfer_mechanism=clean["transfer_mechanism"],
            generated_at=datetime.now(timezone.utc).isoformat(),
        )
        raw = call_groq([{"role": "user", "content": prompt}], temperature=0.3)
        result = json.loads(raw)
        result["is_fallback"] = False
        return jsonify(result), 200
    except Exception as e:
        logger.error("/describe error: %s", str(e))
        return jsonify(FALLBACK), 200
