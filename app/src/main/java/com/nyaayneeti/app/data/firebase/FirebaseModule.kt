package com.nyaayneeti.app.data.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nyaayneeti.app.R

object FirebaseModule {
    @Volatile private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (!initialized) {
                if (FirebaseApp.getApps(context.applicationContext).isEmpty()) {
                    FirebaseApp.initializeApp(context.applicationContext)
                }
                initialized = true
            }
        }
    }

    val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    /** True only when a real (non-placeholder) google-services.json is present. */
    fun isConfigured(context: Context): Boolean {
        return try {
            val key = context.getString(R.string.google_api_key)
            !key.contains("PLACEHOLDER", ignoreCase = true) && key.isNotBlank()
        } catch (e: Exception) {
            false
        }
    }
}
