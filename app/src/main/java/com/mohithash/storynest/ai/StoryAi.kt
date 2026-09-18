package com.mohithash.storynest.ai

import com.mohithash.storynest.domain.Child
import com.mohithash.storynest.domain.StoryContent

class StoryAi(private val client: AiClient) {
    private val schema = Schema.obj(
        "title" to Schema.str, "pages" to Schema.arr(Schema.obj("text" to Schema.str, "illustration_prompt" to Schema.str)),
        "moral" to Schema.str, "questions" to Schema.arr(Schema.str), "minutes" to Schema.int,
    )

    suspend fun write(ai: AiSettings, c: Child, theme: String, minutes: Int, wish: String, previous: StoryContent?): StoryContent {
        val words = minutes * 110
        val system = """You are a beloved children's author. Write an original bedtime story in ${c.language} for ${c.name}, age ${c.age}${if (c.interests.isNotBlank()) ", who loves ${c.interests}" else ""}.
            |Theme: $theme. About $words words, split into ${minutes + 2} to ${minutes + 5} short pages (2-4 sentences each, one moment per page). ${c.name} is the hero. Vocabulary suited to age ${c.age}; gentle rhythm; a calm, safe ending suitable for sleep.
            |No violence, no scary villains for under-6s, no brands. illustration_prompt: one line describing the page's picture. moral: one warm sentence, not preachy. questions: 2-3 simple things a parent can ask afterwards. minutes: reading time.""".trimMargin()
        val user = buildString {
            if (previous != null) append("This is a sequel to \"${previous.title}\". Previously: ${previous.pages.joinToString(" ") { it.text }.take(1200)}\n\nContinue the adventure with a new self-contained plot.\n")
            if (wish.isNotBlank()) append("Tonight's special request: $wish")
            if (isEmpty()) append("Write tonight's story.")
        }
        return client.ask(ai, system, user, schema, maxTokens = 6000)
    }
}
