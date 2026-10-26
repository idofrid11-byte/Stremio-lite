package com.example.model

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class AddonManifest(
    @SerializedName("id") val id: String = "",
    @SerializedName("version") val version: String = "1.0.0",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("background") val background: String? = null,
    @SerializedName("types") val types: List<String> = emptyList(),
    @SerializedName("catalogs") val catalogs: List<AddonCatalog> = emptyList(),
    @SerializedName("resources") val rawResources: JsonElement? = null
) {
    /**
     * Stremio allows 'resources' to be either List<String> or List<ResourceObject>.
     * This helper extracts supported resource names reliably.
     */
    fun getSupportedResources(): List<String> {
        val element = rawResources ?: return listOf("catalog", "meta", "stream")
        return try {
            if (element.isJsonArray) {
                val array = element.asJsonArray
                array.mapNotNull { item ->
                    if (item.isJsonPrimitive) {
                        item.asString
                    } else if (item.isJsonObject) {
                        item.asJsonObject.get("name")?.asString
                    } else null
                }
            } else emptyList()
        } catch (e: Exception) {
            listOf("catalog", "meta", "stream")
        }
    }

    fun supportsResource(resource: String): Boolean {
        val list = getSupportedResources()
        return list.any { it.equals(resource, ignoreCase = true) }
    }
}

data class AddonCatalog(
    @SerializedName("type") val type: String = "movie",
    @SerializedName("id") val id: String = "top",
    @SerializedName("name") val name: String? = null,
    @SerializedName("extra") val extra: List<CatalogExtra>? = null
)

data class CatalogExtra(
    @SerializedName("name") val name: String = "",
    @SerializedName("isRequired") val isRequired: Boolean? = false,
    @SerializedName("options") val options: List<String>? = null
)
