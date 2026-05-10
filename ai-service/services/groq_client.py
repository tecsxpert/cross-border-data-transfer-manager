import os
import time
import logging
from groq import Groq

logger = logging.getLogger(__name__)

client = Groq(api_key=os.environ.get("GROQ_API_KEY", ""))

MODEL = "llama-3.3-70b-versatile"
MAX_RETRIES = 3
RETRY_DELAYS = [1, 2, 4]  # exponential backoff seconds


def call_groq(messages: list, temperature: float = 0.3, max_tokens: int = 1024) -> str:
    """Call Groq API with 3-retry exponential backoff. Returns text or raises."""
    last_error = None
    for attempt in range(MAX_RETRIES):
        try:
            response = client.chat.completions.create(
                model=MODEL,
                messages=messages,
                temperature=temperature,
                max_tokens=max_tokens,
                response_format={"type": "json_object"},
            )
            return response.choices[0].message.content
        except Exception as e:
            last_error = e
            logger.warning("Groq attempt %d/%d failed: %s", attempt + 1, MAX_RETRIES, str(e))
            if attempt < MAX_RETRIES - 1:
                time.sleep(RETRY_DELAYS[attempt])

    raise RuntimeError(f"Groq failed after {MAX_RETRIES} attempts: {last_error}")
