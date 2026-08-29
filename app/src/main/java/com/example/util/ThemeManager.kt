package com.example.util

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val id: String, val title: String) {
    SYSTEM("system", "Ikuti Pengaturan HP"),
    LIGHT("light", "Mode Terang (Cerah & Bersih)"),
    DARK("dark", "Mode Gelap (Malam)")
}

enum class AppThemeStyle(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryPreview: Color,
    val secondaryPreview: Color,
    val accentPreview: Color
) {
    MODERN_EDU(
        id = "modern_edu",
        title = "Modern Edu (Bawaan)",
        subtitle = "Biru Safir & Toska Modern",
        primaryPreview = Color(0xFF1E3A8A),
        secondaryPreview = Color(0xFF0D9488),
        accentPreview = Color(0xFFD97706)
    ),
    FORMAL_NAVY(
        id = "formal_navy",
        title = "Klasik Kemendikbud",
        subtitle = "Biru Dongker & Emas Resmi",
        primaryPreview = Color(0xFF0F2B5C),
        secondaryPreview = Color(0xFF0284C7),
        accentPreview = Color(0xFFB45309)
    ),
    SAGE_NATURE(
        id = "sage_nature",
        title = "Sage Green Soft",
        subtitle = "Hijau Teduh, Tenang di Mata",
        primaryPreview = Color(0xFF2D6A4F),
        secondaryPreview = Color(0xFF52B788),
        accentPreview = Color(0xFF74C69D)
    ),
    WARM_TERRACOTTA(
        id = "warm_terracotta",
        title = "Warm Terracotta",
        subtitle = "Krem Hangat & Ceria Edukasi",
        primaryPreview = Color(0xFF9C4221),
        secondaryPreview = Color(0xFFD97706),
        accentPreview = Color(0xFFF59E0B)
    ),
    ROYAL_PURPLE(
        id = "royal_purple",
        title = "Royal Lavender",
        subtitle = "Ungu Elegan & Kreatif",
        primaryPreview = Color(0xFF6D28D9),
        secondaryPreview = Color(0xFF8B5CF6),
        accentPreview = Color(0xFFA855F7)
    ),
    CLEAN_SLATE(
        id = "clean_slate",
        title = "Minimalis Monokrom",
        subtitle = "Abu-abu Arang & Bersih",
        primaryPreview = Color(0xFF334155),
        secondaryPreview = Color(0xFF475569),
        accentPreview = Color(0xFF64748B)
    );

    fun lightScheme(): ColorScheme {
        return when (this) {
            MODERN_EDU -> lightColorScheme(
                primary = Color(0xFF1E3A8A),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFEFF6FF),
                onPrimaryContainer = Color(0xFF0F172A),
                secondary = Color(0xFF0D9488),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFF0FDFA),
                onSecondaryContainer = Color(0xFF0F766E),
                tertiary = Color(0xFFD97706),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFFEF3C7),
                onTertiaryContainer = Color(0xFFB45309),
                background = Color(0xFFF8FAFC),
                onBackground = Color(0xFF0F172A),
                surface = Color.White,
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFF1F5F9),
                onSurfaceVariant = Color(0xFF334155),
                outline = Color(0xFFCBD5E1)
            )
            FORMAL_NAVY -> lightColorScheme(
                primary = Color(0xFF0F2B5C),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE8EEF8),
                onPrimaryContainer = Color(0xFF0A1C3B),
                secondary = Color(0xFF0284C7),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFE0F2FE),
                onSecondaryContainer = Color(0xFF0369A1),
                tertiary = Color(0xFFB45309),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFFEF3C7),
                onTertiaryContainer = Color(0xFF78350F),
                background = Color(0xFFF4F6F9),
                onBackground = Color(0xFF0F172A),
                surface = Color.White,
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFE2E8F0),
                onSurfaceVariant = Color(0xFF334155),
                outline = Color(0xFF94A3B8)
            )
            SAGE_NATURE -> lightColorScheme(
                primary = Color(0xFF2D6A4F),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE8F5EE),
                onPrimaryContainer = Color(0xFF1B4332),
                secondary = Color(0xFF40916C),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFD8F3DC),
                onSecondaryContainer = Color(0xFF1B4332),
                tertiary = Color(0xFF8B5E3C),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFF2ECE6),
                onTertiaryContainer = Color(0xFF583B26),
                background = Color(0xFFF2F7F4),
                onBackground = Color(0xFF1C2826),
                surface = Color.White,
                onSurface = Color(0xFF1C2826),
                surfaceVariant = Color(0xFFE0ECE6),
                onSurfaceVariant = Color(0xFF2D4438),
                outline = Color(0xFFA3C4BC)
            )
            WARM_TERRACOTTA -> lightColorScheme(
                primary = Color(0xFF9C4221),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFDF2EB),
                onPrimaryContainer = Color(0xFF6B260F),
                secondary = Color(0xFFD97706),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFFEF3C7),
                onSecondaryContainer = Color(0xFF92400E),
                tertiary = Color(0xFF4B5563),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFF3F4F6),
                onTertiaryContainer = Color(0xFF1F2937),
                background = Color(0xFFFAF5F0),
                onBackground = Color(0xFF292524),
                surface = Color.White,
                onSurface = Color(0xFF292524),
                surfaceVariant = Color(0xFFF4EAE3),
                onSurfaceVariant = Color(0xFF57534E),
                outline = Color(0xFFD6C7BF)
            )
            ROYAL_PURPLE -> lightColorScheme(
                primary = Color(0xFF6D28D9),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFF5F3FF),
                onPrimaryContainer = Color(0xFF4C1D95),
                secondary = Color(0xFF8B5CF6),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFEDE9FE),
                onSecondaryContainer = Color(0xFF5B21B6),
                tertiary = Color(0xFF0284C7),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFE0F2FE),
                onTertiaryContainer = Color(0xFF0369A1),
                background = Color(0xFFF8F5FF),
                onBackground = Color(0xFF1E1B4B),
                surface = Color.White,
                onSurface = Color(0xFF1E1B4B),
                surfaceVariant = Color(0xFFEBE3FE),
                onSurfaceVariant = Color(0xFF4C1D95),
                outline = Color(0xFFC4B5FD)
            )
            CLEAN_SLATE -> lightColorScheme(
                primary = Color(0xFF334155),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFF1F5F9),
                onPrimaryContainer = Color(0xFF0F172A),
                secondary = Color(0xFF475569),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFE2E8F0),
                onSecondaryContainer = Color(0xFF1E293B),
                tertiary = Color(0xFF2563EB),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFEFF6FF),
                onTertiaryContainer = Color(0xFF1E40AF),
                background = Color(0xFFF6F8FA),
                onBackground = Color(0xFF0F172A),
                surface = Color.White,
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFE5E9EE),
                onSurfaceVariant = Color(0xFF334155),
                outline = Color(0xFF94A3B8)
            )
        }
    }

    fun darkScheme(): ColorScheme {
        return when (this) {
            MODERN_EDU -> darkColorScheme(
                primary = Color(0xFF93C5FD),
                onPrimary = Color(0xFF0F172A),
                primaryContainer = Color(0xFF1E3A8A),
                onPrimaryContainer = Color(0xFFDBEAFE),
                secondary = Color(0xFF5EEAD4),
                onSecondary = Color(0xFF042F2E),
                secondaryContainer = Color(0xFF0F766E),
                onSecondaryContainer = Color(0xFFCCFBF1),
                tertiary = Color(0xFFFBBF24),
                onTertiary = Color(0xFF451A03),
                background = Color(0xFF0B132B),
                onBackground = Color(0xFFF8FAFC),
                surface = Color(0xFF1C2541),
                onSurface = Color(0xFFF8FAFC),
                surfaceVariant = Color(0xFF283556),
                onSurfaceVariant = Color(0xFFCBD5E1),
                outline = Color(0xFF475569)
            )
            FORMAL_NAVY -> darkColorScheme(
                primary = Color(0xFF90CDF4),
                onPrimary = Color(0xFF0A1C3B),
                primaryContainer = Color(0xFF0F2B5C),
                onPrimaryContainer = Color(0xFFE0F2FE),
                secondary = Color(0xFF7DD3FC),
                onSecondary = Color(0xFF082F49),
                secondaryContainer = Color(0xFF0369A1),
                onSecondaryContainer = Color(0xFFE0F2FE),
                tertiary = Color(0xFFFBBF24),
                onTertiary = Color(0xFF451A03),
                background = Color(0xFF081224),
                onBackground = Color(0xFFF0F6FC),
                surface = Color(0xFF0F1E38),
                onSurface = Color(0xFFF0F6FC),
                surfaceVariant = Color(0xFF1B2F52),
                onSurfaceVariant = Color(0xFFCBD5E1),
                outline = Color(0xFF4A628A)
            )
            SAGE_NATURE -> darkColorScheme(
                primary = Color(0xFF74C69D),
                onPrimary = Color(0xFF081C13),
                primaryContainer = Color(0xFF1B4332),
                onPrimaryContainer = Color(0xFFD8F3DC),
                secondary = Color(0xFF95D5B2),
                onSecondary = Color(0xFF081C13),
                secondaryContainer = Color(0xFF2D6A4F),
                onSecondaryContainer = Color(0xFFD8F3DC),
                tertiary = Color(0xFFD8B4E2),
                onTertiary = Color(0xFF2A1035),
                background = Color(0xFF0B1711),
                onBackground = Color(0xFFEFF7F2),
                surface = Color(0xFF13271D),
                onSurface = Color(0xFFEFF7F2),
                surfaceVariant = Color(0xFF1D382A),
                onSurfaceVariant = Color(0xFFC5DFD0),
                outline = Color(0xFF3B614D)
            )
            WARM_TERRACOTTA -> darkColorScheme(
                primary = Color(0xFFFDBA74),
                onPrimary = Color(0xFF371204),
                primaryContainer = Color(0xFF7C2D12),
                onPrimaryContainer = Color(0xFFFFEDD5),
                secondary = Color(0xFFFCD34D),
                onSecondary = Color(0xFF3A1C02),
                secondaryContainer = Color(0xFF92400E),
                onSecondaryContainer = Color(0xFFFEF3C7),
                tertiary = Color(0xFFA7F3D0),
                onTertiary = Color(0xFF042F2E),
                background = Color(0xFF170E0B),
                onBackground = Color(0xFFFBF6F3),
                surface = Color(0xFF251814),
                onSurface = Color(0xFFFBF6F3),
                surfaceVariant = Color(0xFF382520),
                onSurfaceVariant = Color(0xFFE4D3CC),
                outline = Color(0xFF64433A)
            )
            ROYAL_PURPLE -> darkColorScheme(
                primary = Color(0xFFC4B5FD),
                onPrimary = Color(0xFF2E1065),
                primaryContainer = Color(0xFF5B21B6),
                onPrimaryContainer = Color(0xFFEDE9FE),
                secondary = Color(0xFFA78BFA),
                onSecondary = Color(0xFF2E1065),
                secondaryContainer = Color(0xFF6D28D9),
                onSecondaryContainer = Color(0xFFEDE9FE),
                tertiary = Color(0xFF67E8F9),
                onTertiary = Color(0xFF083344),
                background = Color(0xFF110A21),
                onBackground = Color(0xFFF6F3FE),
                surface = Color(0xFF1D1334),
                onSurface = Color(0xFFF6F3FE),
                surfaceVariant = Color(0xFF2E2050),
                onSurfaceVariant = Color(0xFFDED5F8),
                outline = Color(0xFF58418A)
            )
            CLEAN_SLATE -> darkColorScheme(
                primary = Color(0xFFCBD5E1),
                onPrimary = Color(0xFF0F172A),
                primaryContainer = Color(0xFF334155),
                onPrimaryContainer = Color(0xFFF1F5F9),
                secondary = Color(0xFF94A3B8),
                onSecondary = Color(0xFF0F172A),
                secondaryContainer = Color(0xFF475569),
                onSecondaryContainer = Color(0xFFF1F5F9),
                tertiary = Color(0xFF60A5FA),
                onTertiary = Color(0xFF082F49),
                background = Color(0xFF0F1115),
                onBackground = Color(0xFFF1F3F5),
                surface = Color(0xFF1A1E24),
                onSurface = Color(0xFFF1F3F5),
                surfaceVariant = Color(0xFF272D36),
                onSurfaceVariant = Color(0xFFD0D6DC),
                outline = Color(0xFF46505E)
            )
        }
    }

    companion object {
        fun fromId(id: String?): AppThemeStyle {
            return values().firstOrNull { it.id == id } ?: MODERN_EDU
        }
    }
}

object ThemeManager {
    private const val PREFS_NAME = "rpp_theme_prefs"
    private const val KEY_THEME_STYLE = "key_app_theme_style"
    private const val KEY_THEME_MODE = "key_app_theme_mode"

    private val _currentTheme = MutableStateFlow(AppThemeStyle.MODERN_EDU)
    val currentTheme: StateFlow<AppThemeStyle> = _currentTheme.asStateFlow()

    private val _currentMode = MutableStateFlow(ThemeMode.SYSTEM)
    val currentMode: StateFlow<ThemeMode> = _currentMode.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedId = prefs.getString(KEY_THEME_STYLE, AppThemeStyle.MODERN_EDU.id)
        _currentTheme.value = AppThemeStyle.fromId(savedId)

        val savedMode = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.id)
        _currentMode.value = ThemeMode.values().firstOrNull { it.id == savedMode } ?: ThemeMode.SYSTEM
    }

    fun setTheme(context: Context, theme: AppThemeStyle) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME_STYLE, theme.id).apply()
        _currentTheme.value = theme
    }

    fun setMode(context: Context, mode: ThemeMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME_MODE, mode.id).apply()
        _currentMode.value = mode
    }
}
