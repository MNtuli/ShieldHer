# ShieldHer 🛡️

> **Protecting her voice. Securing her digital world.**
> Making digital spaces safe, not scary.

FNB Hackathon submission | Team: `<team name>` | Members: `<names>`

---

## Why ShieldHer

Imagine a young woman who opens her phone every morning to share her dreams and connect with friends. Then the messages start: cruel comments, threats, her private information shared without consent, all from anonymous accounts. She dreads every notification, withdraws from the people she loves, and begins to feel unsafe even at home.

For many survivors, online harassment escalates into stalking, physical threats and real-world violence, with devastating effects on mental health. Gender-based violence doesn't always begin with a fist. Sometimes it begins behind a screen.

As young software developers, we asked: **what if technology could help protect people from the harm it sometimes enables?** ShieldHer is our answer.

---

## 1. Problem Statement

Gender-based violence (GBV) doesn't always start with a punch. Often it starts behind a screen: a cruel comment, a threatening message, a private detail shared without consent, an anonymous account that won't stop.

Online harassment can escalate into stalking, physical threats and real-world violence. Survivors often experience anxiety, isolation, depression and, in severe cases, suicidal thoughts. Many don't know where to report, who to call, or how to ask for help quickly and quietly when they feel unsafe.

**The gap:** there is no simple, stress-free tool that lets someone facing online harassment (1) alert people they trust in seconds, (2) keep emergency contacts ready, and (3) understand safe reporting options, all in one place.

## 2. Overview

ShieldHer is a web app that helps people facing online harassment find quick support, keep trusted emergency contacts, and learn about safe reporting options without stress.

We don't promise perfect protection. We provide **practical tools, clear information, and a path to real help**, so users don't feel alone.

**Core features (MVP)**

| Feature | Description |
|---|---|
| SOS button | One large, always-visible button that triggers an emergency alert |
| Trusted contacts | Add, edit and remove emergency contacts |
| Emergency alert | Sends the user's message (and location, if permitted) to all trusted contacts |
| Safe reporting guide | Clear steps, helplines and links for reporting harassment and getting support |

**Stretch goals:** incident/evidence log, quick-exit button, discreet mode, SMS delivery, multi-language support.

## 3. Solution

1. **User registers/logs in** securely (JWT).
2. **Adds trusted contacts** (name, phone, email, relationship).
3. **Presses SOS** when feeling unsafe. The browser captures location (optional, with consent).
4. **Backend creates an alert record** and passes it to the notification service, which messages every trusted contact.
5. **User browses the support hub** for helplines, reporting steps and safety tips.

**Design principles:** accessible, minimal clicks, calm tone, mobile-first, privacy by default (no sensitive data in logs, hashed passwords, minimal data collected).

## 4. Tech Stack

| Layer | Technology |
|---|---|
| Back-end | Java 17+, Spring Boot (Web, Data JPA, Security, Validation) |
| Front-end | HTML, CSS, JavaScript (served from Spring Boot `static/`) |
| Database | PostgreSQL |
| Design | Figma |
| Collaboration | Git & GitHub |

## 5. Architecture

```
┌──────────────────────────────────────────────┐
│                    CLIENT                    │
│     HTML + CSS + JavaScript (mobile-first)   │
│  Login • Dashboard (SOS) • Contacts • Support│
└──────────────────────┬───────────────────────┘
                       │ REST / JSON (HTTPS) + JWT
┌──────────────────────▼───────────────────────┐
│               SPRING BOOT API                │
│                                              │
│  Security Filter (JWT)                       │
│          │                                   │
│  Controllers ── Auth │ Contact │ Alert │ Resource
│          │                                   │
│  Services ───── AuthService  ContactService  │
│                 AlertService ResourceService │
│                       │                      │
│              NotificationService ──► Email / SMS provider
│          │                                   │
│  Repositories (Spring Data JPA)              │
└──────────────────────┬───────────────────────┘
                       │ JDBC
┌──────────────────────▼───────────────────────┐
│                  PostgreSQL                  │
│  users • emergency_contacts • alerts •       │
│  support_resources                           │
└──────────────────────────────────────────────┘
```

**SOS flow**

```
User taps SOS → sos.js gets location → POST /api/alerts
  → AlertService saves Alert
  → ContactService loads user's contacts
  → NotificationService sends message to each contact
  → Alert status updated (SENT / FAILED) → UI confirmation
```

### Data model

| Table | Key fields |
|---|---|
| `users` | id, full_name, email (unique), phone, password_hash, created_at |
| `emergency_contacts` | id, user_id (FK), name, phone, email, relationship |
| `alerts` | id, user_id (FK), message, latitude, longitude, status, created_at |
| `support_resources` | id, title, description, category, phone, url |

### REST API

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Create account |
| POST | `/api/auth/login` | Login, returns JWT |
| GET | `/api/contacts` | List my contacts |
| POST | `/api/contacts` | Add contact |
| PUT | `/api/contacts/{id}` | Update contact |
| DELETE | `/api/contacts/{id}` | Remove contact |
| POST | `/api/alerts` | Trigger SOS alert |
| GET | `/api/alerts` | My alert history |
| GET | `/api/resources` | Support & reporting info (public) |

## 6. Project Structure

```
shieldher/
├── pom.xml
├── README.md
├── docs/                       # Figma exports, diagrams
└── src/
    ├── main/
    │   ├── java/za/co/shieldher/
    │   │   ├── ShieldHerApplication.java
    │   │   ├── config/         SecurityConfig
    │   │   ├── security/       JwtUtil, JwtAuthFilter
    │   │   ├── controller/     Auth, Contact, Alert, Resource
    │   │   ├── service/        Auth, Contact, Alert, Resource, NotificationService
    │   │   ├── repository/     User, EmergencyContact, Alert, SupportResource
    │   │   ├── model/          User, EmergencyContact, Alert, SupportResource
    │   │   ├── dto/            request/response objects
    │   │   └── exception/      GlobalExceptionHandler, custom exceptions
    │   └── resources/
    │       ├── application.yml
    │       ├── data.sql        # seed support resources
    │       └── static/
    │           ├── index.html, login.html, register.html
    │           ├── dashboard.html, contacts.html, resources.html
    │           ├── css/style.css
    │           └── js/ api.js, auth.js, sos.js, contacts.js, resources.js
    └── test/java/za/co/shieldher/
```

## 7. Getting Started

**Prerequisites:** Java 17+ (JDK 21 recommended), PostgreSQL 16. Maven is bundled (`mvnw`).

**1. Create the database** (Windows PowerShell; use your own `postgres` password)

```powershell
$env:PGPASSWORD = '<your postgres password>'
& "C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -c "CREATE USER shieldher WITH PASSWORD 'shieldher';"
& "C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -c "CREATE DATABASE shieldher OWNER shieldher;"
```

Linux/macOS: `sudo -u postgres psql -c "CREATE USER shieldher WITH PASSWORD 'shieldher';"` and `sudo -u postgres psql -c "CREATE DATABASE shieldher OWNER shieldher;"`

**2. Create your `.env`** (never commit it)

```powershell
Copy-Item .env.example .env      # Linux/macOS: cp .env.example .env
```

Then set `JWT_SECRET` to a Base64 string of at least 32 bytes. Generate one:

```powershell
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Maximum 256 }) -as [byte[]])
```

Spring loads `.env` automatically (`spring.config.import`), so run from the project root.

**3. Run**

```powershell
.\mvnw.cmd spring-boot:run       # Linux/macOS: ./mvnw spring-boot:run
```

App: http://localhost:8080

**4. Smoke test**

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/api/auth/register -ContentType "application/json" `
  -Body '{"fullName":"Test User","email":"test@shieldher.co.za","password":"secret123","phoneNumber":"0821234567"}'
```

You should get back a `token`.

**Troubleshooting**

| Symptom | Fix |
|---|---|
| `Connection refused` on 5432 | PostgreSQL is not running: `Get-Service *postgres*` |
| `Could not resolve placeholder 'JWT_SECRET'` | `.env` missing or not in the project root |
| `TypeTag :: UNKNOWN` on build | Lombok too old for your JDK; keep `lombok.version` current in `pom.xml` |
| `password authentication failed` | Wrong DB password in `.env` |

## 8. Safety, Privacy & Limitations

- Passwords are hashed (BCrypt); APIs are protected by JWT.
- Location is shared only with the user's consent, and only inside an alert.
- ShieldHer **does not replace** emergency services. In immediate danger, contact SAPS (10111) or the GBV Command Centre (0800 428 428). *(Verify numbers before the demo.)*

## 9. Roadmap

- SMS/WhatsApp delivery, quick-exit / discreet mode
- Evidence log (screenshots, notes, timestamps)
- Partnerships with GBV organisations and financial-safety guidance

## 10. Team

| Name | Role |
|---|---|
| `<name>` | Back-end |
| `<name>` | Front-end |
| `<name>` | UI/UX (Figma) |
| `<name>` | Database / DevOps |


