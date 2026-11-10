# Firebase Setup Guide (Production)

The app is now **Firebase-ready**: real Phone Auth + Firestore write-through sync +
FCM + security rules are all wired in the code. The app **still builds and runs locally**
without Firebase (it falls back to the offline Room + test OTP `123456`). To go live, follow
these steps once — everything is on the **free (Spark) tier**.

## 1. Get your `google-services.json`
1. Open https://console.firebase.google.com and select (or create) your existing project.
2. **Project settings → Your apps → Add app → Android**.
3. Package name: **`com.nyaayneeti.app`** (must match exactly).
4. Register the app, then **download `google-services.json`**.
5. Replace the placeholder file at `app/google-services.json` with the real one.

> Until you do this, the app runs in offline mode (OTP `123456`, data on-device only).

## 2. Add your SHA-1 fingerprint (required for Phone Auth)
1. In the same Firebase project: **Project settings → Your apps → your Android app**.
2. Click **Add fingerprint**.
3. Get your debug SHA-1 with:
   ```
   keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android
   ```
4. Paste the `SHA1:` value and save. (Also add your release keystore SHA-1 when you ship.)

## 3. Enable Phone Authentication
1. Firebase console → **Build → Authentication → Sign-in method**.
2. Click **Phone** → enable it. (Optionally add test phone numbers for free testing.)

## 4. Create Firestore database
1. Firebase console → **Build → Firestore Database → Create database**.
2. Choose a location and start in **production mode**.
3. Go to the **Rules** tab, delete the default rules, and **paste the entire contents of
   `firestore.rules`** (in this repo), then **Publish**.

## 5. (Optional) Enable push notifications
1. Firebase console → **Build → Cloud Messaging**.
2. The `NyaayMessagingService` is already registered in the manifest. FCM works on the free tier.

## 6. Rebuild & run
- Delete the placeholder `google-services.json` first if you haven't replaced it.
- Sync Gradle (the `com.google.gms.google-services` plugin will pick up the real config).
- Run the app. Login uses **real SMS OTP** now. All data writes go to Firestore.

## What is already implemented
- **Phone Auth** — real OTP via Firebase (falls back to `123456` offline).
- **Firestore write-through** — users, firms, cases, chat (conversations+messages),
  consultations, internships, applications, forum posts, comments, notifications, reviews,
  subscriptions, payments all push to Firestore on every change.
- **Security rules** — role-aware (clients/lawyers only see their own cases, chat participants
  only see their own messages, etc.).
- **FCM service** registered.

## What you still do on the backend (not code)
- Real **payment gateway** (Razorpay/Stripe) — currently payments are simulated.
- Real **video calling** (native WebRTC / Agora / 100ms) — currently uses a Jitsi meeting link.
- Real **AI intake** (Gemini API key) — currently a rule-based engine behind `AIIntakeEngine`.
- **Play Store release signing** (upload keystore + bundle).

## Free-tier limits to know (Spark plan)
- Firestore: 1 GiB storage, 50K reads / 20K writes / 20K deletes per day.
- Phone Auth: 10K SMS verifications/month (US/Canada/India pricing differs).
- Cloud Messaging: free.
These are plenty for a launch and scale-up to Blaze when needed.

## 7. AI keys (NVIDIA NIM + eCourts) — already wired in code
The native app now uses the same AI stack as your web app. Put the **full** keys in
`~/.gradle/gradle.properties` (or the project `local.properties`), then rebuild:

```
NVIDIA_NIM_API_KEY=nvapi-xxxxxxxx
ECOURTS_API_KEY=eci_live_xxxxxxxx
ECOURTS_BASE_URL=https://your-ecourts-endpoint
```

- **NVIDIA NIM** (`NimIntakeEngine`) powers "Build My Case" — it calls
  `https://integrate.api.nvidia.com/v1/chat/completions` and falls back to the offline
  rule-based engine if the key is blank or the call fails.
- **eCourts** (`ECourtsClient`) provides live case-number search/verification via
  `ECOURTS_BASE_URL` with an `X-API-Key` header. Wire it to a screen when you confirm the
  exact response schema for your provider.

> Leave the keys blank to keep running the app fully offline.

## 8. Google Sign-In (must enable in console)
The app now has a **Sign Up / Sign In** screen with "Continue with Google". To make it work:
1. Firebase console → **Authentication → Sign-in method → Google → toggle Enable → Save**.
   (This is the #1 reason "Continue with Google" fails.)
2. Make sure your **SHA-1** is added (step 2 above) — Google Sign-In also requires it.
3. No code change needed: the app reads the Web client ID automatically from
   `google-services.json` (`default_web_client_id`).
4. On failure the app now shows the actual error instead of failing silently.

> Note: `GoogleSignIn` / `GoogleSignInOptions` are deprecated by Google in favor of Credential
> Manager; they still work. We can migrate to Credential Manager later if you want zero warnings.


