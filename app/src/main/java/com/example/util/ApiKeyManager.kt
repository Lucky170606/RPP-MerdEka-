package com.example.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object ApiKeyManager {
    private const val PREF_NAME = "secure_prefs"
    private const val KEY_API_KEY = "gemini_api_key"

    fun getEncryptedPrefs(context: Context): EncryptedSharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    fun saveApiKey(context: Context, apiKey: String) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    fun getApiKey(context: Context): String? {
        val prefs = getEncryptedPrefs(context)
        return prefs.getString(KEY_API_KEY, null)
    }
}
