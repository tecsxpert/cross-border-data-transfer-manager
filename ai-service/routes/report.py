import json
import logging
from datetime import datetime, timezone
from flask import Blueprint, request, jsonify
from services.sanitizer import sanitize_all
from services.groq_client import call_groq

report_bp = Blueprint("report", __name__)
logger = logging.getLogger(__name__)

FALLBACK = {
    "report_title": "Compliance Report — AI Unavailable",
    "executive_summary": "AI report generation temporarily unavailable. Manual review is required.",
    "transfer_overview": {
        "regulatory_framework": "Manual review required",
        "transfer_basis": "To be determined",
        "adequacy_status": "Unknown",
        "safeguards_in_place": [],
    },
    "risk_assessment": {
        "overall_risk": "MEDIUM",
        "risk_factors": ["AI unavailable — manual assessment required"],
        "mitigating_factors": [],
    },
    "compliance_gaps": ["AI analysis unavailable"],
    "recommended_actions": [{"priority": "HIGH", "action": "Conduct manual compliance review", "deadline": "Immediately"}],
    "conclusion": "AI report generation is temporarily unavailable. Please consult your compliance team.",
    "is_fallback": True,
}

def load_prompt():
    with open("prompts/report.txt", "r") as f:
        return f.read()

@report_bp.route("/generate-report", methods=["POST"])
def generate_report():
    data = request.get_json(silent=True)
    if not data:
        return jsonify({"error": "Request body must be JSON"}), 400

    required = ["title", "source_country", "destination_country", "data_category", "transfer_mechanism"]
    for field in required:
        if not data.get(field):
            return jsonify({"error": f"Field '{field}' is required"}), 400

    str_fields = {k: v for k, v in data.items() if isinstance(v, str)}
    clean, injected = sanitize_all(str_fields)
    if injected:
        logger.warning("Prompt injection detected in /generate-report request")
        return jsonify({"error": "Invalid input detected"}), 400

    try:
        prompt = load_prompt().format(
            transfer_id=data.get("transfer_id", "N/A"),
            title=clean.get("title", data["title"]),
            source_country=clean.get("source_country", data["source_country"]),
            destination_country=clean.get("destination_country", data["destination_country"]),
            data_category=clean.get("data_category", data["data_category"]),
            transfer_mechanism=clean.get("transfer_mechanism", data["transfer_mechanism"]),
            status=clean.get("status", data.get("status", "PENDING")),
            compliance_score=data.get("compliance_score", 0),
            generated_at=datetime.now(timezone.utc).isoformat(),
        )
        raw = call_groq([{"role": "user", "content": prompt}], temperature=0.3, max_tokens=2048)
        result = json.loads(raw)
        result["is_fallback"] = False
        return jsonify(result), 200
    except Exception as e:
        logger.error("/generate-report error: %s", str(e))
        fallback = dict(FALLBACK)
        fallback["generated_at"] = datetime.now(timezone.utc).isoformat()
        return jsonify(fallback), 200
