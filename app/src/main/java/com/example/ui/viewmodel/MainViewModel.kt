package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.repository.AddonPreset
import com.example.repository.AddonRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Search : Screen()
    object Watchlist : Screen()
    object AddonManager : Screen()
    data class Detail(val addonUrl: String, val type: String, val id: String) : Screen()
    data class Player(val streamUrl: String, val title: String, val subtitle: String) : Screen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository = AddonRepository(application)

    // Navigation Stack
    var currentScreen by mutableStateOf<Screen>(Screen.Home)
        private set

    private val screenStack = mutableListOf<Screen>(Screen.Home)

    // Data State
    var installedAddonUrls by mutableStateOf(repository.getInstalledAddonUrls())
        private set

    val manifests = mutableStateMapOf<String, AddonManifest>()
    val catalogs = mutableStateMapOf<String, List<MetaItem>>() // Key: "addonUrl_type_catalogId"

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Featured Hero item for Discover / Home
    var featuredItem by mutableStateOf<MetaItem?>(null)
        private set

    // Active Catalog Filter (All, movie, series)
    var selectedMediaTypeFilter by mutableStateOf("all")

    // Search State
    var searchQuery by mutableStateOf("")
    var isSearching by mutableStateOf(false)
        private set
    var searchResults by mutableStateOf<List<MetaItem>>(emptyList())
        private set
    private var searchJob: Job? = null

    // Selected Genre Filter for Search/Browse
    var selectedGenre by mutableStateOf<String?>(null)

    // Detail Screen State
    var currentDetailMeta by mutableStateOf<MetaItem?>(null)
        private set
    var isLoadingDetail by mutableStateOf(false)
        private set
    var currentStreams by mutableStateOf<List<StreamItem>>(emptyList())
        private set
    var isLoadingStreams by mutableStateOf(false)
        private set
    var selectedEpisodeId by mutableStateOf<String?>(null)
        private set

    // Watchlist State
    var watchlistItems by mutableStateOf<List<MetaItem>>(emptyList())
        private set

    // Addon Manager State
    var addonStatusMessage by mutableStateOf<String?>(null)
    var isInstallingAddon by mutableStateOf(false)

    val curatedPresets: List<AddonPreset> = AddonRepository.CURATED_ADDON_PRESETS

    init {
        loadAddonsAndCatalogs()
    }

    fun navigateTo(screen: Screen) {
        if (currentScreen != screen) {
            screenStack.add(screen)
            currentScreen = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            currentScreen = screenStack.last()
            return true
        }
        return false
    }

    fun loadAddonsAndCatalogs() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val urls = repository.getInstalledAddonUrls()
                installedAddonUrls = urls

                for (url in urls) {
                    try {
                        val manifest = repository.fetchManifest(url)
                        manifests[url] = manifest

                        for (cat in manifest.catalogs) {
                            val items = repository.fetchCatalog(url, cat.type, cat.id)
                            catalogs["${url}_${cat.type}_${cat.id}"] = items

                            if (featuredItem == null && items.isNotEmpty()) {
                                featuredItem = items.firstOrNull { !it.background.isNullOrBlank() || !it.poster.isNullOrBlank() }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                updateWatchlistFromLoaded()
            } catch (e: Exception) {
                errorMessage = "Failed to load addons: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addAddon(rawUrl: String) {
        val trimmed = rawUrl.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            isInstallingAddon = true
            addonStatusMessage = null
            try {
                val normalized = repository.normalizeUrl(trimmed)
                val manifest = repository.fetchManifest(normalized)
                val current = installedAddonUrls.toMutableList()
                if (!current.contains(normalized)) {
                    current.add(normalized)
                    repository.saveInstalledAddons(current)
                    installedAddonUrls = current
                    manifests[normalized] = manifest

                    // Fetch its catalogs
                    for (cat in manifest.catalogs) {
                        val items = repository.fetchCatalog(normalized, cat.type, cat.id)
                        catalogs["${normalized}_${cat.type}_${cat.id}"] = items
                    }
                    addonStatusMessage = "Successfully installed ${manifest.name}!"
                } else {
                    addonStatusMessage = "${manifest.name} is already installed."
                }
            } catch (e: Exception) {
                addonStatusMessage = "Failed to install addon: Check URL format or server status."
            } finally {
                isInstallingAddon = false
            }
        }
    }

    fun removeAddon(url: String) {
        val current = installedAddonUrls.toMutableList()
        current.remove(url)
        repository.saveInstalledAddons(current)
        installedAddonUrls = current
        manifests.remove(url)

        // Clean catalogs
        val keysToRemove = catalogs.keys.filter { it.startsWith(url) }
        keysToRemove.forEach { catalogs.remove(it) }

        addonStatusMessage = "Addon removed."
    }

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery = newQuery
        searchJob?.cancel()

        if (newQuery.isBlank()) {
            searchResults = emptyList()
            isSearching = false
            return
        }

        searchJob = viewModelScope.launch {
            delay(350) // debounce
            isSearching = true
            val results = mutableListOf<MetaItem>()
            val cinemetaUrl = installedAddonUrls.firstOrNull { it.contains("cinemeta") }
                ?: installedAddonUrls.firstOrNull()

            if (cinemetaUrl != null) {
                // Search both movie and series
                val movies = repository.fetchCatalog(cinemetaUrl, "movie", "top", searchQuery = newQuery)
                val series = repository.fetchCatalog(cinemetaUrl, "series", "top", searchQuery = newQuery)
                results.addAll(movies)
                results.addAll(series)
            }

            searchResults = results.distinctBy { it.id }
            isSearching = false
        }
    }

    fun selectGenre(genre: String?) {
        selectedGenre = genre
        if (genre == null) {
            searchResults = emptyList()
            return
        }

        viewModelScope.launch {
            isSearching = true
            val results = mutableListOf<MetaItem>()
            val cinemetaUrl = installedAddonUrls.firstOrNull { it.contains("cinemeta") }
                ?: installedAddonUrls.firstOrNull()

            if (cinemetaUrl != null) {
                val movies = repository.fetchCatalog(cinemetaUrl, "movie", "top", genre = genre)
                results.addAll(movies)
            }
            searchResults = results.distinctBy { it.id }
            isSearching = false
        }
    }

    fun openDetail(addonUrl: String, type: String, id: String) {
        navigateTo(Screen.Detail(addonUrl, type, id))
        loadDetail(addonUrl, type, id)
    }

    fun loadDetail(addonUrl: String, type: String, id: String) {
        viewModelScope.launch {
            isLoadingDetail = true
            currentDetailMeta = null
            currentStreams = emptyList()
            selectedEpisodeId = id

            val meta = repository.fetchMetaDetail(addonUrl, type, id)
            currentDetailMeta = meta
            isLoadingDetail = false

            // For series, default to the first episode if available
            val targetStreamId = if (type == "series" && !meta?.videos.isNullOrEmpty()) {
                val firstVideo = meta?.videos?.firstOrNull()
                selectedEpisodeId = firstVideo?.id ?: id
                firstVideo?.id ?: id
            } else {
                selectedEpisodeId = id
                id
            }

            loadStreams(type, targetStreamId)
        }
    }

    fun selectEpisode(type: String, video: VideoItem) {
        selectedEpisodeId = video.id
        loadStreams(type, video.id)
    }

    fun loadStreams(type: String, id: String) {
        viewModelScope.launch {
            isLoadingStreams = true
            val streams = repository.fetchAllStreams(type, id)
            currentStreams = streams
            isLoadingStreams = false
        }
    }

    fun playStream(stream: StreamItem, title: String) {
        val streamUrl = stream.url ?: return
        navigateTo(Screen.Player(streamUrl = streamUrl, title = title, subtitle = stream.displaySubtitle))
    }

    fun toggleWatchlist(meta: MetaItem) {
        val added = repository.toggleWatchlist(meta.id)
        val current = watchlistItems.toMutableList()
        if (added) {
            if (current.none { it.id == meta.id }) {
                current.add(meta)
            }
        } else {
            current.removeAll { it.id == meta.id }
        }
        watchlistItems = current
    }

    fun isWatchlist(id: String): Boolean {
        return repository.isWatchlist(id)
    }

    private fun updateWatchlistFromLoaded() {
        val ids = repository.getWatchlistIds()
        val allLoaded = catalogs.values.flatten().distinctBy { it.id }
        watchlistItems = allLoaded.filter { ids.contains(it.id) }
    }
}
