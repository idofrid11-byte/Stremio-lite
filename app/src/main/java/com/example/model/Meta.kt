package com.example.model

import com.google.gson.annotations.SerializedName

data class CatalogResponse(
    @SerializedName("metas") val metas: List<MetaItem>? = emptyList()
)

data class MetaDetailResponse(
    @SerializedName("meta") val meta: MetaItem? = null
)

data class MetaItem(
    @SerializedName("id") val id: String = "",
    @SerializedName("type") val type: String = "movie",
    @SerializedName("name") val name: String = "",
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("background") val background: String? = null,
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("releaseInfo") val releaseInfo: String? = null,
    @SerializedName("year") val year: String? = null,
    @SerializedName("genres") val genres: List<String>? = null,
    @SerializedName("imdbRating") val imdbRating: String? = null,
    @SerializedName("runtime") val runtime: String? = null,
    @SerializedName("director") val director: List<String>? = null,
    @SerializedName("cast") val cast: List<String>? = null,
    @SerializedName("videos") val videos: List<VideoItem>? = null
) {
    val displayYear: String
        get() = releaseInfo ?: year ?: ""

    val displayGenres: String
        get() = genres?.joinToString(" • ") ?: ""
}

data class VideoItem(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("season") val season: Int? = null,
    @SerializedName("episode") val episode: Int? = null,
    @SerializedName("released") val released: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("imdbRating") val imdbRating: String? = null,
    @SerializedName("overview") val overview: String? = null
) {
    val displayThumbnail: String?
        get() = thumbnail ?: image

    val displayTitle: String
        get() = title ?: name ?: if (season != null && episode != null) "S${season} E${episode}" else "Episode $id"
}
