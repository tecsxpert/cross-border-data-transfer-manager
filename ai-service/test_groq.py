import os
from dotenv import load_dotenv
from services.groq_client import GroqClient

# Load environment variables
load_dotenv()

def main():
    try:
        client = GroqClient()

        prompt = "Explain cross-border data transfer in one paragraph"

        print("\n Sending request to Groq...\n")

        response = client.generate_response(prompt)

        print(" AI Response:\n")
        print(response)

    except Exception as e:
        print(" Error:", str(e))


if __name__ == "__main__":
    main()