package com.indiasocialmedia.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Email/password authentication.
 * Enable Email/Password in Firebase Console > Authentication > Sign-in method first.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun currentUser(): FirebaseUser? = auth.currentUser

    fun register(email: String, password: String, displayName: String, onResult: (Result<FirebaseUser>) -> Unit) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    onResult(Result.failure(IllegalStateException("Account created but user was unavailable.")))
                    return@addOnSuccessListener
                }
                val profile = mapOf(
                    "uid" to user.uid,
                    "email" to user.email.orEmpty(),
                    "displayName" to displayName.trim(),
                    "username" to email.substringBefore("@").lowercase(),
                    "bio" to "",
                    "photoUrl" to "",
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection("users").document(user.uid).set(profile)
                    .addOnSuccessListener { onResult(Result.success(user)) }
                    .addOnFailureListener { onResult(Result.failure(it)) }
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun signIn(email: String, password: String, onResult: (Result<FirebaseUser>) -> Unit) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) onResult(Result.success(user))
                else onResult(Result.failure(IllegalStateException("Sign-in returned no user.")))
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun signOut() = auth.signOut()
}
