# Quick Start Guide for Judges

**Cross-Border Data Transfer Manager | Tool-135**
**Demo Date**: Friday, May 9, 2026

---

## 30-Second System Overview

Enterprise AI-powered system for managing cross-border data transfers with:
- **Real-time compliance scoring** (0-100)
- **AI analysis** powered by Groq LLaMA-3
- **Role-based security** (JWT + RBAC)
- **Audit logging** on all operations
- **Responsive UI** for all devices

**Architecture**: React Frontend → Spring Boot Backend → PostgreSQL + Redis + Flask AI Service

---

## System Access

### URLs
- **Frontend**: http://localhost
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

### Test Credentials
```
User Account:
  Username: user1
  Password: password123

Admin Account:
  Username: admin
  Password: admin123
```

---

## Key Features to Test (6 Minutes)

### 1. Dashboard (30 seconds)
- Shows: Total transfers, Approved count, Pending count, Other statuses
- Chart: Bar chart showing status distribution
- Action: Click "View All Transfers" to continue

**Verify**: 4 KPI cards display, chart renders correctly

### 2. Create Transfer (45 seconds)
- Click "+ New Transfer" button
- Fill in:
  - Source Country: `Germany`
  - Destination Country: `Brazil`
  - Data Type: `Health Data`
  - Status: `Pending`
  - Compliance Score: `75`
  - Risk Level: `High`
  - Legal Basis: `Consent`
- Click "Create"

**Verify**: New row appears in table, success feedback

### 3. Search & Filter (30 seconds)
- Type `USA` in search box → Table filters instantly
- Select Status `Approved` → Only approved show
- Set date range → Results filter
- Clear filters to see all 15+ demo records

**Verify**: Filtering is responsive, pagination works

### 4. View Details (45 seconds)
- Click "View" on any transfer
- See all fields: source, destination, type, status, compliance, risk, legal basis
- Notice: **Compliance Score Badge** (color-coded: green ≥80, yellow 60-79, red <60)
- Click "Analyze with AI" → Loading spinner → AI analysis appears

**Verify**: Detail page renders, AI button works, score badge displays

### 5. Export CSV (30 seconds)
- Go back to Dashboard
- Click "Export CSV"
- File downloads as `data-transfers.csv`
- Open in Excel/Notepad to verify: 10 columns including compliance data

**Verify**: File contains ID, Source, Destination, Type, Status, Compliance Score, Risk Level, Legal Basis, Created Date

### 6. Security Tests (1 minute 15 seconds)

#### 6.1 Unauthorized Access
- Open new browser tab
- Go to: http://localhost/list (without logging in)
- **Verify**: Redirected to login page (401 protection)

#### 6.2 Admin-Only Delete
- Login as `user1` → Go to List
- Click Delete on any transfer
- **Verify**: Delete button doesn't work (403 - USER role can't delete)
- Logout, login as `admin` → Delete works (ADMIN role can delete)

#### 6.3 Responsive Design
- Press F12 (Dev Tools)
- Toggle device toolbar → Mobile (375px)
- Navigate pages
- **Verify**: Tables stack, buttons adapt, UI is usable

---

## What You're Testing

### Technology Stack
| Component | Tech | Version |
|-----------|------|---------|
| Frontend | React | 19 |
| Backend | Spring Boot | 3.x |
| Database | PostgreSQL | 15 |
| Cache | Redis | 7 |
| AI | Groq LLaMA-3 | Latest |
| Containerization | Docker | Compose |

### Key Endpoints
- `GET /api/data-transfers` - List transfers (paginated)
- `POST /api/data-transfers` - Create transfer
- `PUT /api/data-transfers/{id}` - Update transfer
- `DELETE /api/data-transfers/{id}` - Delete (ADMIN only)
- `GET /api/data-transfers/search/filtered` - Advanced search
- `GET /api/data-transfers/export/csv` - CSV download
- `POST /analyze` - AI compliance analysis

### Security Features to Notice
- ✓ Requires login (JWT authentication)
- ✓ Roles matter (USER vs ADMIN)
- ✓ Audit trails (all operations logged)
- ✓ Input validation (form validation on create/edit)
- ✓ Responsive design (works on phone/tablet/desktop)

---

## Important Files to Review

### Documentation
| File | Purpose |
|------|---------|
| README.md | Setup, architecture, API reference |
| SECURITY.md | Security analysis & compliance (printed) |
| DEMO_SCENARIOS.md | Detailed demo walkthrough |
| Swagger UI | Interactive API documentation |

### Code Files to Highlight
| File | What It Shows |
|------|----------------|
| `DataTransferController.java` | REST API endpoints (11 endpoints) |
| `DataTransferService.java` | Business logic & caching |
| `DataTransferRepository.java` | Database queries |
| `AuditAspect.java` | Audit logging via AOP |
| `SecurityConfig.java` | JWT + RBAC configuration |
| `JwtUtil.java` | JWT token generation/validation |
| `Dashboard.jsx` | KPI cards + chart |
| `Detail.jsx` | AI analysis panel |
| `AuthContext.jsx` | Authentication state management |

---

## Troubleshooting During Demo

### "Login not working"
- Verify services are running: `docker ps`
- Check credentials: user1/password123
- Check browser console for errors (F12)

### "Search not filtering"
- Wait 400ms (debounce delay) then results should filter
- Try simpler search term: "USA"

### "AI analysis not working"
- Check Groq API key is set in .env
- Check AI service is running: http://localhost:5000/health
- Network latency may cause delay (Groq API call)

### "Database connection error"
- Wait 30 seconds for PostgreSQL to initialize
- Check docker-compose logs: `docker-compose logs postgres`

---

## Questions to Ask Developers

### Architecture
- "How does the system scale to 1 million transfers?"
- "What's your database indexing strategy?"
- "How does caching improve performance?"

### Security
- "What protects against SQL injection?"
- "How are passwords encrypted?"
- "Can you show me the audit log?"

### AI Integration
- "How does the AI compliance analysis work?"
- "What model are you using?"
- "How is the Groq API called?"

### Code Quality
- "How many test cases do you have?"
- "What's your test coverage?"
- "Show me a security test case."

---

## Scoring Criteria (Typical)

### Functionality (40%)
- ✓ All CRUD operations work
- ✓ Search/filter working
- ✓ CSV export working
- ✓ AI analysis working
- ✓ Charts/analytics showing

### Design (20%)
- ✓ Responsive design (mobile/tablet/desktop)
- ✓ Clean, professional UI
- ✓ Good UX (clear forms, feedback)
- ✓ Proper navigation

### Security (20%)
- ✓ Authentication required
- ✓ Role-based access control
- ✓ Audit logging
- ✓ Input validation
- ✓ No secrets in code

### Code Quality (10%)
- ✓ Clean code (no TODOs)
- ✓ Proper error handling
- ✓ Good architecture (MVC/layers)
- ✓ Tests included

### Documentation (10%)
- ✓ README complete
- ✓ Setup guide works
- ✓ API documented
- ✓ Security documented

---

## Demo Flow (6 Minutes)

| Time | Activity |
|------|----------|
| 0:00-0:30 | Login & show Dashboard (KPI cards + chart) |
| 0:30-1:15 | Create new transfer, show in table |
| 1:15-1:45 | Search/filter demo |
| 1:45-2:30 | View details, show AI analysis |
| 2:30-3:00 | Export CSV demo |
| 3:00-4:15 | Security tests (unauthorized, role-based, responsive) |
| 4:15-6:00 | Q&A + Buffer |

---

## Key Talking Points

**"This system helps companies manage cross-border data transfers compliantly."**

**"We use AI to automatically analyze compliance - it checks the countries, data types, and legal basis, then gives a score and recommendations in seconds."**

**"All operations are logged for audit trails - every change is tracked with timestamps and user information."**

**"It's secured with JWT tokens and role-based access - only admins can delete transfers."**

**"The responsive design works on phones, tablets, and desktops - mobile users get a full experience."**

---

## Success Criteria

✓ System boots with docker-compose up --build
✓ Login works with provided credentials
✓ Dashboard displays 4 KPI cards and chart
✓ Create/edit/delete transfers work
✓ Search and filter respond in real-time
✓ CSV export downloads correctly
✓ Detail page shows all fields + compliance score badge
✓ AI analysis button works (calls Flask service)
✓ Unauthorized requests are blocked (401)
✓ Admin-only operations enforce role (403)
✓ Design is responsive at 375px/768px/1280px

---

## Behind the Scenes (Not Visible in Demo)

### Database
- PostgreSQL with Flyway migrations
- 2 tables: `data_transfer` (11 columns), `audit_log` (7 columns)
- Indexes on: status, countries, compliance, risk_level
- 15+ seed records pre-loaded

### Backend
- 11 REST API endpoints
- Spring Security with JWT (24-hour expiration)
- Role-based access control (USER, ADMIN)
- AOP-based audit logging
- Redis caching on stats
- MockMvc tests (11 test cases)
- Swagger/OpenAPI documentation

### Frontend
- React 19 with Vite build
- Tailwind CSS for responsive design
- Recharts for visualizations
- Axios for API calls
- AuthContext for state management
- ProtectedRoute component for auth

### AI Service
- Flask microservice on port 5000
- Groq LLaMA-3 API integration
- Analyzes compliance based on transfer details
- Rate limited (30 req/min per IP)
- Health check endpoint

---

**Ready to Demo! Questions?**