import os
import time
import logging
import requests
from dotenv import load_dotenv

load_dotenv()

API_KEY = os.getenv("GROQ_API_KEY")
BASE_URL = "https://api.groq.com/openai/v1/chat/completions"

# Setup logging
logging.basicConfig(level=logging.INFO)

class GroqClient:
    def __init__(self):
        if not API_KEY:
            raise ValueError("GROQ_API_KEY not found in environment")

        self.headers = {
            "Authorization": f"Bearer {API_KEY}",
            "Content-Type": "application/json"
        }

    def generate_response(self, prompt, retries=3):
        payload = {
            "model": "llama-3.3-70b-versatile",
            "messages": [
                {"role": "user", "content": prompt}
            ],
            "temperature": 0.7
        }

        for attempt in range(retries):
            try:
                response = requests.post(BASE_URL, headers=self.headers, json=payload)

                if response.status_code == 200:
                    data = response.json()

                    # JSON parsing
                    return data["choices"][0]["message"]["content"]

                else:
                    logging.error(f"API Error: {response.status_code} - {response.text}")

            except Exception as e:
                logging.error(f"Request failed: {str(e)}")

            # Backoff (2^attempt)
            wait_time = 2 ** attempt
            logging.info(f"Retrying in {wait_time}s...")
            time.sleep(wait_time)

        return "Error: Unable to get response after retries"