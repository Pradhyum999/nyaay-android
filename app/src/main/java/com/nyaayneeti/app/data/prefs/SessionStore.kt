package com.nyaayneeti.app.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

class SessionStore(private val context: Context) {
    private val uidKey = stringPreferencesKey("current_uid")
    private val langKey = stringPreferencesKey("language")
    private val themeKey = stringPreferencesKey("theme_mode")

    val currentUid: Flow<String?> = context.dataStore.data.map { it[uidKey] }
    val language: Flow<String> = context.dataStore.data.map { it[langKey] ?: "en" }
    val themeMode: Flow<String> = context.dataStore.data.map { it[themeKey] ?: "system" }

    suspend fun setUid(uid: String?) {
        context.dataStore.edit { prefs -> if (uid == null) prefs.remove(uidKey) else prefs[uidKey] = uid }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[langKey] = lang }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[themeKey] = mode }
    }
}
