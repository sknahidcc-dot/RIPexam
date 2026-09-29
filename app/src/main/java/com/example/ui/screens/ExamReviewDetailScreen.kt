package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamHistoryEntity
import com.example.data.ExamRepository
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BengaliOptions = listOf("ক", "খ", "গ", "ঘ")

@Composable
fun ExamReviewDetailScreen(
    historyId: Long,
    getHistoryEntity: suspend (Long) -> ExamHistoryEntity?,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var history by remember { mutableStateOf<ExamHistoryEntity?>(null) }

    LaunchedEffect(historyId) {
        history = getHistoryEntity(historyId)
    }

    val exam = history

    if (exam == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AmoledBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("লোড হচ্ছে...", color = TextSecondary)
        }
        return
    }

    val userAnswers = remember(exam.userAnswersJson) {
        ExamRepository.decodeMap(exam.userAnswersJson)
    }
    val answerKeys = remember(exam.answerKeysJson) {
        ExamRepository.decodeMap(exam.answerKeysJson)
    }

    val dateStr = remember(exam.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(exam.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .testTag("exam_review_detail_screen")
    ) {
        // Header
        Surface(
            color = DarkSurface,
            tonalElevation = 4.dp,
            border = BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .background(DarkSurfaceElevated, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = exam.title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$dateStr • স্কোর: ${String.format(Locale.US, "%.2f", exam.finalScore)}/${exam.totalQuestions}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Summary Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceElevated)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "সঠিক: ${exam.correctCount}টি",
                color = EmeraldGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ভুল: ${exam.wrongCount}টি",
                color = CoralRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "বাদ: ${exam.skippedCount}টি",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (exam.isPassed) "পাস" else "ফেল",
                color = if (exam.isPassed) EmeraldGreen else CoralRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Question List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(exam.totalQuestions) { index ->
                val qNum = index + 1
                val uAns = userAnswers[qNum]
                val aKey = answerKeys[qNum]

                val isSkipped = uAns == null
                val isCorrect = !isSkipped && aKey != null && uAns == aKey
                val isWrong = !isSkipped && aKey != null && uAns != aKey

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(
                        1.dp,
                        when {
                            isCorrect -> EmeraldGreen.copy(alpha = 0.35f)
                            isWrong -> CoralRed.copy(alpha = 0.35f)
                            else -> DarkSurfaceBorder
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(DarkSurfaceElevated, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$qNum",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "আপনার উত্তর: ", color = TextSecondary, fontSize = 12.sp)
                                    Text(
                                        text = if (uAns != null) BengaliOptions.getOrElse(uAns) { "-" } else "অনুত্তরীত",
                                        color = when {
                                            isCorrect -> EmeraldGreen
                                            isWrong -> CoralRed
                                            else -> TextMuted
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                if (aKey != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "সঠিক উত্তর: ", color = TextMuted, fontSize = 11.sp)
                                        Text(
                                            text = BengaliOptions.getOrElse(aKey) { "-" },
                                            color = EmeraldGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        Icon(
                            imageVector = when {
                                isCorrect -> Icons.Default.CheckCircle
                                isWrong -> Icons.Default.Cancel
                                else -> Icons.Default.RemoveCircle
                            },
                            contentDescription = null,
                            tint = when {
                                isCorrect -> EmeraldGreen
                                isWrong -> CoralRed
                                else -> TextMuted
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
