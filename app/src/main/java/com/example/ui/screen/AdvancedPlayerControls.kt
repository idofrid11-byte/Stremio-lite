package com.example.ui.screen

import android.app.Activity
import android.graphics.Color as AndroidColor
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import androidx.media3.ui.SubtitleView
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSecondary
import com.example.ui.theme.CinemaSurfaceVariant
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun AdvancedPlayerControls(
    exoPlayer: ExoPlayer,
    trackSelector: DefaultTrackSelector,
    playerView: PlayerView?,
    currentTracks: Tracks?
) {
    var showAdvancedDialog by remember { mutableStateOf(false) }

    // Subtitle Style State
    var subtitleTextSize by remember { mutableFloatStateOf(1.0f) }
    var subtitleColor by remember { mutableIntStateOf(AndroidColor.WHITE) }
    var subtitleBgColor by remember { mutableIntStateOf(AndroidColor.TRANSPARENT) }
    var subtitleDelayMs by remember { mutableLongStateOf(0L) }

    // Audio State
    var audioDelayMs by remember { mutableLongStateOf(0L) }
    var volumeBoostEnabled by remember { mutableStateOf(false) }

    // Playback State
    var resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    var sleepTimerMinutes by remember { mutableIntStateOf(0) }

    // Apply Subtitle styling to PlayerView
    LaunchedEffect(subtitleTextSize, subtitleColor, subtitleBgColor, playerView) {
        playerView?.subtitleView?.let { subView ->
            val captionStyle = CaptionStyleCompat(
                subtitleColor,
                subtitleBgColor,
                AndroidColor.TRANSPARENT,
                CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                AndroidColor.BLACK,
                null
            )
            subView.setStyle(captionStyle)
            subView.setFractionalTextSize(SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * subtitleTextSize)
        }
    }

    // Sleep timer logic
    LaunchedEffect(sleepTimerMinutes) {
        if (sleepTimerMinutes > 0) {
            delay(sleepTimerMinutes * 60 * 1000L)
            exoPlayer.pause()
        }
    }

    IconButton(
        onClick = { showAdvancedDialog = true },
        modifier = Modifier
            .padding(4.dp)
            .testTag("advanced_player_settings_button")
    ) {
        Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Advanced Settings",
            tint = Color.White
        )
    }

    if (showAdvancedDialog) {
        AdvancedSettingsDialog(
            exoPlayer = exoPlayer,
            trackSelector = trackSelector,
            currentTracks = currentTracks,
            subtitleTextSize = subtitleTextSize,
            onSubtitleTextSizeChange = { subtitleTextSize = it },
            subtitleColor = subtitleColor,
            onSubtitleColorChange = { subtitleColor = it },
            subtitleBgColor = subtitleBgColor,
            onSubtitleBgColorChange = { subtitleBgColor = it },
            subtitleDelayMs = subtitleDelayMs,
            onSubtitleDelayChange = { subtitleDelayMs = it },
            audioDelayMs = audioDelayMs,
            onAudioDelayChange = { audioDelayMs = it },
            volumeBoostEnabled = volumeBoostEnabled,
            onVolumeBoostChange = {
                volumeBoostEnabled = it
                exoPlayer.volume = if (it) 2.0f else 1.0f
            },
            resizeMode = resizeMode,
            onResizeModeChange = {
                resizeMode = it
                playerView?.resizeMode = it
            },
            sleepTimerMinutes = sleepTimerMinutes,
            onSleepTimerChange = { sleepTimerMinutes = it },
            onDismiss = { showAdvancedDialog = false }
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
fun AdvancedSettingsDialog(
    exoPlayer: ExoPlayer,
    trackSelector: DefaultTrackSelector,
    currentTracks: Tracks?,
    subtitleTextSize: Float,
    onSubtitleTextSizeChange: (Float) -> Unit,
    subtitleColor: Int,
    onSubtitleColorChange: (Int) -> Unit,
    subtitleBgColor: Int,
    onSubtitleBgColorChange: (Int) -> Unit,
    subtitleDelayMs: Long,
    onSubtitleDelayChange: (Long) -> Unit,
    audioDelayMs: Long,
    onAudioDelayChange: (Long) -> Unit,
    volumeBoostEnabled: Boolean,
    onVolumeBoostChange: (Boolean) -> Unit,
    resizeMode: Int,
    onResizeModeChange: (Int) -> Unit,
    sleepTimerMinutes: Int,
    onSleepTimerChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Subtitles, 1: Audio, 2: Playback

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Advanced Player Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CinemaSurfaceVariant,
                    contentColor = CinemaPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Subtitles") },
                        modifier = Modifier.testTag("tab_subtitles")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Audio") },
                        modifier = Modifier.testTag("tab_audio")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Playback") },
                        modifier = Modifier.testTag("tab_playback")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (selectedTab) {
                    0 -> SubtitleAdvancedSettings(
                        textSize = subtitleTextSize,
                        onTextSizeChange = onSubtitleTextSizeChange,
                        textColor = subtitleColor,
                        onTextColorChange = onSubtitleColorChange,
                        bgColor = subtitleBgColor,
                        onBgColorChange = onSubtitleBgColorChange,
                        delayMs = subtitleDelayMs,
                        onDelayChange = onSubtitleDelayChange
                    )
                    1 -> AudioAdvancedSettings(
                        audioDelayMs = audioDelayMs,
                        onAudioDelayChange = onAudioDelayChange,
                        volumeBoost = volumeBoostEnabled,
                        onVolumeBoostChange = onVolumeBoostChange
                    )
                    2 -> PlaybackAdvancedSettings(
                        resizeMode = resizeMode,
                        onResizeModeChange = onResizeModeChange,
                        sleepTimerMinutes = sleepTimerMinutes,
                        onSleepTimerChange = onSleepTimerChange,
                        exoPlayer = exoPlayer
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimary),
                modifier = Modifier.testTag("advanced_settings_done_button")
            ) {
                Text("Done")
            }
        },
        containerColor = CinemaSurfaceVariant,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun SubtitleAdvancedSettings(
    textSize: Float,
    onTextSizeChange: (Float) -> Unit,
    textColor: Int,
    onTextColorChange: (Int) -> Unit,
    bgColor: Int,
    onBgColorChange: (Int) -> Unit,
    delayMs: Long,
    onDelayChange: (Long) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                "Subtitle Scale: ${String.format("%.1f", textSize)}x",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Slider(
                value = textSize,
                onValueChange = onTextSizeChange,
                valueRange = 0.5f..2.0f,
                colors = SliderDefaults.colors(
                    thumbColor = CinemaPrimary,
                    activeTrackColor = CinemaPrimary
                ),
                modifier = Modifier.testTag("subtitle_scale_slider")
            )
        }

        item {
            Text(
                "Text Color",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val isWhite = textColor == AndroidColor.WHITE
                val isYellow = textColor == AndroidColor.YELLOW
                val isCyan = textColor == AndroidColor.CYAN

                Button(
                    onClick = { onTextColorChange(AndroidColor.WHITE) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWhite) CinemaPrimary else Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("White")
                }
                Button(
                    onClick = { onTextColorChange(AndroidColor.YELLOW) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isYellow) CinemaPrimary else Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Yellow")
                }
                Button(
                    onClick = { onTextColorChange(AndroidColor.CYAN) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCyan) CinemaPrimary else Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Cyan")
                }
            }
        }

        item {
            Text(
                "Background Box",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val isNone = bgColor == AndroidColor.TRANSPARENT
                val isBlack = bgColor == AndroidColor.BLACK

                Button(
                    onClick = { onBgColorChange(AndroidColor.TRANSPARENT) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNone) CinemaPrimary else Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("None")
                }
                Button(
                    onClick = { onBgColorChange(AndroidColor.BLACK) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBlack) CinemaPrimary else Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Black")
                }
            }
        }

        item {
            Text(
                "Subtitle Sync Delay: ${delayMs}ms",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onDelayChange(delayMs - 100) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("-100ms")
                }
                Button(
                    onClick = { onDelayChange(0L) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = { onDelayChange(delayMs + 100) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("+100ms")
                }
            }
        }
    }
}

@Composable
fun AudioAdvancedSettings(
    audioDelayMs: Long,
    onAudioDelayChange: (Long) -> Unit,
    volumeBoost: Boolean,
    onVolumeBoostChange: (Boolean) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Volume Boost (200%)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        "Amplify soft dialogs and audio tracks",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = volumeBoost,
                    onCheckedChange = onVolumeBoostChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CinemaPrimary
                    ),
                    modifier = Modifier.testTag("volume_boost_switch")
                )
            }
        }

        item {
            Text(
                "Audio Lip-Sync Delay: ${audioDelayMs}ms",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onAudioDelayChange(audioDelayMs - 50) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("-50ms")
                }
                Button(
                    onClick = { onAudioDelayChange(0L) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = { onAudioDelayChange(audioDelayMs + 50) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("+50ms")
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun PlaybackAdvancedSettings(
    resizeMode: Int,
    onResizeModeChange: (Int) -> Unit,
    sleepTimerMinutes: Int,
    onSleepTimerChange: (Int) -> Unit,
    exoPlayer: ExoPlayer
) {
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                "Video Aspect Ratio / Resize",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onResizeModeChange(AspectRatioFrameLayout.RESIZE_MODE_FIT) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (resizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT) CinemaPrimary else Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Fit")
                }
                Button(
                    onClick = { onResizeModeChange(AspectRatioFrameLayout.RESIZE_MODE_FILL) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (resizeMode == AspectRatioFrameLayout.RESIZE_MODE_FILL) CinemaPrimary else Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Fill")
                }
                Button(
                    onClick = { onResizeModeChange(AspectRatioFrameLayout.RESIZE_MODE_ZOOM) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (resizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) CinemaPrimary else Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Zoom")
                }
            }
        }

        item {
            Text(
                "Sleep Timer",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0 to "Off", 15 to "15m", 30 to "30m", 60 to "60m").forEach { (min, label) ->
                    val isSelected = sleepTimerMinutes == min
                    Button(
                        onClick = { onSleepTimerChange(min) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) CinemaPrimary else Color(0x33FFFFFF)
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(label)
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    if (context is Activity && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        context.enterPictureInPictureMode(
                            android.app.PictureInPictureParams.Builder().build()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("enter_pip_button")
            ) {
                Text("Enter Picture-in-Picture (PiP)")
            }
        }
    }
}
