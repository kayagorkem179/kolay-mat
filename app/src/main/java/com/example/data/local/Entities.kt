package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_questions")
data class SavedQuestionEntity(
    @PrimaryKey val id: Int,
    val examType: String,
    val topic: String,
    val questionText: String,
    val mathFormula: String?,
    val optionsJson: String,
    val correctIndex: Int,
    val explanation: String,
    val userSelectedWrongIndex: Int = -1,
    val userNote: String = "",
    val isStarred: Boolean = true,
    val isSolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val examType: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val emptyCount: Int,
    val netScore: Float,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_plan")
data class StudyPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayName: String,
    val topicName: String,
    val targetQuestions: Int,
    val completedQuestions: Int = 0,
    val estimatedMinutes: Int,
    val isDone: Boolean = false
)

@Entity(tableName = "security_logs")
data class SecurityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventType: String,
    val detail: String,
    val hashVerified: String,
    val serverNode: String,
    val timestamp: Long = System.currentTimeMillis()
)
