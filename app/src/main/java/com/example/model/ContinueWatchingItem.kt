package com.example.model

import com.google.gson.annotations.SerializedName

data class ContinueWatchingItem(
    @SerializedName("id") val id: String, // unique key, e.g. "$metaId:$season:$episode" or metaId
    @SerializedName("metaId") val metaId: String,
    @SerializedName("addonUrl") val addonUrl: String,
    @SerializedName("type") val type: String = "movie",
    @SerializedName("title") val title: String,
    @SerializedName("episodeTitle") val episodeTitle: String? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("background") val background: String? = null,
    @SerializedName("season") val season: Int? = null,
    @SerializedName("episode") val episode: Int? = null,
    @SerializedName("streamUrl") val streamUrl: String,
    @SerializedName("positionMs") val positionMs: Long = 0L,
    @SerializedName("durationMs") val durationMs: Long = 0L,
    @SerializedName("lastWatchedTimestamp") val lastWatchedTimestamp: Long = System.currentTimeMillis()
) {
    val progressFraction: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val displaySubtitle: String
        get() {
            val epInfo = if (season != null && episode != null) "S${season} E${episode}" else type.replaceFirstChar { it.uppercase() }
            val remainingMs = (durationMs - positionMs).coerceAtLeast(0L)
            val remainingMin = remainingMs / 60000L
            return if (remainingMin > 0) "$epInfo • ${remainingMin}m left" else epInfo
        }
}
