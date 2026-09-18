package com.mohithash.storynest.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val childName: String,
    val theme: String,
    /** domain.StoryContent JSON */
    val json: String,
    val favorite: Boolean = false,
    val readCount: Int = 0,
    /** Series: id of the story this continues, or 0. */
    val parentId: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
)

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY favorite DESC, createdAt DESC") fun all(): Flow<List<Story>>
    @Insert suspend fun insert(s: Story): Long
    @Update suspend fun update(s: Story)
    @Query("DELETE FROM stories WHERE id = :id") suspend fun delete(id: Long)
}

@Database(entities = [Story::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() { abstract fun stories(): StoryDao }
