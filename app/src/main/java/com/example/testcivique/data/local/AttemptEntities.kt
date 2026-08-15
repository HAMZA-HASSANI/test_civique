package com.example.testcivique.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "attempts")
data class AttemptEntity(
    @PrimaryKey val id: String,
    val mode: String,
    val target: String,
    val theme: String?,
    val startedAt: Long,
    val completedAt: Long,
    val durationSeconds: Int,
    val score: Int,
    val total: Int,
    val passed: Boolean,
    val contentVersion: Int = 2,
)

@Entity(
    tableName = "attempt_answers",
    foreignKeys = [
        ForeignKey(
            entity = AttemptEntity::class,
            parentColumns = ["id"],
            childColumns = ["attemptId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("attemptId"), Index("conceptId"), Index("theme")],
)
data class AttemptAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val attemptId: String,
    val questionId: String,
    val conceptId: String,
    val theme: String,
    val questionType: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val selectedIndex: Int,
    val correctIndex: Int,
    val isCorrect: Boolean,
    val explanation: String,
    val sourceTitle: String,
    val sourceUrl: String,
)

data class AttemptWithAnswers(
    @Embedded val attempt: AttemptEntity,
    @Relation(parentColumn = "id", entityColumn = "attemptId")
    val answers: List<AttemptAnswerEntity>,
)
