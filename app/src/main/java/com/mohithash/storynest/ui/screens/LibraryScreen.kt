@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.mohithash.storynest.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.mohithash.storynest.ui.AppViewModel
import com.mohithash.storynest.ui.EmptyState
import com.mohithash.storynest.ui.HeroCard
import com.mohithash.storynest.ui.Label
import com.mohithash.storynest.ui.ShapeIcon
import com.mohithash.storynest.ui.StatCard
import com.mohithash.storynest.ui.theme.Brand

@Composable
fun LibraryScreen(vm: AppViewModel, onNew: () -> Unit, onRead: () -> Unit, onSettings: () -> Unit) {
    val stories by vm.stories.collectAsState()
    val s by vm.settings.collectAsState()
    val cs = MaterialTheme.colorScheme
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = { MediumFlexibleTopAppBar(title = { Text("Story Nest") }, subtitle = { Text("${stories.size} stories for ${s.children.joinToString { it.name }.ifBlank { "your little ones" }}") }, actions = { IconButton(onSettings) { Icon(Icons.Default.Settings, null) } }, scrollBehavior = scroll, colors = TopAppBarDefaults.topAppBarColors(containerColor = cs.surface, scrolledContainerColor = cs.surface)) },
        floatingActionButton = { ExtendedFloatingActionButton(onClick = onNew, icon = { Icon(Icons.Default.NightsStay, null) }, text = { Text("Tonight's story") }, containerColor = cs.primary, contentColor = cs.onPrimary) }) { pad ->
        LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                HeroCard(colors = listOf(cs.primary, Brand.heroDeep), blobShape = MaterialShapes.Sunny) {
                    val on = cs.onPrimary
                    Label("Bedtime, made easy", on.copy(alpha = 0.8f))
                    Text("A brand‑new story where your child is the hero — every night.", style = MaterialTheme.typography.headlineSmall, color = on)
                    Text("Pick a theme and length, add a special request, and read it page by page in big, calm text.", color = on.copy(alpha = 0.85f))
                }
            }
            if (stories.isEmpty()) item { EmptyState(Icons.Default.AutoStories, "No stories yet", "Tap Tonight's story to write the first one.") }
            items(stories, key = { it.id }) { st ->
                val c = vm.content(st)
                StatCard(Modifier.clip(MaterialTheme.shapes.extraLarge).clickable { vm.open(st); onRead() }) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ShapeIcon(Icons.Default.AutoStories, cs.secondaryContainer, cs.onSecondaryContainer, MaterialShapes.Clover4Leaf)
                        Column(Modifier.weight(1f)) {
                            Text(st.title, style = MaterialTheme.typography.titleMedium)
                            Text(listOf("for ${st.childName}", st.theme, "${c.minutes} min", if (st.parentId != 0L) "sequel" else "", if (st.readCount > 0) "read ${st.readCount}×" else "").filter { it.isNotBlank() }.joinToString(" · "), style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                        }
                        IconButton({ vm.toggleFavorite(st) }) { Icon(if (st.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = cs.primary) }
                        IconButton({ vm.delete(st.id) }) { Icon(Icons.Default.Delete, null, tint = cs.onSurfaceVariant) }
                    }
                }
            }
            item { Spacer(Modifier.height(88.dp)) }
        }
    }
}
