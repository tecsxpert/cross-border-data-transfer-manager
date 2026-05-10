# Tool-135 — Cross-Border Data Transfer Manager

> AI-powered web application to manage, analyse, and generate compliance reports for cross-border data transfers.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Browser (port 80)                     │
│                  React 18 + Vite + Tailwind              │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP / Axios
┌────────────────────────▼────────────────────────────────┐
│              Spring Boot 3.x  (port 8080)               │
│   REST API · JWT Auth · Redis Cache · Flyway · Mail     │
└──────┬────────────────────────────┬─────────────────────┘
       │ JDBC                       │ RestTemplate
┌──────▼──────┐          ┌──────────▼──────────────────┐
│ PostgreSQL  │          │   Flask AI Service (5000)    │
│     15      │          │  /describe /recommend        │
└─────────────┘          │  /generate-report /health   │
┌─────────────┐          └─────────────────────────────┘
│  Redis 7    │                      │ Groq API (LLaMA-3.3-70b)
│  Cache      │          ┌───────────▼──────────────────┐
└─────────────┘          │   console.groq.com           │
                         └──────────────────────────────┘
```

## Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Docker | 24+ | docs.docker.com |
| Docker Compose | 2.x | included with Docker Desktop |
| Git | 2.x | git-scm.com |

## Environment Variables

Copy `.env.example` to `.env` and fill in the values:

| Variable | Description |
|----------|-------------|
| `DB_HOST` | PostgreSQL host (postgres inside Docker) |
| `DB_PORT` | PostgreSQL port (5432) |
| `DB_NAME` | Database name |
| `DB_USERNAME` | Database user |
| `DB_PASSWORD` | Database password |
| `REDIS_HOST` | Redis host (redis inside Docker) |
| `REDIS_PORT` | Redis port (6379) |
| `JWT_SECRET` | Long random string (min 256 bits) |
| `JWT_EXPIRATION_MS` | Token lifetime in ms (86400000 = 24h) |
| `MAIL_HOST` | SMTP host |
| `MAIL_PORT` | SMTP port |
| `MAIL_USERNAME` | SMTP username |
| `MAIL_PASSWORD` | SMTP app password |
| `AI_SERVICE_URL` | AI microservice URL |
| `GROQ_API_KEY` | Groq API key from console.groq.com |

## Setup & Run

```bash
# 1. Clone the repository
git clone <repository-url>
cd tool-135

# 2. Create your env file
cp .env.example .env
# Edit .env with your actual values

# 3. Start all services
docker-compose up --build

# 4. Verify services
# Frontend   → http://localhost
# Backend    → http://localhost:8080/swagger-ui.html
# AI Health  → http://localhost:5000/health
```

## Default Login

```
Username: admin
Password: Admin@123
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/auth/register | Register user |
| POST | /api/auth/login | Login → JWT |
| GET | /api/transfers | List (paginated) |
| POST | /api/transfers | Create transfer |
| GET | /api/transfers/{id} | Get by ID |
| PUT | /api/transfers/{id} | Update |
| DELETE | /api/transfers/{id} | Soft delete |
| GET | /api/transfers/search | Search & filter |
| GET | /api/stats | Dashboard KPIs |
| GET | /api/transfers/export | CSV export |
| POST | /api/ai/describe | AI description |
| POST | /api/ai/recommend | AI recommendations |
| POST | /api/ai/generate-report | AI full report |
| GET | /api/ai/health | AI service health |

## Testing

```bash
# Backend unit tests
cd backend && mvn test

# AI service tests (no live network required)
cd ai-service && pytest
```
