package com.developer.quizapp.data.remote

import android.net.Uri
import android.util.Log
import com.developer.quizapp.utils.Constants.HOLDER_ICON

import com.developer.quizapp.utils.Constants.USERS
import com.developer.quizapp.models.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import javax.inject.Inject

class AccountService @Inject constructor(
    private val auth: FirebaseAuth,
    private val formatter: SimpleDateFormat
) {
    private val users = FirebaseFirestore.getInstance().collection(USERS)
    private val storage = Firebase.storage

    suspend fun register(

        name: String,
        email: String,
        password: String,
        admin: Int,
        imageUri: String?,

        ): User {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid!!
        var imageURL = HOLDER_ICON

        imageUri?.let {
            val imageUploadResult =
                storage.getReference(email).putFile(Uri.parse(it)).await()
            imageURL =
                imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
        }
        val user = User(uid, name,email,admin,imageURL)
        users.document(uid).set(user).await()
        return user
    }

    suspend fun login(email: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return if (result.user != null) {
            val currentUid = auth.uid!!
            Log.i("TAG", "login:1 ${currentUid}")
            val currentUser = users.document(currentUid).get().await().toObject(User::class.java)
                ?: throw  IllegalStateException()
            Log.i("TAG", "login:2 ${currentUser}")
            currentUser
        } else {
            Log.i("TAG", "login:3 ")
            User()
        }
    }
}