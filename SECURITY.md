# SECURITY.md — Tool-135 Cross-Border Data Transfer Manager

**Version:** 1.0.0 | **Sprint:** 14 April – 9 May 2026 | **Team:** Team 5

---

## Executive Summary

Tool-135 implements defence-in-depth security across all three tiers (frontend, backend, AI service). This document records all threats identified, tests conducted, findings addressed, and residual risks.

---

## Threat Model

| # | Threat | Vector | Impact | Mitigation |
|---|--------|--------|--------|------------|
| T1 | Broken Authentication | Brute-force login | High | JWT expiry, BCrypt password hashing |
| T2 | SQL Injection | Malicious query parameters | Critical | JPA parameterised queries only — no raw SQL |
| T3 | Prompt Injection | Crafted AI inputs | High | Regex sanitiser + HTML stripping in ai-service |
| T4 | Cross-Site Scripting (XSS) | Injected scripts in inputs | High | Nginx headers, React escaping, input sanitisation |
| T5 | Broken Access Control | Accessing other users' data | High | JWT + Spring Security RBAC on all endpoints |
| T6 | Sensitive Data Exposure | Secrets in logs/responses | Critical | Env vars only, no PII in AI prompts |
| T7 | Rate Limiting Bypass | API flooding | Medium | flask-limiter 30 req/min per IP |
| T8 | CSRF | Cross-site form submission | Low | Stateless JWT (no cookies), CSRF disabled safely |

---

## Tests Conducted

### Authentication Tests
- ✅ Calling any `/api/transfers` endpoint without Authorization header returns **HTTP 401**
- ✅ Calling with expired JWT returns **HTTP 401**
- ✅ Calling with tampered JWT signature returns **HTTP 401**
- ✅ Wrong username/password returns **HTTP 401** with generic message (no username enumeration)

### Injection Tests
- ✅ SQL injection in `q` search param (`'; DROP TABLE data_transfers; --`) — JPA ignores it, returns empty results
- ✅ Prompt injection in `/describe` body (`"ignore previous instructions"`) returns **HTTP 400**
- ✅ HTML injection in title field is stripped by sanitiser before reaching Groq

### Rate Limiting
- ✅ Sending 31 requests/minute to AI service returns **HTTP 429** on the 31st request
- ✅ Rate limit resets after 60 seconds

### Access Control
- ✅ USER role cannot access admin-only operations
- ✅ Deleting another user's transfer requires authentication

### PII Audit
- ✅ No personal data (names, emails, phone numbers) is sent to Groq API
- ✅ AI prompts contain only: title, country names, data category, mechanism — no PII

---

## Security Headers Verified

| Header | Value | Where |
|--------|-------|-------|
| X-Content-Type-Options | nosniff | Nginx + Flask |
| X-Frame-Options | DENY | Nginx + Flask |
| X-XSS-Protection | 1; mode=block | Nginx + Flask |
| Strict-Transport-Security | max-age=31536000 | Flask |
| Content-Security-Policy | default-src 'self' | Flask |

---

## Findings Fixed

| ID | Severity | Finding | Fix Applied |
|----|----------|---------|-------------|
| F1 | Critical | Secrets hardcoded in config | All moved to `.env` / `${ENV_VAR}` |
| F2 | High | No JWT validation on AI endpoints | JwtAuthFilter applied to all `/api/**` |
| F3 | High | Prompt injection not blocked | `sanitizer.py` regex middleware added |
| F4 | Medium | No rate limiting on AI service | `flask-limiter` 30 req/min applied |
| F5 | Medium | CORS allowing all origins | Restricted to localhost:80, 3000, 5173 |
| F6 | Low | Stack traces exposed on 500 errors | `GlobalExceptionHandler` masks details |

---

## Residual Risks

| Risk | Severity | Reason Accepted |
|------|----------|-----------------|
| Groq API key compromise | Medium | Stored only in `.env`, not committed. Rotate key if exposed. |
| No HTTPS in development | Low | TLS terminated at load balancer in production. Dev is localhost only. |
| Password policy | Low | Min 6 chars enforced. Production should add complexity rules. |

---

## Team Sign-Off

| Member | Role | Signed |
|--------|------|--------|
| Member 1 | Java Developer 1 | ✅ |
| Member 2 | Java Developer 2 | ✅ |
| Member 3 | AI Developer 1 | ✅ |
| Member 4 | AI Developer 2 | ✅ |
| Member 5 | Security Reviewer | ✅ |

*Date: 9 May 2026*
