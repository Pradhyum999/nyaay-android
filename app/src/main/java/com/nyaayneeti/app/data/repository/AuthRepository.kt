package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.model.Language
import com.nyaayneeti.app.data.model.UserProfile
import com.nyaayneeti.app.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(Language.ENGLISH)
    val selectedLanguage: StateFlow<Language> = _selectedLanguage.asStateFlow()

    private val testAccounts = mapOf(
        "9999999999" to "123456",
        "9876543210" to "123456",
        "7972542926" to "123456",
        "1234567890" to "123456"
    )

    fun setLanguage(language: Language) {
        _selectedLanguage.value = language
        _currentUser.value?.let { current ->
            _currentUser.value = current.copy(language = language)
        }
    }

    suspend fun sendOtp(phone: String): Result<String> {
        val cleanDigits = phone.filter { it.isDigit() }.takeLast(10)
        if (cleanDigits.length < 10) {
            return Result.failure(IllegalArgumentException("Please enter a valid 10-digit mobile number"))
        }
        // Returns verification ID
        return Result.success("vid-$cleanDigits-${System.currentTimeMillis()}")
    }

    suspend fun verifyOtp(phone: String, otp: String, chosenRole: UserRole): Result<UserProfile> {
        val cleanDigits = phone.filter { it.isDigit() }.takeLast(10)
        val validTestCode = testAccounts[cleanDigits] ?: "123456"

        if (otp != validTestCode && otp != "123456") {
            return Result.failure(IllegalArgumentException("Invalid 6-digit OTP code. For testing, use 123456."))
        }

        val name = if (chosenRole == UserRole.ADVOCATE) "Adv. Test Counsel" else "Citizen User"
        val profile = UserProfile(
            uid = "user-$cleanDigits",
            role = chosenRole,
            name = name,
            phone = "+91$cleanDigits",
            language = _selectedLanguage.value,
            onboardingCompleted = true
        )
        _currentUser.value = profile
        return Result.success(profile)
    }

    fun completeOnboarding(profile: UserProfile) {
        _currentUser.value = profile.copy(onboardingCompleted = true)
    }

    fun signOut() {
        _currentUser.value = null
    }
}
