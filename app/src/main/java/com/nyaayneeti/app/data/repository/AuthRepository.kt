package com.nyaayneeti.app.data.repository

import android.app.Activity
import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.FirebaseAuthManager
import com.nyaayneeti.app.data.local.UserDao
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.data.prefs.SessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepository(
    private val userDao: UserDao,
    private val sessionStore: SessionStore,
    private val firebaseAuth: FirebaseAuthManager? = null,
    private val cloud: CloudSync? = null,
    private val firebaseEnabled: Boolean = false
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow("system")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _sessionLoaded = MutableStateFlow(false)
    val sessionLoaded: StateFlow<Boolean> = _sessionLoaded.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = sessionStore.currentUid
        .flatMapLatest { uid -> if (uid == null) flowOf(null) else userDao.observe(uid) }
        .stateIn(scope, SharingStarted.Eagerly, null)

    init {
        scope.launch {
            sessionStore.currentUid.first()
            _selectedLanguage.value = sessionStore.language.first()
            _themeMode.value = sessionStore.themeMode.first()
            _sessionLoaded.value = true
        }
    }

    val verifiedLawyers: Flow<List<UserEntity>> = userDao.observeVerifiedLawyers()

    fun setLanguage(code: String) {
        _selectedLanguage.value = code
        scope.launch { sessionStore.setLanguage(code) }
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        scope.launch { sessionStore.setThemeMode(mode) }
    }

    fun sendOtp(phone: String, activity: Activity, onCodeSent: () -> Unit, onError: (String) -> Unit) {
        val digits = phone.filter { it.isDigit() }.takeLast(10)
        if (digits.length < 10) {
            onError("Enter a valid 10-digit mobile number")
            return
        }
        val e164 = if (phone.startsWith("+")) phone else "+91$digits"
        if (firebaseEnabled && firebaseAuth != null) {
            firebaseAuth.sendOtp(e164, activity, onCodeSent, onError)
        } else {
            // Local (offline) mode: no real SMS, just proceed to the OTP screen.
            onCodeSent()
        }
    }

    suspend fun verifyOtp(phone: String, otp: String, chosenRole: UserRole): Result<UserEntity> {
        val digits = phone.filter { it.isDigit() }.takeLast(10)
        val full = "+91$digits"

        val uid: String
        if (firebaseEnabled && firebaseAuth != null) {
            val fbUser = firebaseAuth.verifyOtp(otp).getOrElse { e -> return Result.failure(e) }
            uid = fbUser.uid
        } else {
            if (otp.length != 6 || otp != "123456") {
                return Result.failure(IllegalArgumentException("Invalid OTP. Use 123456 for testing."))
            }
            uid = "u-$digits"
        }

        val existing = userDao.getByPhone(full)
        val user = existing?.copy(role = chosenRole, language = _selectedLanguage.value)
            ?: UserEntity(
                uid = uid,
                phone = full,
                name = defaultName(chosenRole),
                role = chosenRole,
                language = _selectedLanguage.value
            )
        userDao.upsert(user)
        cloud?.pushUser(user)
        sessionStore.setUid(user.uid)
        return Result.success(user)
    }

    suspend fun signInWithGoogle(idToken: String, email: String?, name: String?, chosenRole: UserRole): Result<UserEntity> {
        val uid: String
        val phoneNum: String
        if (firebaseEnabled && firebaseAuth != null) {
            val fbUser = firebaseAuth.signInWithGoogle(idToken).getOrElse { e -> return Result.failure(e) }
            uid = fbUser.uid
            phoneNum = fbUser.phoneNumber ?: ""
        } else {
            uid = "g-${email ?: "user"}"
            phoneNum = ""
        }

        val existing = userDao.getById(uid)
        val user = existing?.copy(role = chosenRole, language = _selectedLanguage.value)
            ?: UserEntity(
                uid = uid,
                phone = phoneNum,
                email = email,
                name = name ?: defaultName(chosenRole),
                role = chosenRole,
                language = _selectedLanguage.value
            )
        userDao.upsert(user)
        cloud?.pushUser(user)
        sessionStore.setUid(user.uid)
        return Result.success(user)
    }

    suspend fun updateProfile(user: UserEntity): Result<Unit> {
        userDao.upsert(user)
        cloud?.pushUser(user)
        return Result.success(Unit)
    }

    suspend fun updateRole(role: UserRole) {
        val u = currentUser.value ?: return
        val updated = u.copy(role = role, language = _selectedLanguage.value)
        userDao.upsert(updated)
        cloud?.pushUser(updated)
    }

    suspend fun completeOnboarding(uid: String) {
        userDao.getById(uid)?.let {
            val updated = it.copy(onboardingCompleted = true)
            userDao.upsert(updated)
            cloud?.pushUser(updated)
        }
    }

    suspend fun getById(uid: String): UserEntity? = userDao.getById(uid)

    fun signOut() {
        firebaseAuth?.signOut()
        scope.launch { sessionStore.setUid(null) }
    }

    private fun defaultName(role: UserRole): String = when (role) {
        UserRole.INDEPENDENT_LAWYER, UserRole.FIRM_ADMIN, UserRole.FIRM_MEMBER -> "Advocate"
        UserRole.STUDENT -> "Student"
        UserRole.CITIZEN -> "Citizen"
        UserRole.PLATFORM_ADMIN -> "Admin"
    }
}
