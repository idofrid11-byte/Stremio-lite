package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.repository.SettingsRepository
import com.example.ui.screen.*
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.StremioTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StremioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CinemaBlack
                ) {
                    val authViewModel: AuthViewModel = viewModel()
                    val mainViewModel: MainViewModel = viewModel()
                    val settingsRepository = remember { SettingsRepository(applicationContext) }

                    val isLoggedIn = authViewModel.isLoggedIn.value

                    if (!isLoggedIn) {
                        AuthScreen(viewModel = authViewModel)
                    } else {
                        when (val screen = mainViewModel.currentScreen) {
                            is Screen.Home -> {
                                HomeScreen(
                                    viewModel = mainViewModel,
                                    userEmail = authViewModel.userEmail.value,
                                    onItemClick = { url, type, id ->
                                        mainViewModel.openDetail(url, type, id)
                                    },
                                    onAddonManagerClick = {
                                        mainViewModel.navigateTo(Screen.AddonManager)
                                    },
                                    onSearchClick = {
                                        mainViewModel.navigateTo(Screen.Search)
                                    },
                                    onWatchlistClick = {
                                        mainViewModel.navigateTo(Screen.Watchlist)
                                    },
                                    onLogoutClick = {
                                        authViewModel.logout()
                                    },
                                    onSettingsClick = {
                                        mainViewModel.navigateTo(Screen.Settings)
                                    },
                                    onResumeContinueWatching = { item ->
                                        mainViewModel.resumeContinueWatching(item)
                                    }
                                )
                            }
                            is Screen.Detail -> {
                                DetailScreen(
                                    viewModel = mainViewModel,
                                    addonUrl = screen.addonUrl,
                                    type = screen.type,
                                    id = screen.id,
                                    onBack = { mainViewModel.navigateBack() },
                                    onPlayStream = { stream, title ->
                                        mainViewModel.playStream(stream, title)
                                    },
                                    onOpenAddonManager = {
                                        mainViewModel.navigateTo(Screen.AddonManager)
                                    }
                                )
                            }
                            is Screen.Player -> {
                                PlayerScreen(
                                    streamUrl = screen.streamUrl,
                                    title = screen.title,
                                    subtitle = screen.subtitle,
                                    startPositionMs = screen.startPositionMs,
                                    onProgressUpdate = { pos, dur ->
                                        mainViewModel.savePlaybackProgress(screen.continueWatchingItem, pos, dur)
                                    },
                                    onBack = {
                                        mainViewModel.refreshContinueWatching()
                                        mainViewModel.navigateBack()
                                    }
                                )
                            }
                            is Screen.Search -> {
                                SearchScreen(
                                    viewModel = mainViewModel,
                                    onBack = { mainViewModel.navigateBack() },
                                    onItemClick = { url, type, id ->
                                        mainViewModel.openDetail(url, type, id)
                                    }
                                )
                            }
                            is Screen.Watchlist -> {
                                WatchlistScreen(
                                    viewModel = mainViewModel,
                                    onBack = { mainViewModel.navigateBack() },
                                    onItemClick = { url, type, id ->
                                        mainViewModel.openDetail(url, type, id)
                                    },
                                    onBrowseClick = {
                                        mainViewModel.navigateTo(Screen.Home)
                                    }
                                )
                            }
                            is Screen.AddonManager -> {
                                AddonManagerScreen(
                                    viewModel = mainViewModel,
                                    onBack = { mainViewModel.navigateBack() }
                                )
                            }
                            is Screen.Settings -> {
                                ExpandedSettingsScreen(
                                    authViewModel = authViewModel,
                                    settingsRepository = settingsRepository,
                                    onNavigateToAddons = {
                                        mainViewModel.navigateTo(Screen.AddonManager)
                                    },
                                    onBack = {
                                        mainViewModel.navigateBack()
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
