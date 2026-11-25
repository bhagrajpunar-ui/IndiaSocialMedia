package com.indiasocialmedia.app

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@Composable
fun CreatePostScreen(onPostCreated: () -> Unit) {
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var caption by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImage = uri
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create a post", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { imagePicker.launch("image/*") }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text(if (selectedImage == null) "Choose photo from Gallery" else "Choose another photo")
        }
        selectedImage?.let {
            Spacer(Modifier.height(8.dp))
            Text("Photo selected ✓", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = caption, onValueChange = { caption = it },
            label = { Text("Write a caption...") }, modifier = Modifier.fillMaxWidth(), minLines = 3
        )
        Spacer(Modifier.height(16.dp))
        Button(
            enabled = !busy,
            onClick = {
                val uri = selectedImage
                val user = FirebaseAuth.getInstance().currentUser
                if (uri == null) { message = "पहले Gallery से फोटो चुनें।"; return@Button }
                if (user == null) { message = "कृपया पहले Login करें।"; return@Button }
                busy = true
                message = "Photo uploading..."
                val postId = UUID.randomUUID().toString()
                val imageRef = FirebaseStorage.getInstance().reference.child("posts/${user.uid}/$postId.jpg")
                imageRef.putFile(uri).continueWithTask { task ->
                    if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                    imageRef.downloadUrl
                }.addOnSuccessListener { downloadUri ->
                    val username = user.displayName?.takeIf { it.isNotBlank() } ?: user.email?.substringBefore("@") ?: "user"
                    val post = hashMapOf(
                        "postId" to postId, "userId" to user.uid, "username" to username,
                        "caption" to caption.trim(), "imageUrl" to downloadUri.toString(),
                        "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )
                    FirebaseFirestore.getInstance().collection("posts").document(postId).set(post)
                        .addOnSuccessListener { busy = false; message = "Post published!"; onPostCreated() }
                        .addOnFailureListener { error -> busy = false; message = error.localizedMessage ?: "Post save failed" }
                }.addOnFailureListener { error ->
                    busy = false; message = error.localizedMessage ?: "Photo upload failed"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Publish post")
        }
        if (message.isNotBlank()) { Spacer(Modifier.height(12.dp)); Text(message) }
    }
}
