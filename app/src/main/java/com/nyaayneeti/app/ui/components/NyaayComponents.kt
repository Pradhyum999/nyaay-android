package com.nyaayneeti.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NyaayTopAppBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextMain, fontWeight = FontWeight.Bold)
                subtitle?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = GoldPrimary) }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextMain)
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBackground),
        windowInsets = TopAppBarDefaults.windowInsets
    )
}

@Composable
fun NyaayCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ObsidianSurface)
            .border(1.dp, ObsidianBorder, shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun PillBadge(text: String, container: Color, content: Color) {
    Surface(color = container, shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, content.copy(alpha = 0.4f))) {
        Text(text, color = content, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
    }
}

@Composable
fun CasePriorityBadge(priority: CasePriority) {
    if (priority == CasePriority.URGENT) PillBadge("URGENT", RedContainer, RedUrgent) else PillBadge("NORMAL", GoldContainer, GoldPrimary)
}

@Composable
fun CaseStatusBadge(status: CaseStatus) = PillBadge(status.name, GreenContainer, GreenSuccess)

@Composable
fun RoleBadge(role: UserRole) = PillBadge(role.name.replace("_", " "), GoldContainer, GoldPrimary)

@Composable
fun EmptyState(icon: ImageVector, title: String, subtitle: String? = null) {
    Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = TextFaint, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        subtitle?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = GoldPrimary, modifier = Modifier.padding(top = 4.dp))
}

@Composable
fun StarRating(rating: Double, size: Int = 16) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { i ->
            Icon(
                imageVector = if (i < rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(size.dp)
            )
        }
    }
}
