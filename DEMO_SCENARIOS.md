# Demo Scenarios - Cross-Border Data Transfer Manager

**Demo Day: Friday, May 9, 2026 | Duration: 6 minutes**

---

## Scenario 1: End-to-End Transfer Management (2 minutes)

### Objective
Demonstrate the complete workflow of creating, managing, and monitoring a data transfer.

### Steps
1. **Login**
   - URL: `http://localhost`
   - Username: `user1`
   - Password: `password123`
   - Expected: Dashboard displays with 4 KPI cards and chart

2. **Navigate to List Page**
   - Click "View All Transfers" button
   - Expected: Table loads with 15+ demo transfers, showing ID, Source, Destination, Type, Status, Compliance Score, Risk Level

3. **Create New Transfer**
   - Click "+ New Transfer" button
   - Fill form:
     - Source Country: "Germany"
     - Destination Country: "India"
     - Data Type: "Health Data"
     - Status: "Pending"
     - Description: "Patient records for research"
     - Compliance Score: "75"
     - Risk Level: "High"
     - Legal Basis: "Consent"
   - Click "Create"
   - Expected: Modal closes, new row appears in table

4. **View Transfer Details**
   - Click "View" on the newly created transfer
   - Expected: Detail page shows all fields + compliance score badge (yellow for 75/100)

5. **AI Analysis**
   - Click "Analyze with AI" button
   - Expected: Loading spinner appears, then AI compliance analysis displays

6. **Edit Transfer**
   - Go back to list, click "Edit" on first transfer
   - Change Status: "Approved"
   - Change Compliance Score: "90"
   - Click "Update"
   - Expected: Modal closes, table updates

### Talking Points
- "Our system provides a complete lifecycle management for cross-border data transfers"
- "Real-time compliance scoring helps identify risk levels"
- "AI-powered analysis provides instant compliance insights"
- "Audit logging ensures regulatory accountability"

---

## Scenario 2: Search, Filter & Export (1.5 minutes)

### Objective
Show powerful search and data analysis capabilities.

### Steps
1. **Search Functionality**
   - Type "USA" in search box
   - Expected: Table filters to show only USA transfers

2. **Multi-Filter**
   - Clear search
   - Select Status: "Approved"
   - Set Start Date: "2026-05-01"
   - Expected: Table shows only approved transfers from May 1 onwards

3. **CSV Export**
   - Go to Dashboard
   - Click "Export CSV"
   - Expected: File downloads as `data-transfers.csv`
   - Show file contains: ID, Source, Destination, Type, Status, Compliance Score, Risk Level, Legal Basis, Created Date

4. **Analytics**
   - Click "Analytics" in navbar
   - Expected: Page shows bar chart and pie chart of transfer statuses, summary cards

### Talking Points
- "Advanced filtering helps compliance teams quickly find relevant transfers"
- "CSV export enables reporting and compliance documentation"
- "Visual analytics provide executive-level insights"
- "All operations are logged for audit compliance"

---

## Scenario 3: Security & Compliance (1.5 minutes)

### Objective
Demonstrate security features and compliance readiness.

### Steps
1. **Unauthorized Access Test**
   - Open new browser tab, navigate to: `http://localhost/list`
   - Expected: Redirected to login page (401 response)
   - Talking Point: "All endpoints are protected with JWT authentication"

2. **Role-Based Access**
   - Logout (user1)
   - Login as: `admin` / `admin123`
   - Navigate to List page
   - Try to click "Delete" on a transfer
   - Expected: Delete succeeds (only ADMIN can delete)
   - Talking Point: "Role-based access control (RBAC) enforces proper authorization"

3. **Audit Logging Demo**
   - Show SECURITY.md document
   - Reference: "Audit Log" table captures all CUD operations with username and timestamp
   - Talking Point: "Every action is logged for compliance and investigation purposes"

4. **Responsive Design**
   - Open developer tools (F12)
   - Toggle device toolbar to 375px (mobile)
   - Navigate through pages
   - Expected: Tables, forms, buttons adapt to mobile layout
   - Talking Point: "Responsive design ensures accessibility across all devices"

### Talking Points
- "Security is built into every layer: authentication, authorization, audit logging"
- "GDPR-compliant audit trails track all operations"
- "Regular security audits and penetration testing are recommended"
- "Comprehensive SECURITY.md documentation guides production deployment"

---

## Pre-Demo Checklist

- [ ] Fresh database with seed data loaded
- [ ] All services running: `docker-compose up --build`
- [ ] Backend health: `http://localhost:8080/actuator/health` → "UP"
- [ ] Swagger docs: `http://localhost:8080/swagger-ui.html` accessible
- [ ] AI service health: `http://localhost:5000/health` → healthy
- [ ] Frontend loads: `http://localhost` without errors
- [ ] Test user credentials working (user1/password123, admin/admin123)
- [ ] SECURITY.md printed (1 copy for judges)
- [ ] Laptop connected to projector, volume tested
- [ ] Browser in full screen, no distracting notifications

---

## Post-Demo Q&A Talking Points

**Q: How is the system secured?**
A: "We implement multi-layered security: JWT authentication for API access, role-based authorization (USER/ADMIN), SQL injection prevention through parameterized queries, file upload validation, and comprehensive audit logging. All sensitive data is encrypted and environment variables store secrets."

**Q: How does the AI analysis work?**
A: "We integrate with Groq's LLaMA-3 API for real-time compliance analysis. The AI reviews transfer characteristics—source/destination countries, data types, legal basis—against GDPR frameworks and provides compliance scores and recommendations instantly."

**Q: Can it handle production scale?**
A: "Yes. We use PostgreSQL for reliable data storage, Redis for caching, connection pooling for performance, and Docker containerization for horizontal scaling. Database indexes on key fields prevent N+1 queries. Rate limiting protects from abuse."

**Q: What about regulatory compliance?**
A: "The system assists with GDPR compliance through audit trails, compliance scoring, risk assessments, and legal basis documentation. All operations are logged with user context. We provide production security recommendations in SECURITY.md."

---

## Expected Timing

| Activity | Time |
|----------|------|
| Login & Dashboard Overview | 0:30 |
| Create Transfer | 0:45 |
| View/Edit/Delete Operations | 0:45 |
| Search/Filter Demo | 0:30 |
| CSV Export | 0:30 |
| Security Features | 0:45 |
| Q&A Buffer | 1:15 |
| **Total** | **6:00** |