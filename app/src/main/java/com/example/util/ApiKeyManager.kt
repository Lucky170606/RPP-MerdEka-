package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object ApiKeyManager {
    private const val TAG = "ApiKeyManager"
    private const val PREF_NAME = "secure_prefs"
    private const val FALLBACK_PREF_NAME = "app_api_key_prefs"
    private const val KEY_API_KEY = "gemini_api_key"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.w(TAG, "EncryptedSharedPreferences failed, falling back to standard SharedPreferences: ${e.message}")
            context.getSharedPreferences(FALLBACK_PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveApiKey(context: Context, apiKey: String) {
        val cleanKey = apiKey.trim()
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(KEY_API_KEY, cleanKey).apply()

        // Also save to standard fallback in case encrypted prefs fail on next boot
        try {
            val fallbackPrefs = context.getSharedPreferences(FALLBACK_PREF_NAME, Context.MODE_PRIVATE)
            fallbackPrefs.edit().putString(KEY_API_KEY, cleanKey).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving fallback prefs", e)
        }
    }

    fun clearApiKey(context: Context) {
        try {
            getSharedPreferences(context).edit().remove(KEY_API_KEY).apply()
        } catch (_: Exception) {}
        try {
            context.getSharedPreferences(FALLBACK_PREF_NAME, Context.MODE_PRIVATE).edit().remove(KEY_API_KEY).apply()
        } catch (_: Exception) {}
    }

    fun getApiKey(context: Context): String? {
        val userKey = try {
            val primary = getSharedPreferences(context).getString(KEY_API_KEY, null)
            if (!primary.isNullOrBlank()) primary else {
                context.getSharedPreferences(FALLBACK_PREF_NAME, Context.MODE_PRIVATE).getString(KEY_API_KEY, null)
            }
        } catch (e: Exception) {
            try {
                context.getSharedPreferences(FALLBACK_PREF_NAME, Context.MODE_PRIVATE).getString(KEY_API_KEY, null)
            } catch (_: Exception) {
                null
            }
        }

        if (!userKey.isNullOrBlank()) {
            val trimmed = userKey.trim()
            if (trimmed.isNotBlank() && trimmed != "DEFAULT_API_KEY") {
                return trimmed
            }
        }

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
