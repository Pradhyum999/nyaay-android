package com.nyaayneeti.app.data.firebase

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class FirebaseAuthManager {
    private val auth = FirebaseAuth.getInstance()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

    private val _autoCredential = MutableStateFlow<PhoneAuthCredential?>(null)

    val currentUser: FirebaseUser? get() = auth.currentUser

    fun sendOtp(
        phone: String,
        activity: Activity,
        onCodeSent: () -> Unit,
        onError: (String) -> Unit
    ) {
        _autoCredential.value = null
        val e164 = if (phone.startsWith("+")) phone else "+91${phone.filter { it.isDigit() }.takeLast(10)}"
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(e164)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    _autoCredential.value = credential
                    onCodeSent()
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onError(e.localizedMessage ?: "Verification failed. Check the number.")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _verificationId.value = verificationId
                    onCodeSent()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun verifyOtp(otp: String): Result<FirebaseUser> {
        val auto = _autoCredential.value
        val vid = _verificationId.value
        if (auto == null && vid == null) {
            return Result.failure(IllegalArgumentException("No verification in progress"))
        }
        val credential = auto ?: PhoneAuthProvider.getCredential(vid!!, otp)
        return suspendCancellableCoroutine { cont ->
            auth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) cont.resume(Result.success(user)) else cont.resume(Result.failure(IllegalArgumentException("Sign-in failed")))
                }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        return suspendCancellableCoroutine { cont ->
            auth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) cont.resume(Result.success(user)) else cont.resume(Result.failure(IllegalArgumentException("Google Sign-In failed")))
                }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }
    }

    fun signOut() {
        auth.signOut()
        _verificationId.value = null
        _autoCredential.value = null
    }
}
