# NYAAYNEETI (न्यायनीति) — Complete Multi-Role Platform Implementation Plan

> **Purpose of this document:** A single, self-contained, step-by-step specification that a
> **lower-tier coding model (e.g. Gemini 3.x Flash / 3.5 Flash)** can follow to rebuild
> NYAAYNEETI into a full-fledged, business-viable legal platform **without missing any feature**.
>
> **How to use this doc:** Implement **top-to-bottom in the order given** (Section 16 is the
> ordered build checklist). Every data field, screen, repository method, and acceptance
> criterion is written out explicitly so nothing needs to be inferred or invented.

---

## 1. Executive Summary

The current app is a **WebView shell around a bundled React PWA**, with a large amount of
**dead native Compose code** and **failing unit tests**. It is a demo, not a product:
- only 2 user types (Citizen, Advocate),
- no persistence (everything is lost on restart),
- fake OTP auth,
- mock AI intake (keyword matching),
- fake consultation (no real call),
- **no law-firm concept, no admin, no internships, no discussion forum, no payments**.

This plan converts it into a **local-first, offline-capable native Android app** with
**5 user roles**, a **law-firm workspace**, an **internship portal**, a **trending discussion
forum**, and a **business layer** (subscriptions, consultation fees, ratings).

The rebuild is structured so that **Phase A (MVP) is fully self-contained and testable on
device** (Room + seeded demo data), and **Phase B (Production) is a clean swap of the
repository layer to a real backend (Firebase Auth + Firestore)** — the repository interfaces
are identical, so Phase A code is never thrown away.

---

## 2. Current State Audit (what is actually in the repo)

### 2.1 What actually runs today
| File | Reality |
| :--- | :--- |
| `app/src/main/java/.../MainActivity.kt` | Loads a **WebView** pointing at `https://appassets.androidplatform.net/index.html` (a bundled React PWA). This is the **only** thing the user sees. |
| `app/src/main/assets/index.html` + `assets/index-DzRygkId.js` (2.6 MB) + `index-bOJd5tiU.css` (200 KB) | The real app: React + Tailwind v4 + Firebase (Auth/Firestore) + Cloudflare Worker proxy (`/api/ecourts`, `/api/nvidia`) + NVIDIA NIM/Gemini LLM + PWA service worker. |
| `app/src/main/java/.../ui/**`, `viewmodel/**`, `data/repository/**` | **Dead code.** `MainActivity` never instantiates `NyaayNavGraph` or any `ViewModel`. |
| `app/src/test/.../CaseRepositoryTest.kt` | **Will fail** (see 2.2). |

### 2.2 Critical bugs & issues (must fix)
1. **Dead Compose layer** — the entire `ui/` + `viewmodel/` + `repository/` tree is unreachable.
2. **Broken tests** — `CaseRepositoryTest` asserts seed data (`case-101`, task `t-1`, non-empty
   `hearings`) but `CaseRepository` initializes **empty lists**. `./gradlew testDebugUnitTest` fails.
3. **No persistence** — `AuthRepository`, `CaseRepository`, `GeminiIntakeRepository` hold
   everything in in-memory `MutableStateFlow`. All data vanishes on process death.
4. **Fake auth** — `verifyOtp` accepts any `123456`; accounts are hardcoded; no real token/session.
5. **2 roles only** — `UserRole { ADVOCATE, CITIZEN }`. Missing Firm Admin, Firm Member, Student.
6. **No firm/team** — no `LawFirm`, no membership, no role assignment, no add/remove users.
7. **No RBAC** — any advocate sees all cases; no permission checks anywhere.
8. **Mock AI** — `GeminiIntakeRepository` is a keyword `when{}` block, not a real model call.
9. **Mock consultation** — `ConsultationScreen` is static UI; no WebRTC, no scheduling.
10. **No documents** — the `CaseTask` checklist exists but there is no document model/upload/storage.
11. **No discovery** — citizens cannot search/filter lawyers; no lawyer profile page.
12. **No internships, no forum, no notifications, no payments, no ratings.**
13. **`UserProfile` lacks** `firmId`, `verificationStatus`, `specializations`, `bio`, `experienceYears`.
14. **`NyaayApplication` is empty** — no DI container, no seeded data, no theme init.
15. **Manifest has unused permissions** (CAMERA/RECORD_AUDIO/LOCATION) with no real usage; no
    `FileProvider` for uploads, no push-notification setup.

### 2.3 What is reusable (do not re-invent)
- **Theme** (`ui/theme/Color.kt`, `Theme.kt`, `Type.kt`) — Obsidian & Gold palette, dark-first. **Keep.**
- **Components** (`ui/components/NyaayComponents.kt`) — `NyaayTopAppBar`, `NyaayCard`,
  `CasePriorityBadge`, `CaseStatusBadge`. **Keep and extend.**
- **Domain shapes** (`data/model/Models.kt`) — `CaseProfile`, `CaseFile`, `CaseTimelineEvent`,
  `CaseTask`, `CaseNote`, `HearingItem`, `IntakeChatMessage`. **Extend, don't delete.**
- **Navigation scaffold** (`ui/navigation/NyaayNavGraph.kt`) — replace `Screen` sealed class
  with the full route set from Section 7.
- **Version catalog** (`gradle/libs.versions.toml`) — **extend** with Room, DataStore, Coil (already
  present), and (Phase B) Firebase.

---

## 3. Target Product Vision

> *"Your case. One place."* — a legal operating system for **citizens**, **lawyers**, **law firms**,
> **law students**, and a **public legal community**.

### 3.1 The 5 user roles
| # | Role | Who | Core capability |
| :- | :--- | :--- | :--- |
| 1 | **Citizen** | A person seeking legal help | Create cases via AI intake, hire lawyers, track case room, schedule consultations, read/join forum, pay. |
| 2 | **Independent Lawyer** | A solo advocate | Manage own practice: cases, cause list, consultations, earnings, profile/discovery, forum participation. |
| 3 | **Firm Admin** | Owner/admin of a law firm | Create firm, **add/remove members, assign roles & permissions**, assign cases to members, view firm analytics, billing/subscription, post internships. |
| 4 | **Firm Member** | A lawyer/staff inside a firm | Work on cases **assigned to them**, view shared cause list, collaborate (notes/timeline), limited by assigned firm role. |
| 5 | **Student (Intern seeker)** | A law student | Browse & apply to internships, track application status, read/write forum, build a lightweight student profile. |

### 3.2 Cross-cutting features (all roles)
- Trilingual UI (English / हिन्दी / मराठी), switch without restart.
- Trending discussion forum (posts, comments, upvotes, categories, moderation).
- In-app notifications with deep links.
- Role-appropriate onboarding (profile completeness).
- Lawyer verification (Bar Council ID) with admin approval.
- Search & discovery of lawyers/firms.
- Secure document uploads per case.

### 3.3 Business layer (make it financially useful)
- **Citizens:** free. Pay per consultation (fixed fee or lawyer-set fee).
- **Independent Lawyer / Firm:** monthly subscription tiers (`FREE`, `PROFESSIONAL`, `FIRM`, `PREMIUM`) unlocking more cases, members, storage, analytics.
- **Firm seats:** pricing scales with member count.
- **Platform commission:** X% on each paid consultation (configurable constant).
- **Ratings & reviews** drive lawyer ranking in discovery (trust = conversions).
- **Forum** increases engagement/retention.

---

## 4. Roles & Permissions (RBAC) Matrix

| Capability | Citizen | Independent Lawyer | Firm Admin | Firm Member | Student |
| :--- | :--: | :--: | :--: | :--: | :--: |
| Create/manage own case | ✅ | — | — | — | — |
| Manage assigned cases | — | ✅ (own) | ✅ (firm) | ✅ (assigned only) | — |
| Cause list / court diary | — | ✅ | ✅ (firm-wide) | ✅ (assigned) | — |
| Create firm | — | ✅ | ✅ | — | — |
| Invite/remove firm members | — | — | ✅ | — | — |
| Assign firm roles | — | — | ✅ | — | — |
| Reassign case to member | — | — | ✅ | — | — |
| Post internships | — | — | ✅ | — | — |
| Apply to internship | — | — | — | — | ✅ |
| Review internship applications | — | — | ✅ | — | — |
| Consultations (video/audio) | ✅ | ✅ | ✅ | ✅ | — |
| Forum: create/comment/vote | ✅ | ✅ | ✅ | ✅ | ✅ |
| Forum: moderate/remove posts | — | — | ✅ (firm/own) | — | — |
| Approve lawyer verification | — | — | System/Admin | — | — |
| Subscription & billing | ✅ (pay) | ✅ | ✅ | — (firm pays) | — |
| View analytics | — | ✅ (own) | ✅ (firm) | — | — |

**Rule:** a Firm Member **never** sees another member's cases unless the admin assigns them.
Permissions are enforced **in the repository layer** (not just hidden in UI), by filtering
queries on `assignedLawyerUid` / `firmId`.

---

## 5. Architecture & Tech Stack (decision + rationale)

### 5.1 Decision
**Replace the WebView PWA with a fully native Jetpack Compose app.** Retire `MainActivity`'s
WebView usage — keep the file but replace `setContentView(webView)` with
`setContent { NyaayTheme { NyaayNavGraph(...) } }`.

**Rationale:**
1. The Compose layer already exists (theme/components/nav/viewmodels) — reuse it.
2. Native gives offline capability, real file storage, real WebRTC, push notifications.
3. A lower-tier model can implement a native app far more reliably than a 2.6 MB minified
   React bundle plus a Cloudflare Worker plus Firebase security rules.
4. Local-first + Room makes the MVP fully testable without any external service.

### 5.2 Stack (extend `libs.versions.toml`; do **not** change existing versions)
| Concern | Choice | Notes |
| :--- | :--- | :--- |
| Language | Kotlin 2.0.21 | already set |
| UI | Jetpack Compose + Material 3 | already set |
| Architecture | **MVVM + Repository + single-Activity** | already the pattern |
| Persistence | **Room (KSP)** + **DataStore Preferences** | add |
| DI | **Manual DI via `AppContainer`** (no Hilt in MVP) | simplest for a lower model |
| Navigation | Navigation Compose (type-safe routes) | already set |
| Async | Coroutines + Flow | already set |
| Images | Coil | already set |
| Serialization | kotlinx-serialization-json | already set |
| AI intake | **`AIIntakeEngine` interface** with 2 impls: `RuleBasedIntakeEngine` (offline) + `GeminiIntakeEngine` (OkHttp REST) | add interface |
| Auth | Phase A: local OTP + Room session. Phase B: Firebase Phone Auth | |
| Backend | Phase A: none (Room). Phase B: Firebase Auth + Firestore | repository swap |
| Video calls | Phase A: meeting link (Jitsi/Whereby deep-link). Phase B: native WebRTC | keep simple |
| File upload | SAF (`ActivityResultContracts.OpenDocument`) + copy into app files dir + `FileProvider` | add |

### 5.3 New dependencies to add to `libs.versions.toml`
```toml
# Room
room = "2.6.1"
ksp = "2.0.21-1.0.25"          # must match Kotlin version
androidx-room-runtime  = { group="androidx.room", name="room-runtime", version.ref="room" }
androidx-room-ktx      = { group="androidx.room", name="room-ktx", version.ref="room" }
androidx-room-compiler = { group="androidx.room", name="room-compiler", version.ref="room" }
androidx-datastore-preferences = { group="androidx.datastore", name="datastore-preferences", version="1.1.1" }
# plugin: kotlin-ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```
> `build.gradle.kts`: add `alias(libs.plugins.kotlin.ksp)`, `implementation(libs.androidx.room.runtime)`,
> `implementation(libs.androidx.room.ktx)`, `ksp(libs.androidx.room.compiler)`,
> `implementation(libs.androidx.datastore.preferences)`.

### 5.4 Dependency graph (manual DI)
`NyaayApplication` → `AppContainer` → `NyaayDatabase` (Room) → DAOs → Repositories → ViewModels.
```kotlin
class AppContainer(context: Context) {
    val database = NyaayDatabase.get(context)
    val authRepository = AuthRepository(database.userDao(), dataStore)
    val firmRepository = FirmRepository(database.firmDao(), database.userDao())
    val caseRepository = CaseRepository(database.caseDao(), database.timelineDao(), ...)
    val consultationRepository = ...
    val internshipRepository = ...
    val forumRepository = ...
    val notificationRepository = ...
    val paymentRepository = ...
    val intakeEngine: AIIntakeEngine = RuleBasedIntakeEngine() // swap to GeminiIntakeEngine(...)
}
```
ViewModels take repositories via constructor (factory functions). **Do not** use
`AndroidViewModel` with `application` directly in MVP.

---

## 6. Data Model (complete)

> All types below are **Room entities** (with `@Entity`, `@PrimaryKey`) unless noted.
> Strings that are lists (e.g. `facts`, `tags`) are stored as **JSON text** via a `ListConverter`
> (kotlinx-serialization). Timestamps are `Long` (epoch millis). IDs are UUID strings.

### 6.1 Enums (put in `data/model/Enums.kt`)
```kotlin
enum class UserRole { CITIZEN, INDEPENDENT_LAWYER, FIRM_ADMIN, FIRM_MEMBER, STUDENT }

enum class FirmRole {              // permission level inside a firm
    OWNER, ADMIN, SENIOR_PARTNER, ASSOCIATE, PARALEGAL, INTERN
}

enum class VerificationStatus { UNVERIFIED, PENDING, VERIFIED, REJECTED }

enum class CaseStatus { DRAFT, ACTIVE, PENDING, HEARING_SCHEDULED, DISPOSED, CLOSED }

enum class CasePriority { NORMAL, URGENT }

enum class CaseStage {             // ordered workflow
    INTAKE, NOTICE_PLEADINGS, EVIDENCE, ARGUMENTS, JUDGMENT, CLOSED
}

enum class ConsultationStatus { REQUESTED, ACCEPTED, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, REJECTED }

enum class PaymentStatus { PENDING, PAID, FAILED, REFUNDED }

enum class SubscriptionTier { FREE, PROFESSIONAL, FIRM, PREMIUM }

enum class InternshipStatus { DRAFT, OPEN, CLOSED }

enum class ApplicationStatus { APPLIED, SHORTLISTED, ACCEPTED, REJECTED, COMPLETED }

enum class ForumCategory { GENERAL, LEGAL_NEWS, JUDGMENT_ANALYSIS, ASK_LAWYER, CAREER, INTERNSHIPS }

enum class NotificationType { CASE_UPDATE, HEARING_REMINDER, CONSULTATION, MESSAGE, VERIFICATION, INTERNSHIP, FORUM, PAYMENT, SYSTEM }
```

### 6.2 User & Firm entities
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val phone: String,                 // "+91XXXXXXXXXX"
    val name: String,
    val email: String? = null,
    val role: UserRole,
    val firmId: String? = null,        // set for FIRM_ADMIN & FIRM_MEMBER
    val firmRole: FirmRole? = null,    // set for FIRM_MEMBER (and OWNER/ADMIN)
    val barCouncilId: String? = null,  // lawyers only
    val verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    val specializations: String = "[]", // JSON list e.g. ["Family","RERA","Criminal"]
    val bio: String? = null,
    val experienceYears: Int = 0,
    val state: String? = null,
    val city: String? = null,
    val language: String = "en",       // "en" | "hi" | "mr"
    val onboardingCompleted: Boolean = false,
    val college: String? = null,       // students only
    val yearOfStudy: String? = null,   // students only
    val avgRating: Double = 0.0,
    val reviewCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "firms")
data class LawFirmEntity(
    @PrimaryKey val firmId: String,
    val name: String,
    val adminUid: String,              // FIRM_ADMIN owner
    val gstin: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val logoUri: String? = null,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "verification_requests")
data class VerificationRequestEntity(
    @PrimaryKey val id: String,
    val uid: String,
    val barCouncilId: String,
    val supportingDocUri: String? = null,
    val status: VerificationStatus = VerificationStatus.PENDING,
    val reviewedBy: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
```

### 6.3 Case & case-child entities
```kotlin
@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey val id: String,
    val caseNumber: String,            // "CNR-MH-XXXX-YYYYYY-YYYY"
    val title: String,
    val clientUid: String,             // citizen who created it
    val clientName: String,
    val clientPhone: String,
    val firmId: String? = null,        // set when assigned to a firm
    val lawyerUid: String? = null,     // assigned lawyer (independent or firm member)
    val lawyerName: String? = null,
    val court: String = "District & Sessions Court",
    val matterType: String,
    val legalArea: String,
    val location: String = "India",
    val stage: CaseStage = CaseStage.INTAKE,
    val priority: CasePriority = CasePriority.NORMAL,
    val status: CaseStatus = CaseStatus.ACTIVE,
    val nextHearingDate: Long? = null, // epoch millis
    val factsJson: String = "[]",      // JSON list of fact strings
    val summaryText: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "case_timeline_events")
data class TimelineEventEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val type: String,                  // "intake"|"hearing"|"note"|"doc"|"status"|"assignment"
    val titleEn: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val eventDate: Long,
    val actor: String = "System",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "case_tasks")
data class CaseTaskEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val titleEn: String,
    val titleHi: String,
    val assignedTo: String,            // "lawyer" | "client"
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "case_notes")
data class CaseNoteEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val content: String,
    val authorUid: String,
    val authorName: String,
    val isPrivate: Boolean = false,    // lawyer-only notes hidden from client
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val ownerUid: String,
    val name: String,
    val uri: String,                   // file:// in app files dir
    val mimeType: String,
    val sizeBytes: Long,
    val category: String,              // "agreement"|"proof"|"notice"|"id"|"other"
    val uploadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "hearings")
data class HearingEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val caseNumber: String,
    val courtNumber: String,
    val itemNumber: Int,
    val stage: String,
    val hearingDate: Long,             // epoch millis (date only)
    val hearingTime: String,           // "10:30 AM"
    val purpose: String
)
```

### 6.4 Consultation, Internship, Forum, Notification, Payment entities
```kotlin
@Entity(tableName = "consultations")
data class ConsultationEntity(
    @PrimaryKey val id: String,
    val citizenUid: String,
    val lawyerUid: String,
    val caseId: String? = null,        // optional linked case
    val status: ConsultationStatus = ConsultationStatus.REQUESTED,
    val requestedAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null,
    val durationMinutes: Int = 30,
    val fee: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val meetingLink: String? = null,   // Jitsi/Whereby URL (Phase A)
    val notes: String? = null          // citizen's question summary
)

@Entity(tableName = "internships")
data class InternshipEntity(
    @PrimaryKey val id: String,
    val firmId: String,
    val title: String,
    val description: String,
    val location: String,              // "Mumbai" | "Remote"
    val durationMonths: Int = 1,
    val stipend: String = "Unpaid",    // "₹5,000/month"
    val skillsJson: String = "[]",     // ["Drafting","Research","Litigation"]
    val eligibility: String = "Law student (any year)",
    val deadline: Long? = null,
    val status: InternshipStatus = InternshipStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "internship_applications")
data class InternshipApplicationEntity(
    @PrimaryKey val id: String,
    val internshipId: String,
    val studentUid: String,
    val coverLetter: String,
    val resumeUri: String? = null,
    val status: ApplicationStatus = ApplicationStatus.APPLIED,
    val appliedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "forum_posts")
data class ForumPostEntity(
    @PrimaryKey val id: String,
    val authorUid: String,
    val authorName: String,
    val authorRole: UserRole,
    val category: ForumCategory,
    val title: String,
    val body: String,
    val tagsJson: String = "[]",
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val commentCount: Int = 0,
    val isPinned: Boolean = false,
    val isRemoved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "forum_comments")
data class ForumCommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorUid: String,
    val authorName: String,
    val body: String,
    val parentId: String? = null,      // null = top-level comment
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "forum_votes")
data class ForumVoteEntity(
    @PrimaryKey val id: String,        // "{postId}:{uid}"
    val postId: String,
    val uid: String,
    val value: Int                     // +1 or -1
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val lawyerUid: String,
    val citizenUid: String,
    val rating: Int,                   // 1..5
    val comment: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val uid: String,                   // recipient
    val type: NotificationType,
    val title: String,
    val body: String,
    val deepLink: String? = null,      // e.g. "case_room/<id>"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val ownerUid: String,              // lawyer uid or firm admin uid
    val firmId: String? = null,
    val tier: SubscriptionTier,
    val startAt: Long,
    val endAt: Long,
    val status: PaymentStatus = PaymentStatus.PAID
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val payerUid: String,
    val payeeUid: String? = null,
    val amount: Double,
    val purpose: String,               // "consultation" | "subscription"
    val refId: String,                 // consultationId or subscriptionId
    val status: PaymentStatus = PaymentStatus.PENDING,
    val gatewayRef: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
```

## 7. Navigation & Screen Inventory

> Replace the `Screen` sealed class in `NyaayNavGraph.kt` with the full route set below.
> Use a bottom navigation bar for each role's primary tabs (3–4 tabs max, for simplicity).

### 7.1 Common (auth) flow
| Route | Screen | Purpose |
| :--- | :--- | :--- |
| `language_select` | `LanguageSelectScreen` | pick EN/HI/MR |
| `role_select` | `RoleSelectScreen` | pick one of 5 roles |
| `phone_auth` | `PhoneAuthScreen` | 10-digit phone + OTP |
| `onboarding/{role}` | `OnboardingScreen` | role-specific profile form |

### 7.2 Citizen (bottom tabs: Home · Cases · Forum · Profile)
| Route | Screen |
| :--- | :--- |
| `citizen/home` | `CitizenHomeScreen` (AI intake banner, lawyer discovery, active cases) |
| `citizen/cases` | `CitizenCasesScreen` (list) |
| `case_room/{caseId}` | `CaseRoomScreen` (tabs: Summary, Timeline, Tasks, Documents) |
| `build_case_intake` | `BuildCaseIntakeScreen` (AI chat) |
| `lawyers` | `LawyerDiscoveryScreen` (search/filter/sort) |
| `lawyer/{uid}` | `LawyerProfileScreen` (bio, rating, reviews, book) |
| `consultation/book/{lawyerUid}` | `BookConsultationScreen` |
| `consultation/{id}` | `ConsultationScreen` (live/meeting link) |
| `payments` | `PaymentsScreen` (history, pay) |
| `notifications` | `NotificationsScreen` |

### 7.3 Independent Lawyer (bottom tabs: Home · Cases · Forum · Profile)
| Route | Screen |
| :--- | :--- |
| `lawyer/home` | `AdvocateHomeScreen` (court diary, priority queue, earnings) |
| `lawyer/cases` | `AdvocateCasesScreen` |
| `case_room/{caseId}` | `CaseRoomScreen` (lawyer variant: add note/hearing/task/doc) |
| `cause_list` | `CauseListScreen` |
| `lawyer/profile/{uid}` | `LawyerProfileScreen` (edit own) |
| `lawyer/verification` | `VerificationScreen` (submit Bar Council ID) |
| `lawyer/analytics` | `LawyerAnalyticsScreen` |
| `consultation/{id}` | `ConsultationScreen` |
| `subscription` | `SubscriptionScreen` (choose plan) |
| `notifications` | `NotificationsScreen` |

### 7.4 Firm Admin (bottom tabs: Dashboard · Firm · Forum · Profile)
| Route | Screen |
| :--- | :--- |
| `firm/home` | `FirmAdminHomeScreen` (KPIs, recent activity) |
| `firm/members` | `FirmMembersScreen` (list, add/remove, assign role) |
| `firm/members/invite` | `InviteMemberScreen` (phone + role) |
| `firm/cases` | `FirmCasesScreen` (all firm cases, reassign) |
| `firm/internships` | `FirmInternshipsScreen` (post/manage) |
| `firm/internships/new` | `PostInternshipScreen` |
| `firm/internships/{id}/applications` | `ApplicationsScreen` (review/status) |
| `firm/analytics` | `FirmAnalyticsScreen` |
| `firm/subscription` | `SubscriptionScreen` |
| `case_room/{caseId}` | `CaseRoomScreen` |
| `notifications` | `NotificationsScreen` |

### 7.5 Firm Member (bottom tabs: Home · Cases · Forum · Profile)
| Route | Screen |
| :--- | :--- |
| `member/home` | `FirmMemberHomeScreen` (assigned court diary + cases) |
| `member/cases` | `FirmMemberCasesScreen` (assigned only) |
| `case_room/{caseId}` | `CaseRoomScreen` |
| `cause_list` | `CauseListScreen` (assigned hearings) |
| `notifications` | `NotificationsScreen` |

### 7.6 Student (bottom tabs: Home · Internships · Forum · Profile)
| Route | Screen |
| :--- | :--- |
| `student/home` | `StudentHomeScreen` (featured internships, forum highlights) |
| `student/internships` | `InternshipListScreen` (browse/filter/search) |
| `internship/{id}` | `InternshipDetailScreen` (apply) |
| `student/applications` | `MyApplicationsScreen` (status) |
| `student/profile` | `StudentProfileScreen` (college, year, resume) |
| `notifications` | `NotificationsScreen` |

### 7.7 Shared
| Route | Screen |
| :--- | :--- |
| `forum` | `ForumScreen` (categories, trending, search) |
| `forum/post/new` | `NewPostScreen` |
| `forum/post/{id}` | `ForumPostDetailScreen` (comments, votes) |
| `notifications` | `NotificationsScreen` |

---

## 8. Repository, DAO & ViewModel Contracts

> Repositories are the **only** layer that touches Room. ViewModels expose `StateFlow` to UI.
> Every repository method that mutates state **also writes a `NotificationEntity`** and
> **also appends a `TimelineEventEntity`** where relevant (see Section 9).

### 8.1 DAO list (one interface per aggregate, in `data/local/`)
`UserDao`, `FirmDao`, `CaseDao`, `TimelineDao`, `TaskDao`, `NoteDao`, `DocumentDao`,
`HearingDao`, `ConsultationDao`, `InternshipDao`, `ForumDao`, `VoteDao`, `ReviewDao`,
`NotificationDao`, `SubscriptionDao`, `PaymentDao`, `VerificationDao`.

**Key DAO queries (must implement):**
```kotlin
// UserDao
@Query("SELECT * FROM users WHERE uid = :uid") suspend fun getById(uid: String): UserEntity?
@Query("SELECT * FROM users WHERE role IN ('INDEPENDENT_LAWYER','FIRM_ADMIN','FIRM_MEMBER') AND verificationStatus = 'VERIFIED'")
fun observeVerifiedLawyers(): Flow<List<UserEntity>>
@Query("SELECT * FROM users WHERE firmId = :firmId") fun observeFirmMembers(firmId: String): Flow<List<UserEntity>>

// CaseDao
@Query("SELECT * FROM cases WHERE clientUid = :uid ORDER BY updatedAt DESC") fun observeByClient(uid: String): Flow<List<CaseEntity>>
@Query("SELECT * FROM cases WHERE lawyerUid = :uid ORDER BY updatedAt DESC") fun observeByLawyer(uid: String): Flow<List<CaseEntity>>
@Query("SELECT * FROM cases WHERE firmId = :firmId ORDER BY updatedAt DESC") fun observeByFirm(firmId: String): Flow<List<CaseEntity>>
@Query("SELECT * FROM cases WHERE firmId = :firmId AND lawyerUid = :uid") fun observeByMember(firmId: String, uid: String): Flow<List<CaseEntity>>

// ForumDao (trending = score desc)
@Query("SELECT * FROM forum_posts WHERE isRemoved = 0 ORDER BY (upvotes - downvotes) DESC, createdAt DESC")
fun observeTrending(): Flow<List<ForumPostEntity>>

// NotificationDao
@Query("SELECT * FROM notifications WHERE uid = :uid ORDER BY createdAt DESC") fun observeFor(uid: String): Flow<List<NotificationEntity>>
```

### 8.2 Repository interfaces (the "port" — swap Room ↔ Firestore in Phase B)
```kotlin
interface AuthRepositoryI {
    val currentUser: StateFlow<UserEntity?>
    val selectedLanguage: StateFlow<String>
    suspend fun sendOtp(phone: String): Result<String>
    suspend fun verifyOtp(phone: String, otp: String, role: UserRole): Result<UserEntity>
    suspend fun updateProfile(user: UserEntity): Result<Unit>
    fun setLanguage(code: String)
    fun signOut()
}

interface FirmRepositoryI {
    fun observeFirm(firmId: String): Flow<LawFirmEntity?>
    fun observeMembers(firmId: String): Flow<List<UserEntity>>
    suspend fun createFirm(name: String, admin: UserEntity): Result<LawFirmEntity>
    suspend fun inviteMember(firmId: String, phone: String, firmRole: FirmRole): Result<UserEntity>
    suspend fun removeMember(firmId: String, uid: String): Result<Unit>
    suspend fun assignRole(firmId: String, uid: String, role: FirmRole): Result<Unit>
    suspend fun assignCase(caseId: String, lawyerUid: String): Result<Unit>
}

interface CaseRepositoryI {
    fun observeCasesFor(user: UserEntity): Flow<List<CaseEntity>>
    fun observeCase(id: String): Flow<CaseEntity?>
    fun observeTimeline(caseId: String): Flow<List<TimelineEventEntity>>
    fun observeTasks(caseId: String): Flow<List<CaseTaskEntity>>
    fun observeNotes(caseId: String): Flow<List<CaseNoteEntity>>
    fun observeDocuments(caseId: String): Flow<List<DocumentEntity>>
    suspend fun createCaseFromProfile(client: UserEntity, profile: CaseProfile): String
    suspend fun toggleTask(caseId: String, taskId: String)
    suspend fun addNote(caseId: String, content: String, author: UserEntity, isPrivate: Boolean)
    suspend fun addDocument(caseId: String, owner: UserEntity, doc: DocumentEntity)
    suspend fun updateStage(caseId: String, stage: CaseStage)
    suspend fun updatePriority(caseId: String, priority: CasePriority)
}
// + ConsultationRepositoryI, InternshipRepositoryI, ForumRepositoryI,
//   NotificationRepositoryI, PaymentRepositoryI, ReviewRepositoryI, DiscoveryRepositoryI
```
> Every interface above gets **two implementations**: `Room*Repository` (Phase A) and
> `Firestore*Repository` (Phase B). ViewModels depend only on the interface.

### 8.3 ViewModels (in `viewmodel/`, one per screen or aggregate)
`AuthViewModel`, `OnboardingViewModel`, `CitizenHomeViewModel`, `CaseRoomViewModel`,
`AIIntakeViewModel`, `DiscoveryViewModel`, `LawyerProfileViewModel`, `ConsultationViewModel`,
`AdvocateHomeViewModel`, `CauseListViewModel`, `FirmAdminViewModel`, `FirmMemberViewModel`,
`InternshipViewModel`, `StudentViewModel`, `ForumViewModel`, `NotificationsViewModel`,
`BillingViewModel`, `AnalyticsViewModel`.

Each exposes: input `MutableStateFlow`, a `StateFlow<UiState>` data class
(`loading`, `error`, `data`), and `fun onEvent(...)` methods.
**No suspend calls in UI** — everything goes through `viewModelScope.launch`.

---

## 9. Feature Specifications (detailed)

> Each feature lists **User stories** and **Acceptance criteria (AC)**. These are the
> source of truth for "done".

### 9.1 Authentication & Session
- **User story:** As any user, I enter my 10-digit phone, receive OTP, and sign in with a chosen role.
- **AC:**
  1. Phone input strips non-digits, max 10, shows `+91` prefix once (reuse existing logic).
  2. Phase A: `sendOtp` returns a fake verification id; `verifyOtp` accepts `123456` **and** the
     seeded account OTPs. Store `currentUid` in DataStore so **restart keeps login**.
  3. `startDestination` derives from `currentUser` **and** `onboardingCompleted` (unfinished → onboarding).
  4. Sign-out clears session and pops to `language_select`.
- **Edge cases:** invalid OTP → inline error; empty phone disables button; role chosen first.

### 9.2 Onboarding (role-specific)
- **AC:**
  1. Citizen: name, state, city (optional email).
  2. Independent Lawyer: name, state, city, **Bar Council ID**, specializations (multi-select chips), experience years, bio → sets `verificationStatus = PENDING`, creates a `VerificationRequestEntity`.
  3. Firm Admin: name, firm name, GSTIN, address, city → creates `LawFirmEntity`, sets own `firmId` + `firmRole = OWNER`.
  4. Firm Member: name, then admin invites them (see 9.6); a member cannot self-join a firm.
  5. Student: name, college, year of study.
  6. After onboarding, `onboardingCompleted = true` and navigate to role home.
- **Edge case:** lawyer without Bar Council ID can still use app but sees a "Verify now" banner and is **excluded** from public discovery until `VERIFIED`.

### 9.3 AI Intake ("Build My Case")
- **User story:** As a citizen, I describe my problem in plain words and get a structured case profile + document checklist.
- **AC:**
  1. Chat UI (existing `IntakeChatMessage`) with fake typing via `delay`.
  2. `AIIntakeEngine.analyze(userText): CaseProfile`. `RuleBasedIntakeEngine` maps keywords → legalArea + required docs (move the existing `GeminiIntakeRepository` logic into this class).
  3. `GeminiIntakeEngine` (Phase B) calls Gemini REST via OkHttp with a fixed system prompt; **fallback** to `RuleBasedIntakeEngine` on network/JSON error (never crash).
  4. "Create Case Room" → `createCaseFromProfile`, generates `caseNumber`, seeds timeline + task checklist (documents required → tasks).
- **Edge case:** empty input ignored; input truncated to 2000 chars; no profile yet → disable create button.

### 9.4 Case Room
- **AC:**
  1. Four tabs: **Summary** (title, court, lawyer, stage, priority, status, facts, next hearing), **Timeline** (sorted desc), **Tasks** (checkboxes, per-role), **Documents** (list + upload).
  2. Citizen sees tasks `assignedTo = client`; lawyer sees all + can add tasks.
  3. Lawyer-only actions: change stage, change priority, add private note, add hearing, add task.
  4. Every change appends a `TimelineEventEntity` and notifies the other party.
- **Edge case:** a Firm Member opening a case not assigned to them → "No access" (repository filters).

### 9.5 Lawyer Discovery, Profile & Reviews
- **User story:** As a citizen, I search lawyers by name/city/specialization and view ratings before booking.
- **AC:**
  1. `observeVerifiedLawyers()` feeds a list; filter by `specializations`, `city`, `state`; sort by rating (desc), experience, name.
  2. Profile shows: photo placeholder, name, bar ID, specializations chips, experience, bio, avg rating (1–5 stars), review count, reviews list, "Book consultation" button.
  3. A citizen who completed a consultation can leave a 1–5 star review; recalculate lawyer `avgRating` and `reviewCount` on insert.
- **Edge case:** no results → empty state with "try different filters"; unverified lawyers hidden.

### 9.6 Law Firm Management (Admin)
- **User story:** As a Firm Admin, I invite lawyers/staff, assign roles, and assign cases.
- **AC:**
  1. `createFirm` creates firm + sets admin membership.
  2. `inviteMember(firmId, phone, firmRole)` looks up or creates the user (role becomes `FIRM_MEMBER`), sets `firmId` + `firmRole`; sends notification.
  3. `removeMember` clears the user's `firmId`/`firmRole` and reassigns their cases to `firmId` (unassigned) so nothing is orphaned.
  4. `assignRole` updates `firmRole` (e.g. ASSOCIATE → SENIOR_PARTNER).
  5. `assignCase(caseId, lawyerUid)` sets `case.lawyerUid`, `lawyerName`, `firmId`; notifies the member and the client.
  6. Members list shows role badge, case count, and status.
- **Edge case:** cannot remove the OWNER; cannot invite a phone that is already a member of another firm.

### 9.7 Cause List / Court Diary
- **User story:** As a lawyer/member, I see today's hearings ordered by court + item number.
- **AC:**
  1. Hearings filtered by date (today) and by `lawyerUid`/`firmId` (RBAC from Section 4).
  2. Card shows item number, court number, time, case number, stage, purpose (reuse `HearingCard`).
  3. Lawyer can add a hearing to a case (court number, item number, date, time, purpose).
- **Edge case:** no hearings today → "No hearings scheduled today" empty state.

### 9.8 Consultations
- **User story:** As a citizen, I book a paid consultation; as a lawyer, I accept and meet.
- **AC:**
  1. `BookConsultationScreen`: pick lawyer, date/time, duration (15/30/45/60 min), fee shown, "Pay & Book".
  2. Creates `ConsultationEntity(status=REQUESTED)` + `PaymentEntity(purpose=consultation)`; mark paid on success (Phase A simulates payment).
  3. Lawyer accepts → `ACCEPTED`/`SCHEDULED`, generates `meetingLink` (Jitsi/Whereby URL), notifies citizen.
  4. `ConsultationScreen` shows meeting link (open in browser via `Intent.ACTION_VIEW`) and call timer; End Call → `COMPLETED`, prompts review.
- **Edge case:** past dates disabled; lawyer can REJECT with reason; cancelled before payment → no charge.

### 9.9 Internship Portal
- **User story:** As a student, I browse & apply; as a firm admin, I post and review applications.
- **AC:**
  1. Admin posts internship: title, description, location, duration, stipend, skills, eligibility, deadline.
  2. Student list: filter by location (`Remote`), search title, show deadline countdown; `OPEN`/`CLOSED` badge.
  3. Apply: cover letter (required) + optional resume upload (SAF).
  4. Admin views applications per internship and updates status: SHORTLISTED / ACCEPTED / REJECTED / COMPLETED; notifies student on each change.
  5. Student's `MyApplicationsScreen` shows status timeline.
- **Edge case:** deadline passed → auto `CLOSED`, apply disabled; student can't apply twice to same internship.

### 9.10 Trending Discussion Forum
- **User story:** As any user, I read trending legal discussions, post, comment, and vote.
- **AC:**
  1. `ForumScreen` tabs: **Trending** (score = upvotes−downvotes desc), **Latest**, **Categories** (filter by `ForumCategory`).
  2. Compose a post: category, title, body, tags (comma-separated).
  3. Detail screen: post, vote buttons (+1/−1, one vote per user via `ForumVoteEntity`), comments (nested one level via `parentId`), comment composer.
  4. Author can delete own post/comment; Firm Admin can remove any post (`isRemoved = true`).
  5. Vote is idempotent (re-vote replaces value; un-vote removes row).
- **Edge case:** removed posts hidden from lists; empty state per category; long text clamped with "read more".

### 9.11 Notifications
- **AC:**
  1. Single `NotificationsScreen` shared by all roles; list sorted desc; unread bold; tap → mark read + navigate via `deepLink`.
  2. Bell icon with unread badge in every top app bar.
  3. Repository writes `NotificationEntity` on every meaningful event (case update, hearing added, consultation accepted, internship status change, new comment on own post, payment).
  4. Phase B: bridge to push via FCM (reuse same entity + deep link).

### 9.12 Payments, Subscriptions & Business
- **AC:**
  1. `SubscriptionScreen` lists 4 tiers with monthly price + feature list; `FREE` always available.
  2. Limits enforced in repository (Phase A): FREE = 3 active cases & 1 member & 50 MB docs; PROFESSIONAL = 25 cases; FIRM = unlimited members; PREMIUM = analytics + priority support. (Exact numbers in a single `PlanLimits` object so they're easy to change.)
  3. Choosing a paid tier → `SubscriptionEntity` + `PaymentEntity`; Phase A marks paid instantly (simulated gateway).
  4. `PaymentsScreen` shows history with status badges.
  5. `AnalyticsScreen` (lawyer/firm): active cases, hearings this month, consultations completed, earnings, rating trend. Phase A computes from Room; Phase B from Firestore aggregates.
- **Edge case:** subscription expired → show upgrade banner and limit new actions; refund sets `PaymentStatus.REFUNDED`.

### 9.13 Documents
- **AC:**
  1. Upload via SAF `OpenDocument`; copy to `filesDir/documents/<caseId>/<uuid>_<name>`; persist `DocumentEntity` with `file://` URI.
  2. List documents with name, size, date, category; tap to open via `FileProvider` + `Intent.ACTION_VIEW`.
  3. Enforce per-case storage quota per plan.
- **Edge case:** large file (> plan limit) rejected with message; duplicate name allowed (uuid prefix).

---

## 10. UX & Design System (simplicity rules)

> The user explicitly said the current UX is poor and the app must be **very simple to use**.
> Follow these rules everywhere.

1. **≤ 4 bottom tabs** per role. No nested drawers. Every destination reachable in ≤ 3 taps.
2. **One primary action per screen** (a single gold FAB or full-width button). Secondary actions in an overflow menu.
3. **Consistent language**: reuse `NyaayTopAppBar`, `NyaayCard`, badges. Use the Obsidian/Gold theme (already defined). Never introduce new colors; extend `Color.kt` only if needed.
4. **Empty states everywhere**: icon + one-line hint + optional CTA. Never show a blank list.
5. **Loading states**: `CircularProgressIndicator` centered; disable buttons while loading.
6. **Error states**: inline snackbar/text, never silent. Toast only for non-blocking confirmations.
7. **Confirmation dialogs** for destructive actions (remove member, delete post, end call).
8. **Role-aware UI**: hide (not disable) actions the user can't perform (e.g. citizen never sees "add hearing").
9. **Accessibility**: every `IconButton` has `contentDescription`; touch targets ≥ 48 dp; text scales with system font.
10. **Onboarding is short**: ≤ 2 screens, optional fields clearly marked, skippable where safe.

---

## 11. Localization

- Keep `Language` concept; store code (`en`/`hi`/`mr`) in `UserEntity.language` + DataStore.
- All **UI strings** go through a `Strings` object with `@Composable get` or a `LocalStrings`
  (do not hardcode English in screens). Provide `values/strings.xml` (en) plus Hindi & Marathi
  `values-hi/`, `values-mr/` for system strings; app content strings via a `LocalizedStrings` map.
- Content fields already store `titleEn`/`titleHi` for timeline/tasks — **add Marathi** (`titleMr`, `descriptionMr`) OR store a single JSON `{en,hi,mr}`. Use JSON to avoid 3 columns everywhere.
- Language switch must not require restart (existing behavior).

---

## 12. Testing Plan

> Fix the existing broken test **first**, then add new tests in this order.

1. **Fix `CaseRepositoryTest`** — it asserts seed data that no longer exists. Either add a
   `seedDatabase()` used only by tests, or rewrite assertions to match the new `CaseRepositoryI`
   contract (create → observe → mutate → assert).
2. **Unit tests (Room `inMemoryDatabaseBuilder`)**, one per repository:
   - `AuthRepositoryTest`: sign-up/in/out, session persistence, invalid OTP.
   - `CaseRepositoryTest`: create case, RBAC filtering (member cannot see unassigned), toggle task, add note, stage transition.
   - `FirmRepositoryTest`: create firm, invite member, remove member (cases not orphaned), assign role, assign case.
   - `InternshipRepositoryTest`: post, apply (duplicate rejected), status transitions.
   - `ForumRepositoryTest`: post, comment, idempotent vote, remove.
   - `PaymentRepositoryTest`: book consultation → payment created → paid; plan limits enforced.
3. **ViewModel tests** with `kotlinx-coroutines-test`: state transitions, error states, loading flags.
4. **UI (Compose) tests** (optional, Phase B): happy-path for auth, case creation, forum post.
5. **Run & gate:** `./gradlew testDebugUnitTest` must pass green before every phase ends.

---

## 13. Implementation Phases (ordered build checklist)

> Build in this exact order. Each phase ends with **compile + tests green**.

### Phase 0 — Foundation (fix the shell)
1. Rewire `MainActivity` to `setContent { NyaayTheme { ... NyaayNavGraph(...) } }`; delete WebView code.
2. Add Room + KSP + DataStore deps to `libs.versions.toml` and `app/build.gradle.kts`.
3. Create `data/local/NyaayDatabase.kt` (all entities + `ListConverter` + DAOs) and `AppContainer`.
4. Move/extend enums (`Enums.kt`) and update `Models.kt` (keep existing `CaseProfile`, `CaseFile`, `HearingItem`, `IntakeChatMessage` for UI mapping).
5. Fix `CaseRepositoryTest` so the suite compiles and passes (use a seeded in-memory DB).

### Phase 1 — Auth, Session & Onboarding
6. `AuthRepository` (Room + DataStore session), `OnboardingScreen`, role-specific forms.
7. Update `NyaayNavGraph` start-destination logic (`currentUser` + `onboardingCompleted`).
8. Wire `LanguageSelectScreen` / `RoleSelectScreen` / `PhoneAuthScreen` to new repository.

### Phase 2 — Citizen core
9. `CaseRepository`, `CaseRoomScreen` (4 tabs), `BuildCaseIntakeScreen` + `AIIntakeEngine` (rule-based).
10. `CitizenHomeScreen` + `CitizenCasesScreen` with bottom nav.

### Phase 3 — Lawyer core (independent)
11. `AdvocateHomeScreen`, `AdvocateCasesScreen`, `CauseListScreen` (reuse `HearingCard`).
12. `VerificationScreen` + verification request flow + discovery exclusion until verified.

### Phase 4 — Firm & RBAC
13. `FirmRepository`, `FirmAdminHomeScreen`, `FirmMembersScreen`, `InviteMemberScreen`, role assignment, case assignment.
14. `FirmMemberHomeScreen` + `FirmMemberCasesScreen` (assigned-only).

### Phase 5 — Discovery, Reviews & Consultations
15. `LawyerDiscoveryScreen`, `LawyerProfileScreen`, `BookConsultationScreen`, `ConsultationScreen`.
16. `ReviewRepository` + rating recompute.

### Phase 6 — Internships & Forum
17. `InternshipRepository`, `InternshipListScreen`, `InternshipDetailScreen`, `MyApplicationsScreen`, `PostInternshipScreen`, `ApplicationsScreen`.
18. `ForumRepository`, `ForumScreen`, `NewPostScreen`, `ForumPostDetailScreen`, votes/comments.

### Phase 7 — Notifications, Payments & Analytics
19. `NotificationRepository` + `NotificationsScreen` + unread badge; hook into every mutating method.
20. `SubscriptionScreen`, `PaymentRepository`, `PaymentsScreen`, `PlanLimits`, `AnalyticsScreen`.

### Phase 8 — Polish & business hardening
21. Localization (EN/HI/MR), empty/loading/error states, accessibility pass, edge-case sweep.
22. Seed demo data (a firm with members, internships, forum posts) on first run for demo mode.

### Phase 9 — Production backend (optional, Phase B)
23. Firebase Auth (phone) + Firestore; implement `Firestore*Repository` behind the same interfaces; migrate. Keep Room as offline cache.

---

## 14. Definition of Done (acceptance for the whole app)

A feature/phase is **done** only when ALL of these are true:
1. Compiles with **no warnings-as-errors** and `./gradlew testDebugUnitTest` is green.
2. The RBAC matrix (Section 4) is enforced **in the repository**, not only in the UI.
3. Every list has loading, empty, and error states (Section 10).
4. Every mutation writes the correct `NotificationEntity` + `TimelineEventEntity`.
5. Data survives app restart (Room + DataStore session).
6. All UI strings are localized (Section 11) — no hardcoded English in screens.
7. The screen is reachable within 3 taps from its role home.
8. Destructive actions are behind a confirmation dialog.

---

## 15. Risks & Mitigations

| Risk | Mitigation |
| :--- | :--- |
| 2.6 MB web PWA already contains working features (eCourts, NVIDIA NIM) that get discarded | Keep the web assets untouched for now; re-implement the *visible* feature set natively. Revisit eCourts/NIM only in Phase B. |
| Lower-tier model may struggle with Room + KSP | Use explicit DAO SQL (given above); add one entity at a time; gate each phase on compile+tests. |
| RBAC leaking data across firm members | Filter in DAO queries (`WHERE firmId=:firmId AND lawyerUid=:uid`), never filter only in UI. Add a repository unit test per Section 12. |
| Payments/legal compliance | Phase A uses a **simulated** gateway with a clear `PaymentGateway` interface; real gateway (Razorpay/Stripe) in Phase B. Keep `PaymentEntity` audit trail. |
| WebRTC complexity | Phase A uses meeting links (Jitsi/Whereby) opened in browser; native WebRTC deferred. |
| Scope creep | Phase 0–8 is the MVP; Phase 9 is optional. Anything not listed is out of scope. |

---

## 16. Appendix — Target File Map (add/change)

```
app/src/main/java/com/nyaayneeti/app/
├── MainActivity.kt                     # CHANGED: setContent + NyaayNavGraph (drop WebView)
├── NyaayApplication.kt                 # CHANGED: build AppContainer
├── AppContainer.kt                     # NEW: manual DI
├── data/
│   ├── model/
│   │   ├── Enums.kt                    # NEW: all enums (Sec 6.1)
│   │   ├── Models.kt                   # CHANGED: keep CaseProfile/CaseFile/etc, add DTOs
│   │   └── Entities.kt                 # NEW: Room entities (Sec 6.2–6.4)
│   ├── local/
│   │   ├── NyaayDatabase.kt            # NEW
│   │   ├── Converters.kt               # NEW: ListConverter, date converters
│   │   └── *Dao.kt                     # NEW: 17 DAOs
│   ├── repository/
│   │   ├── AuthRepository.kt           # CHANGED → AuthRepositoryI + Room impl
│   │   ├── FirmRepository.kt           # NEW
│   │   ├── CaseRepository.kt           # CHANGED → CaseRepositoryI + Room impl
│   │   ├── ConsultationRepository.kt   # NEW
│   │   ├── InternshipRepository.kt     # NEW
│   │   ├── ForumRepository.kt          # NEW
│   │   ├── NotificationRepository.kt   # NEW
│   │   ├── PaymentRepository.kt        # NEW
│   │   ├── ReviewRepository.kt         # NEW
│   │   └── ai/AIIntakeEngine.kt        # NEW: interface + RuleBased + Gemini impls
│   └── prefs/SessionStore.kt           # NEW: DataStore wrapper
├── ui/
│   ├── navigation/NyaayNavGraph.kt     # CHANGED: full route set + role tabs
│   ├── components/NyaayComponents.kt   # CHANGED: add badges, empty/loading/error composables
│   ├── theme/                          # KEEP (Color/Theme/Type)
│   └── screens/
│       ├── auth/                       # CHANGED + add OnboardingScreen
│       ├── citizen/                    # CHANGED + LawyerDiscovery, BookConsultation, Payments
│       ├── advocate/                   # CHANGED + Verification, Analytics, Subscription
│       ├── firm/                       # NEW: admin + member screens
│       ├── student/                    # NEW: internship + profile screens
│       ├── forum/                      # NEW: Forum, NewPost, PostDetail
│       ├── consultation/               # CHANGED: meeting-link based
│       └── shared/NotificationsScreen.kt # NEW
└── viewmodel/
    ├── ViewModels.kt                   # CHANGED: split into per-aggregate files
    └── *ViewModel.kt                   # NEW (Sec 8.3)
```

> **Retire (but keep in repo for reference):** `data/repository/GeminiIntakeRepository.kt`
> (logic moves into `AIIntakeEngine`), and the WebView-based parts of `MainActivity.kt`.

---

### Final note to the implementer
Implement **Phase 0 → Phase 8 in order**, gate every phase on `./gradlew testDebugUnitTest`,
and do **not** skip the RBAC filtering in repositories. This document is self-contained: if you
follow Sections 6 (data), 7 (screens), 8 (contracts), and 9 (acceptance criteria) exactly, you
will produce a complete, business-viable 5-role legal platform.













