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
        val userKey = prefs.getString(KEY_API_KEY, null)
        if (!userKey.isNullOrBlank()) return userKey.trim()

        // Fallback to BuildConfig injected from Secrets (.env)
        val buildKey = try {
            val clazz = Class.forName("com.example.BuildConfig")
            val field = try {
                clazz.getField("GEMINI_API_KEY")
            } catch (e: NoSuchFieldException) {
                try { clazz.getField("API_KEY") } catch (e2: NoSuchFieldException) { null }
            }
            field?.get(null) as? String
        } catch (e: Exception) {
            null
        }

        if (!buildKey.isNullOrBlank() && buildKey != "DEFAULT_API_KEY" && buildKey != "your_api_key_here") {
            return buildKey.trim()
        }

        return null
    }
}
