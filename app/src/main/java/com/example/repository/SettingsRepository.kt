package com.example.repository

import android.content.Context

class SettingsRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("stremio_global_settings", Context.MODE_PRIVATE)

    var defaultSubtitleSize: Float
        get() = prefs.getFloat("sub_size", 1.0f)
        set(value) = prefs.edit().putFloat("sub_size", value).apply()

    var defaultVolumeBoost: Boolean
        get() = prefs.getBoolean("volume_boost", false)
        set(value) = prefs.edit().putBoolean("volume_boost", value).apply()

    var preferredAudioLanguage: String
        get() = prefs.getString("audio_lang", "English") ?: "English"
        set(value) = prefs.edit().putString("audio_lang", value).apply()

    var defaultPlaybackSpeed: Float
        get() = prefs.getFloat("playback_speed", 1.0f)
        set(value) = prefs.edit().putFloat("playback_speed", value).apply()

    var autoPlayNextEpisode: Boolean
        get() = prefs.getBoolean("autoplay_next", true)
        set(value) = prefs.edit().putBoolean("autoplay_next", value).apply()

    var hardwareDecoding: Boolean
        get() = prefs.getBoolean("hw_decoding", true)
        set(value) = prefs.edit().putBoolean("hw_decoding", value).apply()

    var preferredSubtitleLang: String
        get() = prefs.getString("sub_lang", "English") ?: "English"
        set(value) = prefs.edit().putString("sub_lang", value).apply()

    var wifiOnlyStreaming: Boolean
        get() = prefs.getBoolean("wifi_only", false)
        set(value) = prefs.edit().putBoolean("wifi_only", value).apply()

    var darkModeEnabled: Boolean
        get() = prefs.getBoolean("dark_mode", true)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()

    fun clearCache(): Long {
        return try {
            var bytesFreed = 0L
            val cacheDir = context.cacheDir
            cacheDir.listFiles()?.forEach { file ->
                bytesFreed += file.length()
                file.deleteRecursively()
            }
            bytesFreed
        } catch (e: Exception) {
            0L
        }
    }
}
