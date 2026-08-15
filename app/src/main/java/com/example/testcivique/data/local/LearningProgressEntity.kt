package com.example.testcivique.data.local

import androidx.room.Entity

@Entity(
    tableName = "learning_progress",
    primaryKeys = ["target", "theme", "chapterIndex"],
)
data class LearningProgressEntity(
    val target: String,
    val theme: String,
    val chapterIndex: Int,
    val completedAt: Long,
)
