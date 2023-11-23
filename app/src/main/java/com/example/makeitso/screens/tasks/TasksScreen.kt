package com.example.makeitso.screens.tasks

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.makeitso.R.drawable as AppIcon
import com.example.makeitso.R.string as AppText
import com.example.makeitso.common.composable.ActionToolbar
import com.example.makeitso.common.ext.smallSpacer
import com.example.makeitso.common.ext.toolbarActions
import com.example.makeitso.model.Task
import com.example.makeitso.theme.MakeItSoTheme

@Composable
@ExperimentalMaterialApi
fun TasksScreen(
  openScreen: (String) -> Unit,
  viewModel: TasksViewModel = hiltViewModel()
) {
  // Menerima daftar tugas dari ViewModel sebagai state
  val tasks = viewModel.tasks.collectAsStateWithLifecycle(emptyList())
  // Mendapatkan opsi tindakan dari ViewModel
  val options by viewModel.options

  // Memanggil komposisi untuk menampilkan konten tugas
  TasksScreenContent(
    tasks = tasks.value,
    options = options,
    onAddClick = viewModel::onAddClick,
    onSettingsClick = viewModel::onSettingsClick,
    onTaskCheckChange = viewModel::onTaskCheckChange,
    onTaskActionClick = viewModel::onTaskActionClick,
    openScreen = openScreen
  )

  // Memuat opsi tindakan saat komposisi dijalankan
  LaunchedEffect(viewModel) { viewModel.loadTaskOptions() }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@ExperimentalMaterialApi
fun TasksScreenContent(
  modifier: Modifier = Modifier,
  tasks: List<Task>,
  options: List<String>,
  onAddClick: ((String) -> Unit) -> Unit,
  onSettingsClick: ((String) -> Unit) -> Unit,
  onTaskCheckChange: (Task) -> Unit,
  onTaskActionClick: ((String) -> Unit, Task, String) -> Unit,
  openScreen: (String) -> Unit
) {

  // Membuat tata letak untuk layar tugas menggunakan Material Scaffold
  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        // Menambahkan FloatingActionButton untuk menambahkan tugas baru
        onClick = { onAddClick(openScreen) },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        modifier = modifier.padding(16.dp)
      ) {
        Icon(Icons.Filled.Add, "Add")
      }
    }
  ) {
    // Membuat tata letak kolom untuk menampilkan daftar tugas
    Column(modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()) {

      // Menampilkan toolbar aksi di bagian atas layar
      ActionToolbar(
        title = AppText.tasks,
        modifier = Modifier.toolbarActions(),
        endActionIcon = AppIcon.ic_settings,
        endAction = { onSettingsClick(openScreen) }
      )
      // Menambahkan ruang kosong di antara toolbar dan daftar tugas
      Spacer(modifier = Modifier.smallSpacer())

      // Membuat daftar tugas menggunakan LazyColumn
      LazyColumn {
        // Menampilkan item tugas menggunakan komposisi TaskItem
        items(tasks, key = { it.id }) { taskItem ->
          TaskItem(
            task = taskItem,
            options = options,
            onCheckChange = { onTaskCheckChange(taskItem) },
            onActionClick = { action -> onTaskActionClick(openScreen, taskItem, action) }
          )
        }
      }
    }
  }
}

// Pratinjau tampilan untuk TasksScreenContent
@Preview(showBackground = true)
@ExperimentalMaterialApi
@Composable
fun TasksScreenPreview() {
  // Membuat contoh tugas dan opsi tindakan
  val task = Task(
    title = "Task title",
    flag = true,
    completed = true
  )

  val options = TaskActionOption.getOptions(hasEditOption = true)

  // Menampilkan tampilan pratinjau menggunakan tema MakeItSo
  MakeItSoTheme {
    TasksScreenContent(
      tasks = listOf(task),
      options = options,
      onAddClick = { },
      onSettingsClick = { },
      onTaskCheckChange = { },
      onTaskActionClick = { _, _, _ -> },
      openScreen = { }
    )
  }
}
