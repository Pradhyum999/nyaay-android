# NYAAYNEETI — QA Audit & Fix Plan

> Static code audit of every feature (positive + negative paths). Each finding cites the exact
> file and behavior. Severity: 🔴 Critical · 🟠 High · 🟡 Medium · ⚪ Low.

---

## 1. 🔴 Critical — Signup/Login is broken for RETURNING users (your #1 complaint)

### Bug 1.1 — Role is overwritten to "Citizen" on every login
**File:** `data/repository/AuthRepository.kt` — `verifyOtp()` and `signInWithGoogle()`

```kotlin
val existing = userDao.getByPhone(full)
val user = existing?.copy(role = chosenRole, ...)   // ⚠️ overwrites role!
```

Since the flow was changed to **Login → Choose role → Onboarding**, `chosenRole` is the default
`CITIZEN` at login time (role selection happens *after* OTP).

**What the app shows:** a returning Firm Admin / Lawyer logs in and is **sent to the Citizen home**
and their role is silently reset to CITIZEN. A returning Student becomes a Citizen.

**Fix:** preserve the existing role. Only set role for brand-new users:
```kotlin
val user = existing?.copy(language = _selectedLanguage.value)   // do NOT touch role
    ?: UserEntity(..., role = chosenRole, ...)                  // new user only
```

### Bug 1.2 — "Sign Up" vs "Sign In" toggle does nothing
**File:** `ui/screens/auth/AuthScreens2.kt`. Both tabs run the identical phone flow. `isSignUp` only changes the label. **Fix:** make "Sign In" skip onboarding for known users, or remove the toggle.

### Bug 1.3 — Google Sign-In uses deprecated API + can silently fail
**File:** `AuthScreens2.kt` (deprecated `GoogleSignIn`/`GoogleSignInOptions`). If Google provider / web-client-id isn't enabled, the token is null → "Enable Google Sign-In" with no guidance. **Fix:** migrate to Credential Manager, or surface a clear setup message.

### Bug 1.4 — "Auto-fill 123456" debug button ships to production
**File:** `AuthScreens2.kt` (~line 137) — hardcoded OTP bypass. **Fix:** gate behind `BuildConfig.DEBUG`.

---

## 2. 🟠 High — Data not synced (two-device gaps)

### Bug 2.1 — Verification requests / reports / blocks are Room-only
**Files:** `VerificationRepository.kt`, `ModerationRepository.kt` — no `CloudSync`, no `push*`.
A lawyer's KYC request on device A is **invisible** to the Platform Admin on device B.

### Bug 2.2 — Verification approve/reject doesn't push the updated user

---

## 4. Positive / Negative test matrix (what the code actually does)

| Flow | Positive | Negative | Status |
|---|---|---|---|
| Phone OTP | 10-digit → OTP → role → onboarding | 9-digit (button disabled) · wrong OTP → "Invalid OTP" | ✅ works |
| Google login | token → user created | no token → "Enable Google Sign-In" | 🟡 deprecated |
| **Returning login** | — | — | 🔴 **role reset** (Bug 1.1) |
| Firm onboarding | firm name → "Create firm" enabled | empty name → button disabled | ✅ |
| Member onboarding | email matches invitation → linked | email mismatch → **silently not linked** | 🟡 |
| Build My Case | facts → case + tasks | empty description → input disabled | ✅ |
| Case add (lawyer) | fields → case | duplicate CNR → error | ✅ |
| Hearing add | date/time → hearing | duplicate date/time → error | ✅ |
| Invoice | fees → invoice | negative fee → accepted (no validation) | 🟡 |
| Engage lawyer | subject+case → inquiry | empty message → **accepted** | 🟡 |
| Inquiry accept | → assigns case + opens chat | — | ✅ (just fixed) |
| Consultation | request → ₹500 → PAID | empty notes → accepted | 🟡 |
| Internship apply | cover letter → applied | duplicate apply → blocked | ✅ |
| Forum post | title+body → post | empty → disabled | ✅ |
| Verification | submit → PENDING | duplicate pending → blocked | ✅ |
| Notification | tap → deep-links | — | ✅ (just fixed) |

---

## 5. Fix Plan (prioritized)

### P0 — fix login now (your #1 issue)
1. **`verifyOtp` + `signInWithGoogle`: stop overwriting `role` for existing users** (1-line change, critical).
2. Hide "Auto-fill 123456" behind `BuildConfig.DEBUG`.
3. Differentiate Sign Up / Sign In (or remove the toggle).

### P1 — close sync gaps
4. Add `CloudSync` to `VerificationRepository` + `ModerationRepository` and push on submit/approve/reject/report/block.
5. Push updated user on verification approve/reject.
6. Add `isDeleted = 0` to `observeByClient` (and audit other case queries).
7. Add `push` after `logOrder`/`verifyDocument` partial updates.

### P2 — UX polish
8. Auto-suggest lawyers on "Build My Case" completion (no more orphaned cases).
9. Validate empty message/notes on engage/consultation.
10. Add unread badge to chat inbox.
11. Editable lawyer profile.
12. Show a message when a firm-member email has no matching invitation.

**Start with P0 #1** — it's a one-line change that fixes the "login sends me to the wrong screen / resets my role" bug that's making the app feel broken.

`approve()/reject()` update `userDao` only — the lawyer's new `verificationStatus` never reaches Firestore, so the directory stays stale cross-device.

### Bug 2.3 — Soft-deleted cases still appear for the client
**File:** `data/local/Daos.kt` — `observeByClient` = `SELECT * FROM cases WHERE clientUid = :uid` (no `isDeleted = 0`). A case the lawyer "removed" still shows in the client's list.

### Bug 2.4 — Seed demo data is local-only
`AppContainer.seedIfEmpty()` writes directly to Room (not Firestore). Demo lawyers/cases don't sync until the demo account logs in via OTP (which pushes the user).

---

## 3. 🟡 Medium — UX / flow gaps

| # | Feature | Issue | Fix |
|---|---|---|---|
| 3.1 | Build My Case | Case is created **unassigned**; if the citizen never manually "Engages" a lawyer, no lawyer ever sees it | Auto-suggest matched lawyers on creation |
| 3.2 | Engage lawyer | `engage()` accepts empty `message` (no validation) | Require non-blank subject/message |
| 3.3 | Consultation | `request()` allows empty `notes`; payment is **simulated** (`status=PAID` instantly) | Validate notes; add a "payment declined" path |
| 3.4 | Chat | No unread badge on the conversation list | Add unread count to inbox rows |
| 3.5 | Profile | Lawyer can't edit fees/languages/practice areas in-app | Add editable profile screen |
| 3.6 | Court diary | `logOrder`/`verifyDocument` partial updates **without** pushing to Firestore | Add `push` after these |
