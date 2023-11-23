package com.example.makeitso.model.service.impl

import com.example.makeitso.model.Task
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.model.service.trace
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
  StorageService {

  // Fungsi untuk mendapatkan daftar tugas berdasrkan pengguna saat ini
  @OptIn(ExperimentalCoroutinesApi::class)
  override val tasks: Flow<List<Task>>
    get() =
      auth.currentUser.flatMapLatest { user ->
        firestore.collection(TASK_COLLECTION).whereEqualTo(USER_ID_FIELD, user.id).dataObjects()
      }

  // Fungsi untuk mendapatkan satu tugas berdasarkan ID
  override suspend fun getTask(taskId: String): Task? =
    firestore.collection(TASK_COLLECTION).document(taskId).get().await().toObject()

  // Fungsi untuk menyimpan menyimpan tugas baru ke firebase
  override suspend fun save(task: Task): String =
    trace(SAVE_TASK_TRACE) {
     // Menambahkan ID pengguna ke tugas sebelum menyimpan
      val taskWithUserId = task.copy(userId = auth.currentUserId)
     // Menyimpan tugas ke firestore dan mengembalikan ID Dokumen baru
      firestore.collection(TASK_COLLECTION).add(taskWithUserId).await().id
    }
   // Fungsi untuk memperbarui tugas yang ada pada firebase firestore
  override suspend fun update(task: Task): Unit =
    // Mengupdate berdsasarkan ID
    trace(UPDATE_TASK_TRACE) {
      firestore.collection(TASK_COLLECTION).document(task.id).set(task).await()
    }

  override suspend fun delete(taskId: String) {
    firestore.collection(TASK_COLLECTION).document(taskId).delete().await()
  }
  //  Companion object untuk menyimpan konstanta-konstanta yang digunakan dalam kelas ini
  companion object {
    private const val USER_ID_FIELD = "userId"
    private const val TASK_COLLECTION = "tasks"
    private const val SAVE_TASK_TRACE = "saveTask"
    private const val UPDATE_TASK_TRACE = "updateTask"
  }
}
