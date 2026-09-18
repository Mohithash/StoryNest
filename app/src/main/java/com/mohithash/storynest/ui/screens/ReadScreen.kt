@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.mohithash.storynest.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohithash.storynest.ui.AppViewModel
import com.mohithash.storynest.ui.HeroCard
import com.mohithash.storynest.ui.Label
import com.mohithash.storynest.ui.StatCard
import com.mohithash.storynest.ui.theme.Brand

@Composable
fun ReadScreen(vm: AppViewModel, onBack: () -> Unit, onSequel: () -> Unit) {
    val sel by vm.selected.collectAsState()
    val story = sel ?: run { onBack(); return }
    val c = vm.content(story)
    val cs = MaterialTheme.colorScheme
    var page by remember { mutableIntStateOf(-1) }   // -1 = cover, pages.size = the end
    val total = c.pages.size
    Scaffold(topBar = {
        TopAppBar(title = { Text(if (page in 0 until total) "Page ${page + 1} of $total" else story.title, maxLines = 1) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = cs.surface),
            navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            actions = { IconButton({ vm.toggleFavorite(story) }) { Icon(if (story.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = cs.primary) } })
    }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(horizontal = 20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            if (page >= 0) LinearWavyProgressIndicator(progress = { (page + 1f) / (total + 1) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
            AnimatedContent(page, label = "page", modifier = Modifier.weight(1f)) { p ->
                Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), contentAlignment = Alignment.Center) {
                    when {
                        p < 0 -> HeroCard(colors = listOf(cs.primary, Brand.heroDeep), blobShape = MaterialShapes.Sunny) {
                            Label("A story for ${story.childName}", cs.onPrimary.copy(alpha = 0.8f)); Text(story.title, style = MaterialTheme.typography.displaySmall, color = cs.onPrimary)
                            Text("${story.theme} · ${c.minutes} min · $total pages", color = cs.onPrimary.copy(alpha = 0.85f))
                        }
                        p >= total -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("The End 🌙", style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            if (c.moral.isNotBlank()) StatCard(container = cs.secondaryContainer) { Label("Tonight's thought", cs.onSecondaryContainer); Text(c.moral, style = MaterialTheme.typography.titleLarge, color = cs.onSecondaryContainer) }
                            if (c.questions.isNotEmpty()) StatCard { Label("Talk about it"); c.questions.forEach { Text("•  $it", style = MaterialTheme.typography.bodyLarge) } }
                        }
                        else -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(c.pages[p].text, style = MaterialTheme.typography.headlineSmall.copy(lineHeight = 40.sp), textAlign = TextAlign.Center)
                            if (c.pages[p].illustration_prompt.isNotBlank()) Text("🖼️ ${c.pages[p].illustration_prompt}", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 16.dp)) {
                OutlinedButton({ page-- }, enabled = page >= 0, shapes = ButtonDefaults.shapes(), modifier = Modifier.weight(1f).height(56.dp)) { Text("Back") }
                if (page >= total) Button({ onSequel() }, shapes = ButtonDefaults.shapes(), modifier = Modifier.weight(2f).height(56.dp)) { Text("Write a sequel") }
                else Button({ if (page + 1 >= total) vm.markRead(story); page++ }, shapes = ButtonDefaults.shapes(), modifier = Modifier.weight(2f).height(56.dp)) { Text(if (page < 0) "Start reading" else if (page + 1 >= total) "The end" else "Next page", style = MaterialTheme.typography.titleMedium) }
            }
        }
    }
}
