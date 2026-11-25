package com.indiasocialmedia.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

data class SocialPost(
    val postId: String = "",
    val uid: String = "",
    val username: String = "",
    val caption: String = "",
    val mediaUrl: String = "",
    val mediaType: String = "image",
    val createdAt: Long = 0L,
    val likeCount: Long = 0L
)

/**
 * Firestore posts helper. The app should only call this after the user is authenticated.
 * Storage uploads are separate; pass the resulting media download URL to publish().
 */
class PostRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun publish(post: SocialPost, onResult: (Result<String>) -> Unit) {
        val ref = db.collection("posts").document()
        val payload = hashMapOf(
            "postId" to ref.id,
            "uid" to post.uid,
            "username" to post.username,
            "caption" to post.caption,
            "mediaUrl" to post.mediaUrl,
            "mediaType" to post.mediaType,
            "createdAt" to System.currentTimeMillis(),
            "likeCount" to 0L
        )
        ref.set(payload)
            .addOnSuccessListener { onResult(Result.success(ref.id)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun listenFeed(onPosts: (List<SocialPost>) -> Unit, onError: (Exception) -> Unit): ListenerRegistration {
        return db.collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(SocialPost::class.java)
                }.orEmpty()
                onPosts(posts)
            }
    }
}
