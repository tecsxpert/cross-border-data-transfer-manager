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

    # -----------------------------
    # Load prompt from file
    # -----------------------------
    def load_prompt_template(self):
        try:
            base_dir = os.path.dirname(os.path.dirname(__file__))
            prompt_path = os.path.join(base_dir, "prompts", "generate_prompt.txt")

            with open(prompt_path, "r", encoding="utf-8") as file:
                return file.read()

        except Exception as e:
            logging.error(f"Error loading prompt file: {str(e)}")
            return None

    # -----------------------------
    # Generate response
    # -----------------------------
    def generate_response(self, user_input, retries=3):

        template = self.load_prompt_template()

        if not template:
            return "Error: Prompt template not found"

        # Inject user input into template
        final_prompt = template.replace("{input}", user_input)

        payload = {
            "model": "llama-3.3-70b-versatile",
            "messages": [
                {
                    "role": "system",
                    "content": "You are an expert AI assistant specializing in cross-border data transfer and data compliance."
                },
                {
                    "role": "user",
                    "content": final_prompt
                }
            ],
            "temperature": 0.5
        }

        for attempt in range(retries):
            try:
                response = requests.post(BASE_URL, headers=self.headers, json=payload)

                if response.status_code == 200:
                    data = response.json()
                    return data["choices"][0]["message"]["content"]

                else:
                    logging.error(f"API Error: {response.status_code} - {response.text}")

            except Exception as e:
                logging.error(f"Request failed: {str(e)}")

            # Exponential backoff
            wait_time = 2 ** attempt
            logging.info(f"Retrying in {wait_time}s...")
            time.sleep(wait_time)

        return "Error: Unable to get response after retries"