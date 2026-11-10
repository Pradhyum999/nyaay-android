# NYAAY Android — Feature Completion Plan (Parity + Beyond)

> **Goal:** Make the Android app feature-complete for **every** persona — Citizen, Independent
> Lawyer, Firm Admin, Firm Member, Student, and Platform Admin — matching the web app's
> functionality **and** filling gaps the web app itself has. UI/UX stays native Compose (Obsidian/Gold
> theme); we only borrow *behavior*, never the web design.
>
> **How to use:** Implement top-to-bottom in Section 7 order. Each item is a self-contained task.

---

## 1. Current State vs Target (one-paragraph summary)

**Android today:** 5 roles, WhatsApp-style chat + call screen, basic Case Room (summary/timeline/
tasks/notes/docs), lawyer directory (name/city/specialization search only), consultations
(book→accept→meeting-link→review), internships, forum (posts/votes/comments), firm management
(invite/remove/assign role/assign case), notifications, subscriptions, payments (simulated),
rule-based + NVIDIA NIM AI intake, eCourts client (stub), Firebase (real Phone Auth + Firestore
write-through + security rules).

**Target:** all of the above **plus** the web app's depth (directory matching, verification + admin,
billing/invoices, court diary + calendar, eCourts lookup, Ask My Case, lawyer AI, IPC→BNS, full
trilingual i18n, firm provisioning) **plus** new completeness features (escrow payments, hearing
reminders, consultation time slots, document analysis, public Q&A, data export, report/block).

---

## 2. Persona-by-Persona Completeness

Legend: ✅ done · 🟡 partial · ❌ missing · ⭐ new (neither app has it)

### 2.1 Citizen / Litigant
- ✅ Phone OTP, role onboarding, Build My Case (AI intake) → Case Room, chat + calls, forum,
  consultations, notifications, payments history.
- 🟡 Lawyer directory (search only; no filters/matching/engage). Case Room (no invoices, no
  document verification status, no eCourts sync). Chat (not case-linked, no accept/decline badge).
- ❌ Accept/Decline engagement lifecycle + "attach AI brief" on engage. Ask My Case (grounded Q&A).
  Pay invoices. Feedback/rating with voice dictation. Emergency helpline. Legal templates. Help.
  Delete account.
- ⭐ Escrow for consultation fee (released when lawyer completes). Hearing reminders (push).
  Public "Ask a lawyer free" Q&A. Report/block a lawyer. Document upload with verification tick.

### 2.2 Independent Lawyer
- ✅ KYC status field, verification banner, cases (assigned), court diary list, chat/calls,
  consultations, forum, profile, subscription.
- 🟡 Case management (stage/priority only; no add-case, no delete, no hearings form, no invoices).
  Discovery profile (few fields).
- ❌ Add Case form (CNR/case-number validation + duplicate prevention). Hearings (add/edit, auto
  item number, judge, order notes, duplicate date/time prevention). Log order + **summarize order**.
  Billing: invoices (appearance/drafting/clerkage), mark paid UPI/bank/cash. **Lawyer AI counsel**
  (research assistant). IPC→BNS. Share case/diary with juniors. Public case portfolio (eCourts).
  Rich profile (AIBE cert, bar association, practice courts, education, languages, fee range, geo).
- ⭐ Real payment collection (Razorpay/UPI) with receipts. Hearing reminders + cause-list sync from
  eCourts. Analytics (earnings, case load). Client rating is BCI-safe (shown as "verified profile
  matches matter", not "best lawyer").

### 2.3 Firm Admin
- ✅ Firm create, invite/remove members, assign roles, assign case, internships (post/apply/status),
  subscription.
- 🟡 No firm analytics, no firm-wide calendar, no member password flow.
- ❌ Firm/clinic/college registration types. Member provisioning with **temp password + forced
  reset on first login**. Firm-wide cause list + month calendar. Firm billing/analytics. Case
  reassignment history.
- ⭐ Seat-based subscription limits enforced (free = N members). Firm approval workflow for
  joining. Member activity audit log.

### 2.4 Firm Member (junior/associate/paralegal/student)
- ✅ Assigned-cases view (RBAC), case room, chat, forum.
- ❌ Case-linked collaboration (notes/tasks scoped to assignment). Diary subset. Document
  checklist for assigned cases. Role-based permissions (paralegal can't edit billing, etc.).
- ⭐ Task assignment with due dates + reminders. Time tracking on tasks (for billing).

### 2.5 Student / Intern
- ✅ Profile, browse/apply internships, track applications, forum.
- 🟡 No internship search/filter (location, stipend, remote, domain). No resume upload.
- ❌ Resume upload + skills tags. Saved internships. Firm "applications" shortlist flow (partial).
- ⭐ Internship calendar/deadline reminders. Mentorship/learning feed (templates, IPC→BNS,
  judgment digests). Student portfolio (public).

### 2.6 Platform Admin (new role)
- ❌ Entire role missing.
- ❌ Verification dashboard (approve/reject lawyer KYC, audit trail). Forum moderation.
  Citizen feedback management. Platform analytics. Announcements.
- ⭐ Content moderation queue. Reported-content review. Ban/suspend users.

---

## 3. Parity Gap Map (web → Android)

| Web feature | Android status |
|---|---|
| Lawyer directory (practice area / location / experience / language / fee filters) | ❌ only name/city/specialization |
| Objective lawyer matching (`matchLawyers` scoring + reasons) | ❌ |
| Engage advocate + attach AI brief + accept/decline (Vakalatnama badge) | 🟡 consultation only |
| Case Room full (timeline, tasks, notes, docs, hearings, invoices) | 🟡 no invoices/hearings UI |
| Add Case form (8-stage lifecycle, CNR validation, duplicate prevention) | ❌ |
| Hearings (form, auto item #, judge, order notes, duplicate prevention) | ❌ |
| Log order + summarize order (AI) | ❌ |
| Invoices/billing (fees, mark paid UPI/bank/cash) | ❌ |
| Document verification status + evidence upload modal | 🟡 upload only |
| Case status/priority/queue management | 🟡 |
| Delete/share case | ❌ |
| Verification (ID doc upload, masked ID, admin audit queue) | ❌ |
| Admin verification dashboard | ❌ |
| eCourts (CNR lookup, search by advocate/case, public cases, auto-verify) | 🟡 client stub only |
| Ask My Case + summarize order + prepared packet + lawyer AI counsel | ❌ |
| IPC → BNS conversion | ❌ |
| Court analytics & forum (judicial analytics) | 🟡 forum only |
| Inquiries inbox (new inquiry → accepted/declined) | ❌ |
| Rich lawyer profile (AIBE, bar assoc, practice courts, education, languages, fee range, public cases, geolocation) | ❌ |
| Legal templates / Emergency helpline / Help / Feedback (voice) | ❌ |
| Full EN/HI/MR translation of all strings | ❌ (enum only) |
| Firm member temp-password + forced reset | ❌ |
| Delete account | ❌ |
| Read receipts + case-linked chat + pinned | 🟡 unread dot only |
| Real Phone Auth + Firestore write-through + security rules | ✅ (ahead of web) |

## 4. New Features Beyond the Web (completeness ideas)

> These make the app genuinely complete and are missing from *both* apps.

### Trust & safety
1. **Escrow consultations** — client pays upfront, held in escrow, released to lawyer on
   "Mark completed"; auto-refund on cancel/decline. (`consultation.paymentStatus` already exists.)
2. **Report & block** — client/lawyer can report the other; admin reviews; block prevents chat.
3. **Verification ticks on documents** — lawyer/client marks a doc "verified"; shown as a tick in
   the Case Room checklist.

### Convenience
4. **Hearing reminders** — push notification N days + day-of before a hearing (needs scheduling
   + FCM, both already wired).
5. **Consultation time slots** — lawyer sets weekly availability; client books a slot (not just
   "request now").
6. **Voice dictation** — speech-to-text for intake, notes, and feedback (Hindi/Marathi friendly).
7. **Offline-first** — already true via Room; keep Firestore as sync layer (already built).

### AI depth
8. **Ask My Case** — chat grounded on the case workspace (facts, timeline, docs, hearings).
9. **Summarize order** — paste/scan a court order → plain-language summary (EN/HI/MR).
10. **Document analysis** — upload PDF → summary + extracted key dates/parties.
11. **Lawyer AI counsel** — private research assistant for advocates (case law, sections).

### Business
12. **Real payment gateway** (Razorpay/UPI) with receipts — replaces simulated payments.
13. **Consultation commission** — configurable platform % on each paid consultation.
14. **Subscription enforcement** — free tier limits (cases, seats, storage) actually gate actions.
15. **Referral code** for lawyers/clients.

### Engagement
16. **Public Q&A** ("Ask a lawyer free") — separate from private engagement; BCI-safe.
17. **Judgment/legal-news digest** — curated feed in the forum.
18. **Data export & account deletion** (DPDP / GDPR-style) — export JSON, hard-delete.

### Compliance
19. **BCI-safe positioning** — objective "verified profile matches your matter" + disclaimer
    (never "best/cheapest lawyer").
20. **Full trilingual UI** (EN/HI/MR) with runtime toggle (already scaffolded).

---

## 5. Data Model Additions (extend existing Room entities)

> Keep JSON-in-string for list fields (pattern already used). Enums stored via `Converters`.

### 5.1 Extend `UserEntity`
`aibeCertificateNo`, `barAssociation`, `practiceCourts: String` (JSON list), `education: String`,
`languages: String`, `feeMin: Double`, `feeMax: Double`, `practiceType` (INDEPENDENT/FIRM/CHAMBERS),
`chambersAddress`, `chamberLat`, `chamberLng`, `idDocumentType`, `idDocumentUrl`,
`idDocumentNumberMasked`, `rejectionReason`, `verifiedAt`, `verifiedBy`.

### 5.2 Extend `CaseEntity`
`cnrNumber`, `filingNumber`, `filingDate`, `judge`, `orderNotes`, `queuePosition: Int`,
`isDeleted: Boolean`, `sharePermissions: String` (JSON).

### 5.3 Extend `HearingEntity`
`judgeName`, `orderNotes`, `status` (UPCOMING/COMPLETED/ADJOURNED), `purposeEn`, `purposeHi`.

### 5.4 Extend `DocumentEntity`
`status` (PENDING/VERIFIED), `uploadedBy` ("lawyer"|"client").

### 5.5 Extend `ConsultationEntity`
`releasedAt: Long?`, `refundedAt: Long?` (for escrow).

### 5.6 New entities
```kotlin
InvoiceEntity(id, caseId, lawyerId, clientId, invoiceNumber, caseNumber, clientName, date,
    appearanceFee, draftingFee, clerkageMisc, totalAmount, status, paidVia, upiRef, notes, createdAt)

VerificationRequestEntity(id, uid, role, name, documentType, documentUrl, maskedIdNumber,
    status, submittedAt, rejectionReason, verifiedBy, verifiedAt, auditTrailJson)

InquiryEntity(id, lawyerId, clientId, clientName, subject, message, status, createdAt)

FeedbackEntity(id, userId, userName, rating, feedbackText, voiceAudioUrl, status, createdAt)

ReportEntity(id, reporterUid, targetUid, targetName, reason, status, createdAt)

AnnouncementEntity(id, title, body, isPinned, createdAt)

PublicCaseEntity(id, lawyerUid, cnrNumber, caseNumber, title, court, year, stage, sourceType,
    showOnProfile, ecourtsUrl, advocateNotes)

JudicialAnalyticEntity(id, judgeName, court, dispositionTrend, avgHearingIntervalDays,
    adjournmentFrequency, keyObservation)

TemplateEntity(id, title, category, content, updatedAt)

LawyerAvailabilityEntity(id, lawyerUid, dayOfWeek, startTime, endTime)
```

### 5.7 New DAO queries
- `UserDao.observeDirectory()` → verified lawyers with public fields (for matching).
- `CaseDao` duplicate check: `SELECT COUNT(*) WHERE caseNumber = :n`.
- `HearingDao` duplicate check: `SELECT COUNT(*) WHERE lawyerId = :id AND hearingDate = :d AND hearingTime = :t`.
- `HearingDao.observeByMonth(start, end)` for calendar.
- `InvoiceDao.observeByCase / observeForLawyer`.
- `InquiryDao.observeForLawyer`.
- `VerificationDao.observePending` (admin).
- `ReportDao.observeOpen` (admin).

---

## 6. Screen & Route Additions

| Area | New screens/routes |
|---|---|
| Directory | Upgrade `LawyerDiscoveryScreen` (filters: practice area, city, experience, language, fee) + `matchLawyers` scoring + `EngageAdvocateSheet` (subject + attach AI brief + confirm) |
| Connection | Accept/Decline banner in `ChatThreadScreen` (Vakalatnama badge); case-linked chat (`chat` tab scoped to case) |
| Case mgmt | `NewCaseSheet` (8-stage lifecycle, CNR validation + duplicate check + eCourts lookup), `HearingFormSheet` (auto item #, judge, order notes, duplicate prevention), `HearingCalendar` (month grid), `LogOrderSheet` + `SummarizeOrder`, delete/archive case, `ShareCaseSheet` |
| Billing | `InvoiceListScreen`, `NewInvoiceSheet`, `MarkPaidSheet` (UPI/bank/cash) |
| Verification | `VerificationUploadScreen` (ID doc + Bar Council), admin `VerificationDashboardScreen` (approve/reject + audit trail) |
| eCourts | `ECourtsLookupScreen` (CNR search + result), "Verify case" button in Case Room |
| AI | `AskMyCasePanel` (in Case Room), `LawyerAICounselScreen`, `IpcToBnsScreen`, `DocumentAnalysis` (upload PDF → summary) |
| Discovery | `LawyerProfileScreen` enrich (public cases, fee range, languages, geo map) |
| Firm | `FirmRegistrationScreen` (firm/clinic/college), `MemberPasswordResetScreen` (forced on first login) |
| Admin (new role) | `AdminHomeScreen` (tabs: Verifications, Moderation, Feedback, Announcements, Analytics), `ModerationScreen` |
| Feedback | `FeedbackSheet` (star + voice dictation) |
| Inquiries | `InboxPage`/`InquiryCard` (new inquiry → accepted/declined) |
| Help/resource | `HelpPage`, `LegalTemplatesScreen`, `EmergencySheet` (helpline) |
| Account | `DeleteAccountScreen`, `DataExportScreen`, `ReportUserSheet`, `BlockListScreen` |
| Availability | `AvailabilityScreen` (lawyer weekly time slots) + bookable slots in `BookConsultation` |
| Engagement | Public Q&A forum category, Judgment/news digest feed |

---

## 7. Phased Implementation Plan (ordered)

> Each phase ends with **compile + tests green**. Build in this order; UI is native Compose,
> behavior-only parity.

### Phase A — Lawyer Discovery & Connection (your #1 ask)
1. Extend `UserEntity` with rich lawyer fields (practice areas, languages, fee range, education,
   bar association, AIBE, practice courts, geo) + onboarding fields.
2. Add `UserDao.observeDirectory()` (verified + public fields).
3. Rebuild `LawyerDiscoveryScreen`: filters (practice area, city, experience, language, fee range) +
   sort by score/experience.
4. Port `matchLawyers` scoring + show "why relevant" reasons (BCI-safe, never "best lawyer").
5. `EngageAdvocateSheet`: matter subject + "attach AI brief" → creates engagement + opens case-linked chat.
6. Accept/Decline lifecycle: `InquiryEntity` + banner in `ChatThreadScreen` + Vakalatnama badge +
   chat scoped to case.

### Phase B — Case Management (lawyer's core)
7. `NewCaseSheet`: 8-stage lifecycle, CNR/case-number validation, duplicate prevention, optional
   eCourts verify.
8. `HearingFormSheet`: auto item #, judge, order notes, duplicate date/time prevention; add/edit.
9. `HearingCalendar` (month grid) + upgraded `CauseListScreen`.
10. `LogOrderSheet` + summarize-order button (AI).
11. `InvoiceEntity` + invoice list/create/mark-paid (UPI/bank/cash).
12. Delete/archive case, `ShareCaseSheet`, document verification status ticks.

### Phase C — Trust: Verification & Admin
13. `VerificationRequestEntity` + `VerificationUploadScreen` (ID + Bar Council doc, masked ID).
14. Add `UserRole.PLATFORM_ADMIN` + `AdminHomeScreen` (Verifications, Moderation, Feedback,
    Announcements, Analytics) + audit trail.
15. Report/block users + moderation queue + citizen feedback dashboard.

### Phase D — eCourts & AI depth
16. Wire `ECourtsClient` into `ECourtsLookupScreen` (CNR search) + "Verify case" in Case Room +
    public cases on lawyer profile (from eCourts).
17. `AskMyCasePanel` (grounded on case workspace).
18. `SummarizeOrder` + `DocumentAnalysis` (PDF → summary) + `LawyerAICounselScreen` + `IpcToBnsScreen`.

### Phase E — Business & completeness
19. Escrow consultations + real payment gateway (Razorpay/UPI) + configurable commission.
20. `LawyerAvailabilityEntity` + bookable consultation time slots.
21. Hearing reminders (FCM push N days + day-of).
22. Subscription enforcement (free-tier case/seat/storage limits actually gate actions).
23. `FeedbackSheet` (star + voice), `HelpPage`, `LegalTemplatesScreen`, `EmergencySheet`.
24. Public Q&A forum category + judgment/news digest.
25. `DeleteAccountScreen`, `DataExportScreen`, full EN/HI/MR i18n.

### Phase F — Firm & student polish
26. Firm registration types (firm/clinic/college), member temp-password + forced reset.
27. Member role-based permissions (paralegal can't edit billing; intern read-only).
28. Internship search/filter + resume upload + deadline reminders + student portfolio.

---

## 8. Definition of Done (whole app)

1. Every web-app capability above is present **and** every ⭐ "new" feature is implemented.
2. Every list has loading / empty / error states; ≤3 taps from role home; destructive actions confirmed.
3. RBAC enforced in repositories (firm member sees only assigned; admin sees all; client sees own).
4. All mutations write to Room **and** Firestore (write-through) + correct notification/timeline.
5. Firestore rules updated to cover new collections (`invoices`, `verification_requests`,
   `inquiries`, `feedback`, `reports`, `announcements`, `public_cases`, `availability`).
6. BCI compliance: objective matching + disclaimer, no "best/cheapest lawyer" ranking.
7. Full trilingual UI (EN/HI/MR) with runtime toggle — zero hardcoded English in screens.
8. Data survives restart (Room) and syncs across devices (Firestore).
9. `./gradlew testDebugUnitTest` green; APK assembles.

---

## 9. Effort / priority summary

| Priority | Theme | Why |
|---|---|---|
| **P0** | Phase A (discovery + connection) | directly what you flagged; unlock the core client↔lawyer loop |
| **P0** | Phase B (case management) | "lawyer can add/remove cases, hearings, billing" — core value |
| **P1** | Phase C (verification + admin) | trust + compliance, monetization enabler |
| **P1** | Phase D (eCourts + AI) | differentiation + "operating system" depth |
| **P2** | Phase E (business) | payments/escrow/slots/reminders — revenue |
| **P2** | Phase F (firm/student polish) | completeness for institutional + student personas |

> Start with **Phase A**, then **B** — together they deliver the exact "search lawyer by location/
> experience, reach out, lawyer manages cases" loop you asked about, in a native, clean UX.



