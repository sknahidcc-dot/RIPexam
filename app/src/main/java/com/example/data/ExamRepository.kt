package com.example.data

import kotlinx.coroutines.flow.Flow

class ExamRepository(private val examDao: ExamDao) {

    val activeExam: Flow<ActiveExamEntity?> = examDao.getActiveExam()
    val allHistory: Flow<List<ExamHistoryEntity>> = examDao.getAllHistory()

    suspend fun getActiveExamOnce(): ActiveExamEntity? = examDao.getActiveExamOnce()

    suspend fun saveActiveExam(activeExam: ActiveExamEntity) {
        examDao.saveActiveExam(activeExam)
    }

    suspend fun clearActiveExam() {
        examDao.clearActiveExam()
    }

    suspend fun getHistoryById(id: Long): ExamHistoryEntity? = examDao.getHistoryById(id)

    suspend fun insertHistory(history: ExamHistoryEntity): Long = examDao.insertHistory(history)

    suspend fun deleteHistoryById(id: Long) = examDao.deleteHistoryById(id)

    suspend fun clearAllHistory() = examDao.clearAllHistory()

    companion object {
        fun encodeMap(map: Map<Int, Int>): String {
            if (map.isEmpty()) return ""
            return map.entries.joinToString(",") { "${it.key}:${it.value}" }
        }

        fun decodeMap(encoded: String?): Map<Int, Int> {
            if (encoded.isNullOrBlank()) return emptyMap()
            val result = mutableMapOf<Int, Int>()
            encoded.split(",").forEach { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) {
                    val k = parts[0].toIntOrNull()
                    val v = parts[1].toIntOrNull()
                    if (k != null && v != null) {
                        result[k] = v
                    }
                }
            }
            return result
        }
    }
}
