package com.example.model

import com.google.gson.annotations.SerializedName

data class StreamResponse(
    @SerializedName("streams") val streams: List<StreamItem>? = emptyList()
)

data class StreamItem(
    @SerializedName("name") val name: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("ytId") val ytId: String? = null,
    @SerializedName("infoHash") val infoHash: String? = null,
    @SerializedName("fileIdx") val fileIdx: Int? = null,
    @SerializedName("behaviorHints") val behaviorHints: BehaviorHints? = null,
    // Client-side helper metadata:
    var addonSource: String? = null
) {
    val displayTitle: String
        get() = name ?: addonSource ?: "Stream"

    val displaySubtitle: String
        get() = title ?: description ?: if (url != null) "Direct Playable Link" else "Stream"

    val isPlayableDirectly: Boolean
        get() = !url.isNullOrBlank() && (
            url.startsWith("http://", ignoreCase = true) ||
            url.startsWith("https://", ignoreCase = true)
        )
}

data class BehaviorHints(
    @SerializedName("notWebReady") val notWebReady: Boolean? = null,
    @SerializedName("proxyHeaders") val proxyHeaders: Map<String, String>? = null
)
