package com.example.repository

import android.content.Context
import com.example.model.ContinueWatchingItem
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class ContinueWatchingRepository(context: Context) {
    private val prefs = context.getSharedPreferences("stremio_continue_watching", Context.MODE_PRIVATE)
    private val gson = GsonBuilder().setLenient().create()

    private val typeToken = object : TypeToken<List<ContinueWatchingItem>>() {}.type

    fun getContinueWatchingList(): List<ContinueWatchingItem> {
        val rawJson = prefs.getString("items_json", null) ?: return emptyList()
        return try {
            val list: List<ContinueWatchingItem> = gson.fromJson(rawJson, typeToken)
            list.sortedByDescending { it.lastWatchedTimestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveProgress(item: ContinueWatchingItem) {
        val currentList = getContinueWatchingList().toMutableList()

        // If watched more than 95%, remove it from continue watching (or if less than 5 seconds)
        if (item.durationMs > 0 && (item.positionMs.toFloat() / item.durationMs.toFloat() >= 0.95f)) {
            currentList.removeAll { it.id == item.id }
        } else if (item.positionMs > 5000L) { // Only save if watched for more than 5 seconds
            currentList.removeAll { it.id == item.id }
            currentList.add(0, item.copy(lastWatchedTimestamp = System.currentTimeMillis()))
        }

        // Limit to 20 recent items
        val trimmed = currentList.take(20)
        prefs.edit().putString("items_json", gson.toJson(trimmed)).apply()
    }

    fun removeItem(id: String) {
        val currentList = getContinueWatchingList().toMutableList()
        currentList.removeAll { it.id == id }
        prefs.edit().putString("items_json", gson.toJson(currentList)).apply()
    }

    fun clearAll() {
        prefs.edit().remove("items_json").apply()
    }
}
