package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_exam")
data class ActiveExamEntity(
    @PrimaryKey val id: Int = 1,
    val title: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val remainingSeconds: Long,
    val negativeMarkRate: Float,
    val passPercentage: Float,
    val step: Int, // 1: Exam Phase, 2: Answer Key & Evaluation Phase
    val userAnswersJson: String,
    val answerKeysJson: String,
    val startTimeMillis: Long,
    val lastSavedMillis: Long
)

@Entity(tableName = "exam_history")
data class ExamHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val timeSpentSeconds: Long,
    val negativeMarkRate: Float,
    val passPercentage: Float,
    val correctCount: Int,
    val wrongCount: Int,
    val skippedCount: Int,
    val negativeMarksDeducted: Float,
    val finalScore: Float,
    val isPassed: Boolean,
    val userAnswersJson: String,
    val answerKeysJson: String,
    val timestamp: Long
)
