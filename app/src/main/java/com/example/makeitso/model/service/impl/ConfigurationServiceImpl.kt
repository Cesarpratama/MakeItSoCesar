package com.example.makeitso.model.service.impl

import com.example.makeitso.BuildConfig
import com.example.makeitso.R.xml as AppConfig
import com.example.makeitso.model.service.ConfigurationService
import com.example.makeitso.model.service.trace
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class ConfigurationServiceImpl @Inject constructor() : ConfigurationService {

  // Mengakses instance Remote Config dari Firebase
  private val remoteConfig
    get() = Firebase.remoteConfig

  // Konfigurasi awal Remote Config saat aplikasi berjalan dalam mode debug
  init {
    if (BuildConfig.DEBUG) {
      val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
      remoteConfig.setConfigSettingsAsync(configSettings)
    }

    // Mengatur nilai default untuk konfigurasi jarak jauh
    remoteConfig.setDefaultsAsync(AppConfig.remote_config_defaults)
  }

  // Mengambil dan mengaktifkan konfigurasi jarak jauh secara asinkron
  override suspend fun fetchConfiguration(): Boolean =
    trace(FETCH_CONFIG_TRACE) { remoteConfig.fetchAndActivate().await() }

  override val isShowTaskEditButtonConfig: Boolean
    get() = remoteConfig[SHOW_TASK_EDIT_BUTTON_KEY].asBoolean()

  companion object {
    // Kunci untuk konfigurasi jarak jauh menampilkan/menyembunyikan tombol edit tugas
    private const val SHOW_TASK_EDIT_BUTTON_KEY = "show_task_edit_button"

    // Jejak untuk penandaan saat mengambil konfigurasi jarak jauh
    private const val FETCH_CONFIG_TRACE = "fetchConfig"
  }
}
