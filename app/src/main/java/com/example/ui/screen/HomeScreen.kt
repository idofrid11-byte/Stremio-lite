package com.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.MetaItem
import com.example.ui.components.MediaCard
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    userEmail: String? = null,
    onItemClick: (addonUrl: String, type: String, id: String) -> Unit,
    onAddonManagerClick: () -> Unit,
    onSearchClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onLogoutClick: (() -> Unit)? = null
) {
    val addonUrls = viewModel.installedAddonUrls
    val manifests = viewModel.manifests
    val catalogs = viewModel.catalogs
    val isLoading = viewModel.isLoading
    val filterType = viewModel.selectedMediaTypeFilter
    val featuredItem = viewModel.featuredItem

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CinemaPrimary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "StremioLite",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier.testTag("nav_search_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(
                        onClick = onWatchlistClick,
                        modifier = Modifier.testTag("nav_watchlist_button")
                    ) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Watchlist")
                    }
                    IconButton(
                        onClick = onAddonManagerClick,
                        modifier = Modifier.testTag("nav_addons_button")
                    ) {
                        Icon(Icons.Outlined.Extension, contentDescription = "Addons")
                    }
                    if (onLogoutClick != null) {
                        var showAccountDialog by remember { mutableStateOf(false) }

                        IconButton(
                            onClick = { showAccountDialog = true },
                            modifier = Modifier.testTag("nav_account_button")
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Account")
                        }

                        if (showAccountDialog) {
                            AlertDialog(
                                onDismissRequest = { showAccountDialog = false },
                                title = { Text("Account Profile") },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Signed in as:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(userEmail ?: "Stremio User", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Your addon configurations and watchlist are synced.", style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            showAccountDialog = false
                                            onLogoutClick()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Sign Out")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showAccountDialog = false }) {
                                        Text("Close")
                                    }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading && catalogs.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = CinemaPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Connecting to Stremio addons...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (catalogs.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MovieFilter,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No media loaded yet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Check your internet connection or install more addons in the Addon Manager.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.loadAddonsAndCatalogs() },
                        modifier = Modifier.testTag("retry_catalogs_button")
                    ) {
                        Text("Retry")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Filter Chips Row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("all" to "All", "movie" to "Movies", "series" to "Series").forEach { (typeKey, label) ->
                                val selected = filterType == typeKey
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.selectedMediaTypeFilter = typeKey },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CinemaPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Featured Hero Banner
                    if (featuredItem != null && (filterType == "all" || filterType == featuredItem.type)) {
                        item {
                            FeaturedHeroBanner(
                                meta = featuredItem,
                                onWatchClick = {
                                    val sourceUrl = addonUrls.firstOrNull() ?: ""
                                    onItemClick(sourceUrl, featuredItem.type, featuredItem.id)
                                }
                            )
                        }
                    }

                    // Addon Catalogs
                    for (url in addonUrls) {
                        val manifest = manifests[url] ?: continue
                        for (cat in manifest.catalogs) {
                            // Filter by media type if selected
                            if (filterType != "all" && cat.type != filterType) {
                                continue
                            }

                            val items = catalogs["${url}_${cat.type}_${cat.id}"] ?: emptyList()
                            if (items.isNotEmpty()) {
                                item(key = "${url}_${cat.type}_${cat.id}") {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = cat.name ?: "${manifest.name} ${cat.type.replaceFirstChar { it.uppercase() }}",
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                                Text(
                                                    text = "${manifest.name} • ${items.size} titles",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                )
                                            }
                                        }

                                        LazyRow(
                                            contentPadding = PaddingValues(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.padding(top = 8.dp)
                                        ) {
                                            items(items, key = { it.id }) { meta ->
                                                MediaCard(
                                                    meta = meta,
                                                    onClick = { onItemClick(url, meta.type, meta.id) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedHeroBanner(
    meta: MetaItem,
    onWatchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onWatchClick)
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
            .testTag("featured_hero_banner")
    ) {
        AsyncImage(
            model = meta.background ?: meta.poster,
            contentDescription = meta.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient scrim for readable text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x880A0912),
                            Color(0xF50A0912)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Surface(
                color = CinemaPrimary,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "FEATURED ${meta.type.uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = meta.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (meta.displayGenres.isNotBlank()) {
                Text(
                    text = meta.displayGenres,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onWatchClick,
                colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Details & Streams", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
