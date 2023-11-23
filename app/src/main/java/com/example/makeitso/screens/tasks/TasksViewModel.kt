package com.example.makeitso.screens.tasks

import androidx.compose.runtime.mutableStateOf
import com.example.makeitso.EDIT_TASK_SCREEN
import com.example.makeitso.SETTINGS_SCREEN
import com.example.makeitso.TASK_ID
import com.example.makeitso.model.Task
import com.example.makeitso.model.service.ConfigurationService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
  private val configurationService: ConfigurationService
) : MakeItSoViewModel(logService) {
  // State untuk menyimpan opsi tindakan pada tugas
  val options = mutableStateOf<List<String>>(listOf())

  // Flow yang berisi daftar tugas dari storageService
  val tasks = storageService.tasks

  // Fungsi untuk memuat opsi tindakan tugas
  fun loadTaskOptions() {
    // Mendapatkan status dari konfigurasi apakah tombol edit tugas ditampilkan atau tidak
    val hasEditOption = configurationService.isShowTaskEditButtonConfig
    // Mengisi state options dengan opsi-opsi tindakan tugas
    options.value = TaskActionOption.getOptions(hasEditOption)
  }

  // Fungsi yang dipanggil saat checkbox tugas diubah
  fun onTaskCheckChange(task: Task) {
    // Melakukan update pada tugas (mengubah status completed)
    launchCatching { storageService.update(task.copy(completed = !task.completed)) }
  }

  // Fungsi yang dipanggil saat tombol tambah diklik
  fun onAddClick(openScreen: (String) -> Unit) = openScreen(EDIT_TASK_SCREEN)

  // Fungsi yang dipanggil saat tombol pengaturan diklik
  fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

  // Fungsi yang dipanggil saat opsi tindakan tugas di klik
  fun onTaskActionClick(openScreen: (String) -> Unit, task: Task, action: String) {
    // Memproses tindakan berdasarkan opsi yang dipilih
    when (TaskActionOption.getByTitle(action)) {
      TaskActionOption.EditTask -> openScreen("$EDIT_TASK_SCREEN?$TASK_ID={${task.id}}")
      TaskActionOption.ToggleFlag -> onFlagTaskClick(task)
      TaskActionOption.DeleteTask -> onDeleteTaskClick(task)
    }
  }
  // Fungsi yang dipanggil saat opsi "Toggle Flag" di klik
  private fun onFlagTaskClick(task: Task) {
    // Melakukan update pada tugas (mengubah status flag)
    launchCatching { storageService.update(task.copy(flag = !task.flag)) }
  }
  // Fungsi yang dipanggil saat opsi "Delete Task" di klik
  private fun onDeleteTaskClick(task: Task) {
    // Menghapus tugas dari storageService berdasarkan ID
    launchCatching { storageService.delete(task.id) }
  }
}
