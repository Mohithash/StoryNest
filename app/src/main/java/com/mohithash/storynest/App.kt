package com.mohithash.storynest

import android.app.Application
import androidx.room.Room
import com.mohithash.storynest.ai.AiClient
import com.mohithash.storynest.ai.StoryAi
import com.mohithash.storynest.data.AppDb
import com.mohithash.storynest.data.JsonStore

class App : Application() {
    lateinit var db: AppDb
    lateinit var store: JsonStore
    val client = AiClient()
    val stories by lazy { StoryAi(client) }
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDb::class.java, "storynest.db").build()
        store = JsonStore(this)
    }
}
