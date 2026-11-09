# NYAAYNEETI Android (न्यायनीति) ⚖

> **Legal Operating System for India** — *"Your case. One place."*  
> Built natively with **Kotlin & Jetpack Compose**, following official **[Google Android Skills](https://github.com/android/skills.git)**.

---

## 🏛 Strategic Mission
Nyayneeti is not just another chatbot; it is the **operating system for the lawyer-client relationship**. It creates a persistent **digital home for every legal case**, bringing clarity, structured AI intake, and transparent court diary management to citizens and advocates across India.

---

## 🛠 Tech Stack & Architecture (Google Android Skills)

| Layer | Technology | Specification / Skill Applied |
| :--- | :--- | :--- |
| **Language** | Kotlin 2.0.21 | Modern Kotlin with built-in Compose Compiler plugin |
| **UI Framework** | Jetpack Compose (Material 3) | Declarative UI, Luxury Obsidian & Gold judicial theme |
| **System Integration** | Edge-to-Edge (`enableEdgeToEdge`) | Grounded in `skills/system/edge-to-edge/SKILL.md` (Target SDK 35) |
| **Build System** | AGP 8.8.0 + Gradle 8.10.2 | Version Catalog (`gradle/libs.versions.toml`) |
| **Navigation** | Navigation Compose | Type-safe route backstack handling |
| **State & Async** | Kotlinx Coroutines & StateFlow | Reactive, lifecycle-aware UI architecture |
| **AI Intelligence** | Gemini Legal Intake Engine | Structured matter intake, legal area detection & dossier drafting |
| **Testing** | JUnit 4 + Coroutines Test | Test suite for case creation, cause lists, and timelines |

---

## 📱 Core Features Ported from NYAAYNEETI Web

### 1. Trilingual Accessibility (त्रिभाषिक)
- Seamless in-app switching between **English**, **हिन्दी (Hindi)**, and **मराठी (Marathi)**.
- Reactive localization without app restarts.

### 2. Dual User Experience (नागरिक व अधिवक्ता)
- **Advocate Workstation**:
  - Today's Court Diary & Cause List (Court room, item numbers, stages).
  - Priority Queue: Normal & Urgent matter triage.
  - Case Dossier with strategy notes & hearing history.
- **Citizen Portal**:
  - Digital Case Room with real-time status and assigned advocate details.
  - "Build My Case" AI intake assistant.
  - Interactive task checklists (document upload requirements).

### 3. "Build My Case" (Structured AI Intake)
- Multi-turn conversational intake powered by Gemini.
- Automatically extracts matter type, legal area, facts, missing details, and required document checklist.
- One-tap transition into a persistent **Case Room**.

### 4. Verified Phone OTP Authentication
- Clean 10-digit phone number entry with strictly non-duplicated `+91` prefix.
- Instant test verification for development (`99999 99999` with OTP `123456`).

### 5. Encrypted Audio/Video Consultation Interface
- Full consultation room with mute, video toggles, and call timer.

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Ladybug | 2024.2.1 or newer recommended)
- **JDK 17**
- **Android SDK Platform 35**

### Clone & Open in Android Studio
```bash
git clone https://github.com/Pradhyum999/nyaay-android.git
cd nyaay-android
```
1. In Android Studio, select **File > Open...** and navigate to `nyaay-android`.
2. Allow Gradle sync to complete using the Version Catalog (`gradle/libs.versions.toml`).
3. Select an emulator or physical device running Android 8.0 (API 26) or higher.
4. Click **Run 'app'** (`Shift + F10`).

### Running Unit Tests
```bash
./gradlew testDebugUnitTest
```

---

## 📂 Project Structure
```
nyaay-android/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/nyaayneeti/app/
│       │   │   ├── MainActivity.kt               # Edge-to-Edge entry point
│       │   │   ├── NyaayApplication.kt
│       │   │   ├── data/
│       │   │   │   ├── model/Models.kt           # Domain models
│       │   │   │   └── repository/               # Auth, Case & Gemini Repositories
│       │   │   ├── ui/
│       │   │   │   ├── theme/                    # Material 3 Obsidian/Gold Theme
│       │   │   │   ├── navigation/               # Type-safe NavHost
│       │   │   │   ├── components/               # TopAppBar, Cards, Badges
│       │   │   │   └── screens/                  # Auth, Citizen, Advocate & Consultation
│       │   │   └── viewmodel/ViewModels.kt
│       │   └── res/                              # Values, vectors, themes, icons
│       └── test/java/com/nyaayneeti/app/
│           └── CaseRepositoryTest.kt             # Automated test suite
├── gradle/
│   └── libs.versions.toml                        # Version catalog
├── settings.gradle.kts
└── build.gradle.kts
```
