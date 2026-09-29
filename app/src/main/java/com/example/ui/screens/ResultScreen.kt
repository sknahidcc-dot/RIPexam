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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

private val BengaliOptions = listOf("ক", "খ", "গ", "ঘ")

@Composable
fun ResultScreen(
    historyId: Long,
    getHistoryEntity: suspend (Long) -> ExamHistoryEntity?,
    onGoToHome: () -> Unit,
    onStartNewExam: () -> Unit,
    onViewHistory: () -> Unit
) {
    BackHandler { onGoToHome() }

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
            Text("ফলাফল লোড হচ্ছে...", color = TextSecondary, fontSize = 16.sp)
        }
        return
    }

    val userAnswers = remember(exam.userAnswersJson) {
        ExamRepository.decodeMap(exam.userAnswersJson)
    }
    val answerKeys = remember(exam.answerKeysJson) {
        ExamRepository.decodeMap(exam.answerKeysJson)
    }

    val accuracy = if (exam.correctCount + exam.wrongCount > 0) {
        ((exam.correctCount.toFloat() / (exam.correctCount + exam.wrongCount)) * 100).toInt()
    } else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 20.dp)
            .testTag("result_screen_container")
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Main Status Banner (Pass / Fail)
            val isPassed = exam.isPassed
            val bannerColor = if (isPassed) EmeraldGreen else CoralRed

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("result_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.5.dp, bannerColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    if (isPassed) Color(0xFF003017) else Color(0xFF380808),
                                    DarkSurface
                                )
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(bannerColor.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = bannerColor,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isPassed) "অভিনন্দন! আপনি পাস করেছেন 🎉" else "দুঃখিত! পাস মার্ক অর্জন হয়নি ⚠️",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = exam.title,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Final Score
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AmoledBlack,
                            border = BorderStroke(1.dp, bannerColor.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.2f", exam.finalScore),
                                    color = bannerColor,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = " / ${exam.totalQuestions}",
                                    color = TextSecondary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 3.dp, start = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val passReq = exam.totalQuestions * (exam.passPercentage / 100f)
                        Text(
                            text = "পাস মার্ক লক্ষ্যমাত্রা: ${passReq.toInt()} (${exam.passPercentage.toInt()}%)",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Score Metrics Grid
            Text(
                text = "বিস্তারিত মূল্যায়ন ফলাফল",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 4 Stats in 2x2 grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "সঠিক উত্তর",
                    value = "${exam.correctCount} টি",
                    sub = "+${exam.correctCount}.00 মার্ক",
                    color = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "ভুল উত্তর",
                    value = "${exam.wrongCount} টি",
                    sub = "-${String.format(Locale.US, "%.2f", exam.negativeMarksDeducted)} কাটা",
                    color = CoralRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "অনুত্তরীত (Skipped)",
                    value = "${exam.skippedCount} টি",
                    sub = "কোনো মার্ক কাটেনি",
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "নির্ভুলতা (Accuracy)",
                    value = "$accuracy%",
                    sub = "চেষ্টা করা প্রশ্নের ওপর",
                    color = CyanNeon,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onStartNewExam,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("result_new_exam_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        contentColor = AmoledBlack
                    )
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নতুন পরীক্ষা", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                OutlinedButton(
                    onClick = onGoToHome,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("result_home_btn"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, DarkSurfaceBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("হোম", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Review Sheet Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ListAlt, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "সম্পূর্ণ OMR উত্তরপত্র পর্যালোচনা",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Review Question Cards
        items(exam.totalQuestions) { index ->
            val qNum = index + 1
            val userAns = userAnswers[qNum]
            val key = answerKeys[qNum]

            val isSkipped = userAns == null
            val isCorrect = !isSkipped && key != null && userAns == key
            val isWrong = !isSkipped && key != null && userAns != key

            ReviewItemCard(
                questionNum = qNum,
                userAnswer = userAns,
                correctKey = key,
                isCorrect = isCorrect,
                isWrong = isWrong,
                isSkipped = isSkipped
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = sub, color = TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ReviewItemCard(
    questionNum: Int,
    userAnswer: Int?,
    correctKey: Int?,
    isCorrect: Boolean,
    isWrong: Boolean,
    isSkipped: Boolean
) {
    val borderColor = when {
        isCorrect -> EmeraldGreen.copy(alpha = 0.4f)
        isWrong -> CoralRed.copy(alpha = 0.4f)
        else -> DarkSurfaceBorder
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Serial
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(DarkSurfaceElevated, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$questionNum", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Answers summary
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "আপনার উত্তর: ", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = if (userAnswer != null) BengaliOptions.getOrElse(userAnswer) { "-" } else "অনুত্তরীত",
                            color = when {
                                isCorrect -> EmeraldGreen
                                isWrong -> CoralRed
                                else -> TextMuted
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (correctKey != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "সঠিক উত্তর: ", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = BengaliOptions.getOrElse(correctKey) { "-" },
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Status Badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = when {
                    isCorrect -> EmeraldGreen.copy(alpha = 0.15f)
                    isWrong -> CoralRed.copy(alpha = 0.15f)
                    else -> DarkSurfaceElevated
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when {
                            isCorrect -> "+1.0"
                            isWrong -> "ভুল"
                            else -> "বাদ"
                        },
                        color = when {
                            isCorrect -> EmeraldGreen
                            isWrong -> CoralRed
                            else -> TextMuted
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
