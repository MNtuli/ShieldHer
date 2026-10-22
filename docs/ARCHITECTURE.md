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

