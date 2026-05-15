FROM python:3.10-slim

# Set working directory
WORKDIR /app

# Copy requirements first
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy all project files
COPY . .

# Expose Flask port
EXPOSE 5000

# Run Flask app
CMD ["python", "app.py"]