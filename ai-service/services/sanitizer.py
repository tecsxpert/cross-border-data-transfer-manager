import re
import html

INJECTION_PATTERNS = [
    r"ignore\s+(previous|all|above|prior)\s+instructions?",
    r"you\s+are\s+now",
    r"disregard\s+",
    r"forget\s+everything",
    r"act\s+as",
    r"pretend\s+(to\s+be|you\s+are)",
    r"jailbreak",
    r"dan\s+mode",
    r"<script",
    r"javascript:",
    r"on\w+\s*=",
]

COMPILED = [re.compile(p, re.IGNORECASE) for p in INJECTION_PATTERNS]


def sanitize(text: str) -> str:
    """Strip HTML entities and tags."""
    if not isinstance(text, str):
        return str(text)
    text = html.unescape(text)
    text = re.sub(r"<[^>]+>", "", text)
    return text.strip()


def detect_injection(text: str) -> bool:
    """Return True if prompt injection pattern detected."""
    if not isinstance(text, str):
        return False
    for pattern in COMPILED:
        if pattern.search(text):
            return True
    return False


def sanitize_all(data: dict) -> tuple[dict, bool]:
    """
    Sanitize all string values in a dict.
    Returns (sanitized_dict, injection_detected).
    """
    sanitized = {}
    injection_found = False
    for key, value in data.items():
        if isinstance(value, str):
            clean = sanitize(value)
            if detect_injection(clean):
                injection_found = True
            sanitized[key] = clean
        else:
            sanitized[key] = value
    return sanitized, injection_found
