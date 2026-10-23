# ShieldHer API Contract (v1)

Source of truth for the front-end and back-end. **Change this file in a PR before changing behaviour.**

- Base URL (local): `http://localhost:8080`
- Format: JSON, UTF-8. Header: `Content-Type: application/json`
- Auth: `Authorization: Bearer <token>` on every endpoint except those marked **public**
- Timestamps: ISO-8601 in UTC, e.g. `2026-10-23T19:10:18Z`. The UI formats them in the user's local time.
- Every contact and alert belongs to the logged-in user. Another user's id returns `404`.

## Error format (all endpoints)

```json
{
  "timestamp": "2026-10-23T19:10:49.778Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": { "phone": "Phone number is required" }
}
```

`fieldErrors` only appears on validation errors. The UI shows `message` and never invents its own success text.

| Status | When |
|---|---|
| 400 | Validation failed or bad input |
| 401 | Bad credentials, or missing/invalid/expired token |
| 404 | Resource not found, or belongs to someone else |
| 500 | Unexpected server error (generic message, no internals) |

## Auth (public) - implemented

### POST /api/auth/register
Request
```json
{ "fullName": "Test User", "email": "test@shieldher.co.za", "password": "secret123", "phoneNumber": "0821234567" }
```
Response `201`
```json
{ "token": "<jwt>", "tokenType": "Bearer", "userId": 1, "fullName": "Test User", "email": "test@shieldher.co.za", "role": "ROLE_USER" }
```

### POST /api/auth/login
Request `{ "email": "...", "password": "..." }` - Response `200`: same shape as register. Bad credentials: `401`.

## Contacts

Request body (POST and PUT)
```json
{ "name": "Thandi", "phone": "0829876543", "email": "thandi@example.com", "relationship": "Sister" }
```
Rules: `name` and `phone` required; `email` and `relationship` optional.

Response body (contact)
```json
{ "id": 7, "name": "Thandi", "phone": "0829876543", "email": "thandi@example.com", "relationship": "Sister" }
```

| Method | Path | Success | Notes |
|---|---|---|---|
| GET | /api/contacts | 200 `[contact, ...]` | Only my contacts |
| POST | /api/contacts | 201 `contact` | |
| PUT | /api/contacts/{id} | 200 `contact` | 404 if not mine |
| DELETE | /api/contacts/{id} | 204 no body | 404 if not mine |

## Alerts (SOS)

### POST /api/alerts
Request (all fields optional; send lat and lng together or not at all)
```json
{ "message": "I feel unsafe, please call me.", "latitude": -26.2041, "longitude": 28.0473 }
```
Rules: `message` max 500 chars (a default message is used if empty); `latitude` -90..90; `longitude` -180..180. Location is only sent if the user allowed it in the browser.

Response `201`
```json
{
  "id": 12,
  "message": "I feel unsafe, please call me.",
  "latitude": -26.2041,
  "longitude": 28.0473,
  "status": "SENT",
  "deliveryMode": "SIMULATED",
  "contactsTotal": 3,
  "contactsNotified": 3,
  "createdAt": "2026-10-23T19:10:18Z"
}
```

### GET /api/alerts
Response `200`: `[alert, ...]`, newest first, only my alerts.

### Alert statuses (exactly these four)

| Status | Meaning | UI must say |
|---|---|---|
| PENDING | Saved, delivery not finished | "Sending..." |
| SENT | Delivered to at least one contact | "Alert sent to X of Y contacts" |
| FAILED | Saved, but delivery failed | "Could not send. Call emergency services." |
| NO_CONTACTS | Saved, user has no trusted contacts | "No contacts to notify. Add a contact." |

### Delivery mode
`deliveryMode` is `SIMULATED` (message logged, nobody actually contacted) or `REAL`. When `SIMULATED`, the UI **must** show: "Demo mode: no real message was sent." The UI shows the result from the response status, never an assumed success.

## Support resources (public)

### GET /api/resources?category=HELPLINE
`category` optional: `HELPLINE`, `REPORTING`, `SAFETY_TIP`.
Response `200`
```json
[ { "id": 1, "title": "GBV Command Centre", "description": "24/7 support", "category": "HELPLINE", "phone": "0800428428", "url": null } ]
```
Verify all numbers before the demo.

## MVP screens

| Screen | File | API calls | Must show |
|---|---|---|---|
| 1. Dashboard (SOS) | dashboard.html + sos.js | POST /api/alerts, GET /api/alerts | Big SOS button, location consent prompt, truthful result per status, recent alerts |
| 2. Contacts | contacts.html + contacts.js | GET/POST/PUT/DELETE /api/contacts | List, add/edit form, delete with confirm, validation errors from `fieldErrors` |
| 3. Support hub | resources.html + resources.js | GET /api/resources | Helplines (tap to call), reporting steps, safety tips |

Login and register pages are entry screens and are not counted as MVP screens. A logged-out user visiting screens 1 or 2 is redirected to login. Token is kept in `localStorage` under `shieldher_token`; on any `401` the UI clears it and redirects to login.