package com.example.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.repository.SettingsRepository
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedSettingsScreen(
    authViewModel: AuthViewModel,
    settingsRepository: SettingsRepository,
    onNavigateToAddons: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var subSize by remember { mutableFloatStateOf(settingsRepository.defaultSubtitleSize) }
    var volumeBoost by remember { mutableStateOf(settingsRepository.defaultVolumeBoost) }
    var playbackSpeed by remember { mutableFloatStateOf(settingsRepository.defaultPlaybackSpeed) }
    var autoPlay by remember { mutableStateOf(settingsRepository.autoPlayNextEpisode) }
    var hwDecoding by remember { mutableStateOf(settingsRepository.hardwareDecoding) }
    var wifiOnly by remember { mutableStateOf(settingsRepository.wifiOnlyStreaming) }
    var darkMode by remember { mutableStateOf(settingsRepository.darkModeEnabled) }
    var cacheMessage by remember { mutableStateOf<String?>(null) }

    val userEmail = authViewModel.userEmail.value ?: authViewModel.emailInput.value.ifBlank { "Stremio User" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account
            item {
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CinemaPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Logged in as", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(userEmail, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        IconButton(
                            onClick = { authViewModel.logout() },
                            modifier = Modifier.testTag("settings_logout_button")
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            // Addons
            item {
                Text(
                    text = "Addons",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CinemaPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToAddons)
                        .testTag("settings_addon_manager_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Extension, contentDescription = "Addons", tint = CinemaPrimary)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Addon Manager", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Install or remove community streaming addons", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Playback Defaults
            item {
                Text(
                    text = "Playback & Performance",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CinemaPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column {
                            Text("Default Playback Speed: ${String.format("%.2f", playbackSpeed)}x", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            Slider(
                                value = playbackSpeed,
                                onValueChange = {
                                    playbackSpeed = it
                                    settingsRepository.defaultPlaybackSpeed = it
                                },
                                valueRange = 0.5f..2.0f,
                                steps = 5,
                                colors = SliderDefaults.colors(thumbColor = CinemaPrimary, activeTrackColor = CinemaPrimary),
                                modifier = Modifier.testTag("playback_speed_slider")
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Auto-play Next Episode", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = autoPlay,
                                onCheckedChange = {
                                    autoPlay = it
                                    settingsRepository.autoPlayNextEpisode = it
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CinemaPrimary)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Hardware Decoding (MediaCodec)", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = hwDecoding,
                                onCheckedChange = {
                                    hwDecoding = it
                                    settingsRepository.hardwareDecoding = it
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CinemaPrimary)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Volume Boost (200%)", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = volumeBoost,
                                onCheckedChange = {
                                    volumeBoost = it
                                    settingsRepository.defaultVolumeBoost = it
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CinemaPrimary)
                            )
                        }
                    }
                }
            }

            // Subtitles
            item {
                Text(
                    text = "Subtitles",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CinemaPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Default Subtitle Scale: ${String.format("%.1f", subSize)}x", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Slider(
                            value = subSize,
                            onValueChange = {
                                subSize = it
                                settingsRepository.defaultSubtitleSize = it
                            },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(thumbColor = CinemaPrimary, activeTrackColor = CinemaPrimary)
                        )
                    }
                }
            }

            // Network & Appearance
            item {
                Text(
                    text = "Network & Appearance",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CinemaPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Wi-Fi Only Streaming Warning", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = wifiOnly,
                                onCheckedChange = {
                                    wifiOnly = it
                                    settingsRepository.wifiOnlyStreaming = it
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CinemaPrimary)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Dark Theme", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = darkMode,
                                onCheckedChange = {
                                    darkMode = it
                                    settingsRepository.darkModeEnabled = it
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CinemaPrimary)
                            )
                        }

                        Button(
                            onClick = {
                                val freed = settingsRepository.clearCache()
                                cacheMessage = if (freed > 0) "Cleared ${(freed / 1024)} KB from temporary cache." else "Cache is already clean!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("clear_cache_button")
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Clear Image & Metadata Cache")
                        }

                        if (!cacheMessage.isNullOrBlank()) {
                            Surface(
                                color = CinemaPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = cacheMessage!!,
                                    style = MaterialTheme.typography.bodySmall.copy(color = CinemaPrimary),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
