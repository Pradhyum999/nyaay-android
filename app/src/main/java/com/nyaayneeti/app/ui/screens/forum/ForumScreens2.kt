package com.nyaayneeti.app.ui.screens.forum

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.ForumCategory
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.screens.auth.fieldColors
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.ForumViewModel

@Composable
fun NewPostScreen(vm: ForumViewModel, onDone: () -> Unit, onBack: () -> Unit) {
    var category by remember { mutableStateOf(ForumCategory.GENERAL) }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    Scaffold(topBar = { NyaayTopAppBar("New post", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Category", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ForumCategory.values().forEach { c ->
                    FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c.name.replace("_", " ")) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub))
                }
            }
            OutlinedTextField(title, { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            OutlinedTextField(body, { body = it }, label = { Text("Write your post…") }, modifier = Modifier.fillMaxWidth().height(160.dp), colors = fieldColors())
            OutlinedTextField(tags, { tags = it }, label = { Text("Tags (comma separated)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            Button(onClick = { vm.createPost(category, title, body, tags); onDone() }, enabled = title.isNotBlank() && body.isNotBlank(), modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                Text("Post", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ForumPostDetailScreen(vm: ForumViewModel, onBack: () -> Unit) {
    val post by vm.selectedPost.collectAsState()
    val comments by vm.comments.collectAsState()
    val myVote by vm.myVote.collectAsState()
    var comment by remember { mutableStateOf("") }
    Scaffold(topBar = { NyaayTopAppBar("Discussion", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(16.dp)) {
            post?.let { p ->
                NyaayCard {
                    Text(p.authorName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(p.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(p.body, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { vm.vote(p.id, 1) }) { Icon(Icons.Default.ThumbUp, null, tint = if (myVote == 1) GoldPrimary else TextSub) }
                        Text("${p.upvotes}", color = GoldPrimary)
                        Spacer(Modifier.width(16.dp))
                        IconButton(onClick = { vm.vote(p.id, -1) }) { Icon(Icons.Default.ThumbDown, null, tint = if (myVote == -1) RedUrgent else TextSub) }
                        Text("${p.downvotes}", color = TextSub)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            SectionTitle("Comments (${comments.size})")
            Spacer(Modifier.height(8.dp))
            comments.forEach { c ->
                NyaayCard {
                    Text(c.authorName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(c.body, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(comment, { comment = it }, label = { Text("Add a comment…") }, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            Spacer(Modifier.height(8.dp))
            Button(onClick = { val p = post; if (p != null && comment.isNotBlank()) { vm.addComment(p.id, comment); comment = "" } }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) {
                Text("Comment", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
