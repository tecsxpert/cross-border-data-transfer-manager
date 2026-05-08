import os
from groq import Groq

class GroqClient:
    def __init__(self):
        self.client = Groq(api_key=os.getenv("GROQ_API_KEY"))

    def analyze_compliance(self, transfer_data):
        prompt = f"""
        Analyze the following cross-border data transfer for GDPR compliance:

        Source Country: {transfer_data['sourceCountry']}
        Destination Country: {transfer_data['destinationCountry']}
        Data Type: {transfer_data['dataType']}
        Description: {transfer_data.get('description', '')}
        Current Compliance Score: {transfer_data.get('complianceScore', 'N/A')}
        Risk Level: {transfer_data.get('riskLevel', 'N/A')}
        Legal Basis: {transfer_data.get('legalBasis', 'N/A')}

        Provide:
        1. Updated compliance score (0-100)
        2. Risk assessment
        3. Recommendations for compliance
        4. Any required actions

        Be concise but thorough.
        """

        response = self.client.chat.completions.create(
            model="llama3-8b-8192",
            messages=[{"role": "user", "content": prompt}],
            max_tokens=500,
            temperature=0.3
        )

        return response.choices[0].message.content.strip()