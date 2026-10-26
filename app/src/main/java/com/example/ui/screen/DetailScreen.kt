package com.example.ui.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.outlined.PlayCircle
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
import com.example.model.StreamItem
import com.example.model.VideoItem
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSecondary
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: MainViewModel,
    addonUrl: String,
    type: String,
    id: String,
    onBack: () -> Unit,
    onPlayStream: (StreamItem, String) -> Unit,
    onOpenAddonManager: () -> Unit
) {
    BackHandler { onBack() }

    val meta = viewModel.currentDetailMeta
    val isLoadingDetail = viewModel.isLoadingDetail
    val streams = viewModel.currentStreams
    val isLoadingStreams = viewModel.isLoadingStreams
    val selectedEpisodeId = viewModel.selectedEpisodeId

    // Group episodes by season if series
    var selectedSeason by remember(meta) {
        val firstSeason = meta?.videos?.firstOrNull()?.season ?: 1
        mutableStateOf(firstSeason)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        if (isLoadingDetail || meta == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = CinemaPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading title details...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val isBookmarked = viewModel.isWatchlist(meta.id)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding()),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Header Backdrop
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        AsyncImage(
                            model = meta.background ?: meta.poster,
                            contentDescription = meta.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Vignette Gradients
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x990A0912),
                                            Color(0x440A0912),
                                            Color(0xFF0A0912)
                                        )
                                    )
                                )
                        )

                        // Top Nav Bar actions
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0x99000000),
                                modifier = Modifier.size(42.dp)
                            ) {
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier.testTag("detail_back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color(0x99000000),
                                modifier = Modifier.size(42.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.toggleWatchlist(meta) },
                                    modifier = Modifier.testTag("detail_watchlist_button")
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (isBookmarked) CinemaGold else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Poster & Metadata Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-40).dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Poster Card
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            modifier = Modifier
                                .width(115.dp)
                                .height(170.dp)
                                .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = meta.poster ?: meta.background,
                                contentDescription = meta.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Text details
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 45.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = meta.name,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (meta.displayYear.isNotBlank()) {
                                    Surface(
                                        color = CinemaSurfaceVariant,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = meta.displayYear,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (!meta.imdbRating.isNullOrBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = CinemaGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${meta.imdbRating}/10",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = CinemaGold
                                            )
                                        )
                                    }
                                }

                                if (!meta.runtime.isNullOrBlank()) {
                                    Text(
                                        text = meta.runtime,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }

                            if (meta.displayGenres.isNotBlank()) {
                                Text(
                                    text = meta.displayGenres,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CinemaSecondary
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Synopsis Description
                if (!meta.description.isNullOrBlank()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-24).dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Synopsis",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = meta.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                }

                // If Series: Episodes selector
                if (meta.type == "series" && !meta.videos.isNullOrEmpty()) {
                    val seasons = meta.videos.mapNotNull { it.season }.distinct().sorted()

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-16).dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Episodes",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            // Season selector chips if more than 1 season
                            if (seasons.size > 1) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    items(seasons) { seasonNum ->
                                        FilterChip(
                                            selected = selectedSeason == seasonNum,
                                            onClick = { selectedSeason = seasonNum },
                                            label = { Text("Season $seasonNum") },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CinemaPrimary,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }

                            // Episode Cards
                            val currentEpisodes = meta.videos.filter { it.season == null || it.season == selectedSeason }
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                currentEpisodes.forEach { video ->
                                    val isSelected = video.id == selectedEpisodeId
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.selectEpisode(meta.type, video)
                                            }
                                            .testTag("episode_card_${video.id}"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) CinemaPrimary.copy(alpha = 0.2f) else CinemaSurfaceVariant
                                        ),
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, CinemaPrimary) else null
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isSelected) CinemaPrimary else Color(0x33FFFFFF),
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "${video.episode ?: 1}",
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = video.displayTitle,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                    )
                                                )
                                                if (!video.released.isNullOrBlank()) {
                                                    Text(
                                                        text = video.released,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    )
                                                }
                                            }

                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayCircle,
                                                    contentDescription = "Selected",
                                                    tint = CinemaPrimary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Available Streams Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-8).dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Available Streams",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (!isLoadingStreams) {
                                Surface(
                                    color = CinemaPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${streams.size} sources",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CinemaPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isLoadingStreams) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = CinemaPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        "Searching installed addons for streams...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else if (streams.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No streams found from current addons",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Install community streaming addons or check addon configuration.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedButton(
                                        onClick = onOpenAddonManager,
                                        modifier = Modifier.testTag("open_addons_from_detail")
                                    ) {
                                        Text("Open Addon Manager")
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                streams.forEachIndexed { index, stream ->
                                    StreamCard(
                                        stream = stream,
                                        index = index,
                                        onClick = {
                                            onPlayStream(stream, meta.name)
                                        }
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

@Composable
fun StreamCard(
    stream: StreamItem,
    index: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
            .testTag("stream_item_$index"),
        colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = CinemaPrimary,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stream.displayTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Surface(
                        color = Color(0x3306B6D4),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (stream.isPlayableDirectly) "DIRECT" else "STREAM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CinemaSecondary
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stream.displaySubtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
