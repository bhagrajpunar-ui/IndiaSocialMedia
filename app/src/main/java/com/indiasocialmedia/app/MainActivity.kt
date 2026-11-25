package com.indiasocialmedia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Brand = Color(0xFFCE315B)
private val StoryNames = listOf("Your story", "Aarav", "Priya", "Rahul", "Neha", "Kabir")
private data class Post(val user: String, val place: String, val caption: String, val color: Color)
private val Posts = listOf(
    Post("india.creator", "Jaipur, Rajasthan", "Beautiful India 🇮🇳 #IndiaSocial", Color(0xFFE7B67A)),
    Post("travel_diary", "Himalayas", "Find your own way. ✨", Color(0xFF87B7C9)),
    Post("daily_moments", "Jaisalmer", "Golden evenings in Rajasthan.", Color(0xFFD49B65))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FirebaseGate() }
    }
}

@Composable
fun IndiaSocialApp() {
    var page by remember { mutableIntStateOf(0) }
    var likedPosts by remember { mutableStateOf(setOf<Int>()) }
    MaterialTheme(colorScheme = lightColorScheme(primary = Brand)) {
        Scaffold(
            topBar = {
                Row(
                    Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("India social media", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Row {
                        IconButton(onClick = { page = 2 }) { Icon(Icons.Default.FavoriteBorder, "Notifications") }
                        IconButton(onClick = { page = 3 }) { Icon(Icons.Default.Send, "Messages") }
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    val icons = listOf(Icons.Default.Home, Icons.Default.Search, Icons.Default.VideoLibrary, Icons.Default.AddBox, Icons.Default.Person)
                    val labels = listOf("Home", "Search", "Reels", "Create", "Profile")
                    icons.forEachIndexed { index, icon ->
                        NavigationBarItem(
                            selected = page == index,
                            onClick = { page = index },
                            icon = { Icon(icon, labels[index]) },
                            label = { Text(labels[index]) }
                        )
                    }
                }
            }
        ) { padding ->
            when (page) {
                0 -> HomeFeed(Modifier.padding(padding), likedPosts) { id ->
                    likedPosts = if (id in likedPosts) likedPosts - id else likedPosts + id
                }
                1 -> SimplePage("Search & Explore", "Find people, hashtags and posts")
                2 -> SimplePage("Reels", "Short videos will appear here")
                3 -> SimplePage("Create post", "Photo and video upload will be connected next")
                else -> SimplePage("Your Profile", "@your_username\nPosts • Followers • Following")
            }
        }
    }
}

@Composable
private fun HomeFeed(modifier: Modifier, liked: Set<Int>, onLike: (Int) -> Unit) {
    LazyColumn(modifier.fillMaxSize()) {
        item {
            Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                StoryNames.forEachIndexed { index, name ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.size(58.dp).background(if (index == 0) Color.LightGray else Brand, CircleShape).padding(3.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (index == 0) "+" else name.take(1), color = Brand, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Text(name, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
        items(Posts) { post ->
            val index = Posts.indexOf(post)
            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(36.dp).background(Brand, CircleShape), contentAlignment = Alignment.Center) {
                        Text(post.user.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f).padding(start = 10.dp)) {
                        Text(post.user, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(post.place, fontSize = 11.sp, color = Color.Gray)
                    }
                    Icon(Icons.Default.MoreVert, "More")
                }
                Box(Modifier.fillMaxWidth().height(280.dp).background(post.color), contentAlignment = Alignment.Center) {
                    Text("India 🇮🇳", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onLike(index) }) {
                        Icon(if (index in liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            "Like", tint = if (index in liked) Brand else Color.Black)
                    }
                    IconButton(onClick = {}) { Icon(Icons.Default.ChatBubbleOutline, "Comment") }
                    IconButton(onClick = {}) { Icon(Icons.Default.Send, "Share") }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {}) { Icon(Icons.Default.BookmarkBorder, "Save") }
                }
                Text("${120 + index * 57 + if (index in liked) 1 else 0} likes",
                    Modifier.padding(horizontal = 14.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("${post.user}  ${post.caption}", Modifier.padding(horizontal = 14.dp, vertical = 6.dp), fontSize = 13.sp)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun SimplePage(title: String, subtitle: String) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(subtitle, color = Color.Gray)
    }
}
