package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    // Active Ongoing Exam (for instant crash and call recovery)
    @Query("SELECT * FROM active_exam WHERE id = 1 LIMIT 1")
    fun getActiveExam(): Flow<ActiveExamEntity?>

    @Query("SELECT * FROM active_exam WHERE id = 1 LIMIT 1")
    suspend fun getActiveExamOnce(): ActiveExamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveActiveExam(activeExam: ActiveExamEntity)

    @Query("DELETE FROM active_exam WHERE id = 1")
    suspend fun clearActiveExam()

    // Exam History (local persistence)
    @Query("SELECT * FROM exam_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ExamHistoryEntity>>

    @Query("SELECT * FROM exam_history WHERE id = :id LIMIT 1")
    suspend fun getHistoryById(id: Long): ExamHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ExamHistoryEntity): Long

    @Query("DELETE FROM exam_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM exam_history")
    suspend fun clearAllHistory()
}
