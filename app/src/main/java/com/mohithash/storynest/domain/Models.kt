package com.mohithash.storynest.domain

import kotlinx.serialization.Serializable

@Serializable
data class Child(val name: String = "", val age: Int = 5, val interests: String = "", val language: String = "English")

@Serializable data class Settings(val children: List<Child> = emptyList(), val onboarded: Boolean = false)

@Serializable data class Page(val text: String, val illustration_prompt: String = "")
@Serializable
data class StoryContent(
    val title: String = "",
    val pages: List<Page> = emptyList(),
    val moral: String = "",
    val questions: List<String> = emptyList(),   // 2-3 "talk about it" questions
    val minutes: Int = 5,
)

val THEMES = listOf("Adventure", "Bedtime calm", "Friendship", "Being brave", "Sharing", "Animals", "Space", "Dinosaurs", "Ocean", "Magic", "Silly", "First day of school")
val LENGTHS = listOf(3 to "Quick (3 min)", 5 to "Classic (5 min)", 8 to "Long (8 min)")
