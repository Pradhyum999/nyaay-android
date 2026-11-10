package com.nyaayneeti.app.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nyaayneeti.app.BuildConfig
import com.nyaayneeti.app.R
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel

@Composable
fun PhoneAuthScreen(vm: AuthViewModel, onDone: () -> Unit, onBack: () -> Unit) {
    val phone by vm.phone.collectAsState()
    val otp by vm.otp.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    var otpSent by remember { mutableStateOf(false) }
    val activity = LocalContext.current as Activity
    val defaultWebClientId = LocalContext.current.getString(R.string.default_web_client_id)
    var isSignUp by remember { mutableStateOf(true) }
    var localError by remember { mutableStateOf<String?>(null) }

    val googleSignInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK || result.data == null) {
            localError = "Google sign-in was cancelled."
            return@rememberLauncherForActivityResult
        }
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken == null) {
                localError = "Google sign-in returned no token. Enable Google Sign-In in Firebase."
            } else {
                vm.signInWithGoogle(idToken, account.email, account.displayName) { onDone() }
            }
        } catch (e: ApiException) {
            localError = if (e.statusCode == 12501) null else "Google sign-in failed (${e.statusCode})."
        } catch (e: Exception) {
            localError = "Google sign-in failed: ${e.message}"
        }
    }

    Scaffold(topBar = { NyaayTopAppBar(if (isSignUp) "Sign Up" else "Sign In", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = isSignUp, onClick = { isSignUp = true }, label = { Text("Sign Up") }, modifier = Modifier.weight(1f), colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub))
                FilterChip(selected = !isSignUp, onClick = { isSignUp = false }, label = { Text("Sign In") }, modifier = Modifier.weight(1f), colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub))
            }
            Spacer(Modifier.height(20.dp))
            Text(if (isSignUp) "Create your account" else "Welcome back", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                if (isSignUp) "New to NYAAYNEETI? Sign up with Google or your mobile number."
                else "Sign in to your legal workspace and records.",
                style = MaterialTheme.typography.bodyMedium, color = TextSub
            )
            Spacer(Modifier.height(24.dp))

            if (!otpSent) {
                Button(
                    onClick = {
                        localError = null
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(defaultWebClientId).requestEmail().build()
                        googleSignInLauncher.launch(GoogleSignIn.getClient(activity, gso).signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text("Continue with Google", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.weight(1f), color = ObsidianBorder)
                    Text("  OR  ", color = TextSub, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(Modifier.weight(1f), color = ObsidianBorder)
                }
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = phone, onValueChange = { vm.onPhone(it) },
                    label = { Text("Mobile number") },
                    leadingIcon = { Text("+91", color = GoldPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors()
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { localError = null; vm.sendOtp(activity) { otpSent = true } },
                    enabled = phone.length == 10 && !loading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = OnPrimary)
                    else Text(if (isSignUp) "Sign up with mobile" else "Send OTP", color = OnPrimary, fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedTextField(
                    value = otp, onValueChange = { vm.onOtp(it) },
                    label = { Text("6-digit OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors()
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { vm.verifyOtp { onDone() } },
                    enabled = otp.length == 6 && !loading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = OnPrimary)
                    else Text("Verify & Continue", color = OnPrimary, fontWeight = FontWeight.Bold)
                }
                if (BuildConfig.DEBUG) {
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { vm.onOtp("123456") }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Auto-fill 123456 (debug)", color = GoldPrimary, fontSize = 12.sp)
                    }
                }
            }

            (localError ?: error)?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = RedUrgent, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
