package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.repository.AddonRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("StremioLite", appName)
  }

  @Test
  fun `verify addon url normalization`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = AddonRepository(context)

    val normalized1 = repository.normalizeUrl("stremio://v3-cinemeta.strem.io/manifest.json")
    assertEquals("https://v3-cinemeta.strem.io/manifest.json", normalized1)

    val normalized2 = repository.normalizeUrl("https://watchhub.strem.io")
    assertEquals("https://watchhub.strem.io/manifest.json", normalized2)

    val defaultAddons = repository.getInstalledAddonUrls()
    assertTrue(defaultAddons.isNotEmpty())
  }

  @Test
  fun `verify auth repository register and login`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val authRepo = com.example.repository.AuthRepository(context)

    val regResult = authRepo.register("user@test.com", "password123")
    assertTrue(regResult.isSuccess)
    assertTrue(authRepo.isLoggedIn())
    assertEquals("user@test.com", authRepo.getLoggedUserEmail())

    authRepo.logout()
    org.junit.Assert.assertFalse(authRepo.isLoggedIn())

    val loginResult = authRepo.login("user@test.com", "password123")
    assertTrue(loginResult.isSuccess)
    assertTrue(authRepo.isLoggedIn())
  }

  @Test
  fun `verify settings repository default and updated values`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val settingsRepo = com.example.repository.SettingsRepository(context)

    assertEquals(1.0f, settingsRepo.defaultPlaybackSpeed, 0.01f)
    assertTrue(settingsRepo.autoPlayNextEpisode)
    assertTrue(settingsRepo.hardwareDecoding)

    settingsRepo.defaultPlaybackSpeed = 1.25f
    assertEquals(1.25f, settingsRepo.defaultPlaybackSpeed, 0.01f)

    settingsRepo.autoPlayNextEpisode = false
    org.junit.Assert.assertFalse(settingsRepo.autoPlayNextEpisode)
  }

  @Test
  fun `verify continue watching repository save progress and remove`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val continueRepo = com.example.repository.ContinueWatchingRepository(context)

    val item = com.example.model.ContinueWatchingItem(
        id = "movie123",
        metaId = "movie123",
        addonUrl = "https://v3-cinemeta.strem.io/manifest.json",
        title = "Inception",
        streamUrl = "https://example.com/stream.mp4",
        positionMs = 120000L,
        durationMs = 600000L
    )

    continueRepo.saveProgress(item)
    val list = continueRepo.getContinueWatchingList()
    assertTrue(list.isNotEmpty())
    assertEquals("Inception", list.first().title)
    assertEquals(0.2f, list.first().progressFraction, 0.01f)

    continueRepo.removeItem("movie123")
    val emptyList = continueRepo.getContinueWatchingList()
    assertTrue(emptyList.isEmpty())
  }
}
