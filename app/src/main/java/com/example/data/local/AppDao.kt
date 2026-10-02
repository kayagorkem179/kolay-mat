package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedQuestionDao {
    @Query("SELECT * FROM saved_questions ORDER BY timestamp DESC")
    fun getAllSavedQuestions(): Flow<List<SavedQuestionEntity>>

    @Query("SELECT * FROM saved_questions WHERE isSolved = 0 ORDER BY timestamp DESC")
    fun getUnsolvedQuestions(): Flow<List<SavedQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(question: SavedQuestionEntity)

    @Query("DELETE FROM saved_questions WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE saved_questions SET isSolved = :isSolved WHERE id = :id")
    suspend fun updateSolvedStatus(id: Int, isSolved: Boolean)

    @Query("SELECT COUNT(*) FROM saved_questions")
    suspend fun getCount(): Int
}

@Dao
interface ExamResultDao {
    @Query("SELECT * FROM exam_results ORDER BY timestamp DESC")
    fun getAllExamResults(): Flow<List<ExamResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ExamResultEntity)

    @Query("SELECT AVG(netScore) FROM exam_results")
    suspend fun getAverageNet(): Float?

    @Query("SELECT SUM(totalQuestions) FROM exam_results")
    suspend fun getTotalQuestionsAnswered(): Int?
}

@Dao
interface StudyPlanDao {
    @Query("SELECT * FROM study_plan ORDER BY id ASC")
    fun getAllPlanItems(): Flow<List<StudyPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<StudyPlanEntity>)

    @Update
    suspend fun updatePlan(item: StudyPlanEntity)

    @Query("DELETE FROM study_plan")
    suspend fun clearPlan()
}

@Dao
interface SecurityLogDao {
    @Query("SELECT * FROM security_logs ORDER BY timestamp DESC LIMIT 20")
    fun getRecentLogs(): Flow<List<SecurityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SecurityLogEntity)
}
