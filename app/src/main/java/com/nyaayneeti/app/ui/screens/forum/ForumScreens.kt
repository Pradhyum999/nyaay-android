package com.nyaayneeti.app.ui.screens.forum

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.PostEntity
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.ForumViewModel

@Composable
fun ForumFeed(vm: ForumViewModel, onOpenPost: (String) -> Unit, onNewPost: () -> Unit) {
    var trending by remember { mutableStateOf(true) }
    val trendingList by vm.trending.collectAsState(emptyList())
    val latestList by vm.latest.collectAsState(emptyList())
    val posts = if (trending) trendingList else latestList

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = trending, onClick = { trending = true }, label = { Text("🔥 Trending") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub))
                FilterChip(selected = !trending, onClick = { trending = false }, label = { Text("Latest") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub))
            }
            TextButton(onClick = onNewPost) { Text("+ Post", color = GoldPrimary) }
        }
        if (posts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Forum, "No posts yet", "Start the conversation.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(posts, key = { it.id }) { p -> PostCard(p, onOpenPost) }
            }
        }
    }
}

@Composable
private fun PostCard(p: PostEntity, onOpenPost: (String) -> Unit) {
    NyaayCard(onClick = { onOpenPost(p.id) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(p.authorName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            PillBadge(p.category.name.replace("_", " "), ObsidianSurfaceElevated, TextSub)
        }
        Spacer(Modifier.height(6.dp))
        Text(p.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(p.body, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.ThumbUp, null, tint = GoldPrimary, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("${p.upvotes}", style = MaterialTheme.typography.labelSmall) }
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.ThumbDown, null, tint = TextSub, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("${p.downvotes}", style = MaterialTheme.typography.labelSmall) }
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Comment, null, tint = TextSub, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("${p.commentCount}", style = MaterialTheme.typography.labelSmall) }
        }
    }
}
