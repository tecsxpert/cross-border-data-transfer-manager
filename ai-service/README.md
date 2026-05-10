# AI Service — README

## Setup

```bash
cd ai-service
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| `GROQ_API_KEY` | Your Groq API key from console.groq.com |

Create a `.env` file:
```
GROQ_API_KEY=your-key-here
```

## Run

```bash
python app.py
# Service starts on http://localhost:5000
```

## API Reference

### GET /health
Returns service status, model name, uptime.

**Response:**
```json
{
  "status": "healthy",
  "model": "llama-3.3-70b-versatile",
  "uptime_seconds": 120,
  "endpoints": ["/describe", "/recommend", "/generate-report", "/health"]
}
```

---

### POST /describe
Analyse a data transfer and return compliance description.

**Request:**
```json
{
  "title": "GDPR Data Export",
  "source_country": "Germany",
  "destination_country": "United States",
  "data_category": "Personal Data",
  "transfer_mechanism": "Standard Contractual Clauses"
}
```

**Response:**
```json
{
  "summary": "...",
  "compliance_framework": "GDPR Article 46",
  "risk_level": "LOW",
  "compliance_score": 85,
  "key_requirements": ["..."],
  "data_subject_rights": "...",
  "is_fallback": false
}
```

---

### POST /recommend
Return 3 prioritised compliance recommendations.

**Request:** Same fields as `/describe` plus `compliance_score` (integer 0-100).

**Response:**
```json
{
  "recommendations": [
    {
      "action_type": "IMMEDIATE",
      "priority": "HIGH",
      "title": "...",
      "description": "...",
      "expected_impact": "..."
    }
  ],
  "overall_guidance": "...",
  "is_fallback": false
}
```

---

### POST /generate-report
Generate a full structured compliance report.

**Request:** Same as `/describe` plus `transfer_id`, `status`, `compliance_score`.

**Response:** Full JSON report with executive summary, risk assessment, compliance gaps, recommended actions.

---

## Rate Limiting

30 requests per minute per IP address. Exceeding returns HTTP 429.

## Tests

```bash
pytest tests/ -v
# All tests use mocked Groq — no live network access required
```
