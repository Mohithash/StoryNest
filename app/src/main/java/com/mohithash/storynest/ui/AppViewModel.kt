package com.mohithash.storynest.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohithash.storynest.App
import com.mohithash.storynest.ai.AiSettings
import com.mohithash.storynest.data.Story
import com.mohithash.storynest.domain.Child
import com.mohithash.storynest.domain.Settings
import com.mohithash.storynest.domain.StoryContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed interface Job<out T> {
    data object Idle : Job<Nothing>
    data object Loading : Job<Nothing>
    data class Done<T>(val value: T) : Job<T>
    data class Failed(val message: String) : Job<Nothing>
}

class AppViewModel(private val app: App) : ViewModel() {
    private val db = app.db
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    val client get() = app.client
    val ai: StateFlow<AiSettings> = app.store.flow("ai", AiSettings.serializer(), AiSettings())
    val settings: StateFlow<Settings> = app.store.flow("settings", Settings.serializer(), Settings())
    fun saveAi(a: AiSettings) = app.store.set("ai", AiSettings.serializer(), a)
    fun saveSettings(s: Settings) = app.store.set("settings", Settings.serializer(), s.copy(onboarded = true))

    val stories = db.stories().all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val selected = MutableStateFlow<Story?>(null)
    private val _write = MutableStateFlow<Job<Story>>(Job.Idle)
    val write: StateFlow<Job<Story>> = _write

    fun content(s: Story): StoryContent = runCatching { json.decodeFromString(StoryContent.serializer(), s.json) }.getOrDefault(StoryContent())

    fun writeStory(child: Child, theme: String, minutes: Int, wish: String, sequelOf: Story?) {
        _write.value = Job.Loading
        viewModelScope.launch {
            _write.value = runCatching { app.stories.write(ai.value, child, theme, minutes, wish, sequelOf?.let(::content)) }.fold({ c ->
                val row = Story(title = c.title.ifBlank { "A story for ${child.name}" }, childName = child.name, theme = theme, json = json.encodeToString(StoryContent.serializer(), c), parentId = sequelOf?.id ?: 0)
                val id = db.stories().insert(row); val saved = row.copy(id = id); selected.value = saved; Job.Done(saved)
            }, { Job.Failed(it.message ?: "Failed") })
        }
    }
    fun clearWrite() { _write.value = Job.Idle }
    fun open(s: Story) { selected.value = s }
    fun toggleFavorite(s: Story) = viewModelScope.launch { val u = s.copy(favorite = !s.favorite); db.stories().update(u); if (selected.value?.id == s.id) selected.value = u }
    fun markRead(s: Story) = viewModelScope.launch { val u = s.copy(readCount = s.readCount + 1); db.stories().update(u); if (selected.value?.id == s.id) selected.value = u }
    fun delete(id: Long) = viewModelScope.launch { db.stories().delete(id); if (selected.value?.id == id) selected.value = null }
}
