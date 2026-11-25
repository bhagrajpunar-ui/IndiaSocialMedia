package com.indiasocialmedia.app

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.indiasocialmedia.app.data.AuthRepository

@Composable
fun FirebaseGate() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val firebaseReady = remember { FirebaseApp.getApps(context).isNotEmpty() }
    if (!firebaseReady) {
        FirebaseSetupRequired()
        return
    }
    val auth = remember { AuthRepository() }
    var signedIn by remember { mutableStateOf(auth.currentUser() != null) }
    if (signedIn) {
        IndiaSocialApp()
    } else {
        FirebaseAuthScreen(auth) { signedIn = true }
    }
}

@Composable
private fun FirebaseSetupRequired() {
    MaterialTheme {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("India social media 🇮🇳", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Text("Firebase configuration is needed before login can work.", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(12.dp))
            Text("Download google-services.json from Firebase Console and put it in the app/ folder of this GitHub project. Then build and run the app again.")
        }
    }
}

@Composable
private fun FirebaseAuthScreen(auth: AuthRepository, onSignedIn: () -> Unit) {
    var register by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = lightColorScheme(primary = ColorBrand)) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("India social media 🇮🇳", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(if (register) "Create your account" else "Welcome back")
            Spacer(Modifier.height(20.dp))
            if (register) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Display name") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
            }
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password (6+ characters)") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
            Spacer(Modifier.height(16.dp))
            Button(
                enabled = !busy && email.isNotBlank() && password.length >= 6 && (!register || name.isNotBlank()),
                onClick = {
                    busy = true
                    message = ""
                    val callback: (Result<com.google.firebase.auth.FirebaseUser>) -> Unit = { result ->
                        busy = false
                        result.fold(
                            onSuccess = { onSignedIn() },
                            onFailure = { message = it.localizedMessage ?: "Authentication failed." }
                        )
                    }
                    if (register) auth.register(email, password, name, callback)
                    else auth.signIn(email, password, callback)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text(if (register) "Create account" else "Sign in")
            }
            if (message.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(message, color = MaterialTheme.colorScheme.error)
            }
            TextButton(onClick = { register = !register; message = "" }) {
                Text(if (register) "Already have an account? Sign in" else "New here? Create an account")
            }
        }
    }
}

private val ColorBrand = androidx.compose.ui.graphics.Color(0xFFCE315B)
