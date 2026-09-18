@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)

package com.mohithash.storynest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohithash.storynest.domain.LENGTHS
import com.mohithash.storynest.domain.THEMES
import com.mohithash.storynest.ui.AppViewModel
import com.mohithash.storynest.ui.Job
import com.mohithash.storynest.ui.Label
import com.mohithash.storynest.ui.StatCard

@Composable
fun NewStoryScreen(vm: AppViewModel, onBack: () -> Unit, onDone: () -> Unit) {
    val ai by vm.ai.collectAsState()
    val s by vm.settings.collectAsState()
    val job by vm.write.collectAsState()
    val sequelOf by vm.selected.collectAsState()
    val cs = MaterialTheme.colorScheme
    var childIdx by remember { mutableIntStateOf(0) }
    var theme by remember { mutableStateOf(THEMES[1]) }
    var minutes by remember { mutableIntStateOf(5) }
    var wish by remember { mutableStateOf("") }
    var asSequel by remember { mutableStateOf(false) }
    LaunchedEffect(job) { if (job is Job.Done) { vm.clearWrite(); onDone() } }
    Scaffold(topBar = { TopAppBar(title = { Text("Tonight's story") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = cs.surface), navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard {
                Label("For"); FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { s.children.forEachIndexed { i, c -> FilterChip(selected = childIdx == i, onClick = { childIdx = i }, label = { Text("${c.name}, ${c.age}") }) } }
                Label("Theme"); FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { THEMES.forEach { t -> FilterChip(selected = theme == t, onClick = { theme = t }, label = { Text(t) }) } }
                Label("Length"); FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { LENGTHS.forEach { (m, l) -> FilterChip(selected = minutes == m, onClick = { minutes = m }, label = { Text(l) }) } }
                OutlinedTextField(wish, { wish = it }, label = { Text("Special request (optional)") }, placeholder = { Text("include her cat Biscuit; she was scared of the dark today") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large)
                sequelOf?.let { FilterChip(selected = asSequel, onClick = { asSequel = !asSequel }, label = { Text("Continue “${it.title}”") }) }
            }
            (job as? Job.Failed)?.let { StatCard(container = cs.errorContainer) { Text(it.message, color = cs.onErrorContainer) } }
            if (!ai.configured) Text("Add an API key in Settings to write stories.", color = cs.error, style = MaterialTheme.typography.bodySmall)
            Button({ s.children.getOrNull(childIdx)?.let { vm.writeStory(it, theme, minutes, wish, if (asSequel) sequelOf else null) } }, enabled = ai.configured && s.children.isNotEmpty() && job != Job.Loading, shapes = ButtonDefaults.shapes(), modifier = Modifier.fillMaxWidth().height(56.dp)) {
                if (job == Job.Loading) { LoadingIndicator(Modifier.size(22.dp)); Spacer(Modifier.size(10.dp)); Text("Writing…") } else { Icon(Icons.Default.AutoAwesome, null); Spacer(Modifier.size(8.dp)); Text("Write the story", style = MaterialTheme.typography.titleMedium) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
