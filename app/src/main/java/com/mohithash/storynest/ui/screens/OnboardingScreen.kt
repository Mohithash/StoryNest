@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.mohithash.storynest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mohithash.storynest.domain.Child
import com.mohithash.storynest.domain.Settings
import com.mohithash.storynest.ui.AppViewModel
import com.mohithash.storynest.ui.Label
import com.mohithash.storynest.ui.StatCard

@Composable
fun SettingsForm(initial: Settings, onChange: (Settings) -> Unit) {
    var kids by remember { mutableStateOf(initial.children.ifEmpty { listOf(Child()) }) }
    fun set(i: Int, c: Child) { kids = kids.toMutableList().also { it[i] = c }; onChange(initial.copy(children = kids.filter { it.name.isNotBlank() })) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Label("Children")
        kids.forEachIndexed { i, c ->
            StatCard(container = MaterialTheme.colorScheme.surfaceContainerHigh) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(c.name, { set(i, c.copy(name = it)) }, label = { Text("Name") }, singleLine = true, modifier = Modifier.weight(2f), shape = MaterialTheme.shapes.large)
                    OutlinedTextField(c.age.toString(), { set(i, c.copy(age = it.filter(Char::isDigit).toIntOrNull()?.coerceIn(1, 12) ?: c.age)) }, label = { Text("Age") }, singleLine = true, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.large, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    if (kids.size > 1) IconButton({ kids = kids.filterIndexed { j, _ -> j != i }; onChange(initial.copy(children = kids.filter { it.name.isNotBlank() })) }) { Icon(Icons.Default.Delete, null) }
                }
                OutlinedTextField(c.interests, { set(i, c.copy(interests = it)) }, label = { Text("Loves") }, placeholder = { Text("dinosaurs, her cat Biscuit, trains") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large)
                OutlinedTextField(c.language, { set(i, c.copy(language = it)) }, label = { Text("Story language") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large)
            }
        }
        OutlinedButton({ kids = kids + Child() }, shapes = ButtonDefaults.shapes()) { Text("Add another child") }
    }
}

@Composable
fun OnboardingScreen(vm: AppViewModel) {
    var draft by remember { mutableStateOf(Settings()) }
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = { LargeFlexibleTopAppBar(title = { Text("Who's the hero?") }, subtitle = { Text("A fresh bedtime story every night, starring your child, in big calm text.") }, scrollBehavior = scroll) }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard { SettingsForm(Settings()) { draft = it } }
            Button({ vm.saveSettings(draft) }, enabled = draft.children.isNotEmpty(), shapes = ButtonDefaults.shapes(), modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Start", style = MaterialTheme.typography.titleMedium) }
            Spacer(Modifier.height(24.dp))
        }
    }
}
