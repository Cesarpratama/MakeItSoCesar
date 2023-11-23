package com.example.makeitso.model.service.impl

import com.example.makeitso.model.User
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.trace
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth) : AccountService {

  // Mendapatkan ID pengguna saat ini atau mengembalikan string kosong jika tidak ada pengguna yang masuk
  override val currentUserId: String
    get() = auth.currentUser?.uid.orEmpty()

  // Memeriksa apakah ada pengguna yang masuk atau tidak
  override val hasUser: Boolean
    get() = auth.currentUser != null

  // Mengembalikan aliran (Flow) yang memantau perubahan status otentikasi pengguna
  override val currentUser: Flow<User>
    get() = callbackFlow {
      val listener =
        // Mengirimkan data pengguna saat ada perubahan status otentikasi
        FirebaseAuth.AuthStateListener { auth ->
          this.trySend(auth.currentUser?.let { User(it.uid, it.isAnonymous) } ?: User())
        }
      auth.addAuthStateListener(listener)
      awaitClose { auth.removeAuthStateListener(listener) }
    }
  // Melakukan otentikasi pengguna dengan email dan kata sandi
  override suspend fun authenticate(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password).await()
  }

  // Mengirim email pemulihan kata sandi
  override suspend fun sendRecoveryEmail(email: String) {
    auth.sendPasswordResetEmail(email).await()
  }

  // Membuat akun anonim dan masuk menggunakan akun tersebut
  override suspend fun createAnonymousAccount() {
    auth.signInAnonymously().await()
  }

  // Mengaitkan akun anonim dengan akun email dan kata sandi
  override suspend fun linkAccount(email: String, password: String) {
    val credential = EmailAuthProvider.getCredential(email, password)
    auth.currentUser!!.linkWithCredential(credential).await()
  }

  // Menghapus akun pengguna saat ini
  override suspend fun deleteAccount() {
    auth.currentUser!!.delete().await()
  }

  // Keluar dari akun pengguna saat ini dan masuk kembali secara anonim
  override suspend fun signOut() {
    if (auth.currentUser!!.isAnonymous) {
      auth.currentUser!!.delete()
    }
    auth.signOut()

    // Masuk kembali secara anonim
    createAnonymousAccount()
  }

  companion object {
    private const val LINK_ACCOUNT_TRACE = "linkAccount"
  }
}
