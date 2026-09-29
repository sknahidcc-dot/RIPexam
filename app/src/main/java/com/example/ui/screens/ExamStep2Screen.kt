package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.BubbleDefaultBg
import com.example.ui.theme.BubbleDefaultBorder
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceBorderHighlight
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ActiveExamUiState
import kotlinx.coroutines.launch

private val BengaliOptions = listOf("ক", "খ", "গ", "ঘ")

@Composable
fun ExamStep2Screen(
    examState: ActiveExamUiState,
    onSelectAnswerKey: (questionNum: Int, optionIndex: Int) -> Unit,
    onSubmitFinalResult: () -> Unit,
    onExitToHome: () -> Unit
) {
    var showFinalConfirmDialog by remember { mutableStateOf(false) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitConfirmDialog = true
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val total = examState.totalQuestions
    val keysFilledCount = examState.answerKeys.size
    val remainingKeysCount = total - keysFilledCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .testTag("exam_step2_container")
    ) {
        // Sticky Header: Step 2 Header & Final Result CTA
        Surface(
            color = DarkSurface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showExitConfirmDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(DarkSurfaceElevated, CircleShape)
                            .testTag("step2_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ধাপ ২: উত্তরমালা মেলাব",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "সঠিক উত্তরপত্র ইনপুট দিন",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // Final Result CTA
                    Button(
                        onClick = { showFinalConfirmDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldGreen,
                            contentColor = AmoledBlack
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("final_result_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ফলাফল",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats Bar: Keys filled vs Remaining
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "উত্তরমালা ইনপুট: $keysFilledCount / $total",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (remainingKeysCount == 0) EmeraldGreen.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (remainingKeysCount == 0) "সব মেলানো সম্পন্ন!" else "উত্তরমালা বাকি: $remainingKeysCount টি",
                            color = if (remainingKeysCount == 0) EmeraldGreen else GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                val progress = if (total > 0) keysFilledCount.toFloat() / total else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = EmeraldGreen,
                    trackColor = DarkSurfaceBorder
                )
            }
        }

        // Quick Jump Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceElevated)
                .padding(vertical = 6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val stepSize = when {
                total <= 30 -> 5
                total <= 100 -> 10
                else -> 25
            }

            val checkpoints = (1..total step stepSize).toList()
            items(checkpoints) { qIndex ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AmoledBlack,
                    border = BorderStroke(1.dp, DarkSurfaceBorder),
                    modifier = Modifier.clickable {
                        scope.launch {
                            listState.animateScrollToItem(qIndex - 1)
                        }
                    }
                ) {
                    Text(
                        text = "প্রশ্ন $qIndex",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Informational Hint
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF141414))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = EmeraldGreen,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "বামপাশে আপনার উত্তর ও ডানপাশে সঠিক উত্তরমালা ট্যাপ করে সিলেক্ট করুন (পরিবর্তনযোগ্য)।",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        // Comparison List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(total) { index ->
                val qNumber = index + 1
                val candidateAns = examState.userAnswers[qNumber]
                val correctKey = examState.answerKeys[qNumber]

                Step2QuestionRow(
                    questionNumber = qNumber,
                    candidateAnswer = candidateAns,
                    correctAnswerKey = correctKey,
                    onSelectKey = { optIndex ->
                        onSelectAnswerKey(qNumber, optIndex)
                    }
                )
            }
        }
    }

    // Final Result Confirmation Dialog
    if (showFinalConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showFinalConfirmDialog = false },
            title = {
                Text(
                    text = "চূড়ান্ত ফলাফল দেখতে চান?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "আপনি $keysFilledCount টি প্রশ্নের উত্তরমালা ইনপুট দিয়েছেন।",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    if (remainingKeysCount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "সতর্কতা: এখনো $remainingKeysCount টি প্রশ্নের উত্তরমালা বাকি আছে!",
                            color = GoldAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ফলাফল সাবমিট করলে সঠিক, ভুল ও মাইনাস মার্কিংসহ বিস্তারিত রেজাল্ট সংরক্ষিত হবে।",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinalConfirmDialog = false
                        onSubmitFinalResult()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        contentColor = AmoledBlack
                    )
                ) {
                    Text("হ্যাঁ, ফলাফল দেখুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinalConfirmDialog = false }) {
                    Text("ফিরে যান", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Exit to Home Confirmation Dialog
    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            title = {
                Text(
                    text = "হোমে ফিরে যেতে চান?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "আপনার দেওয়া উত্তরমালা সেভ থাকবে। আপনি যেকোনো সময় ফিরে এসে এটি সম্পন্ন করতে পারবেন।",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirmDialog = false
                        onExitToHome()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        contentColor = AmoledBlack
                    )
                ) {
                    Text("হোমে যান", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmDialog = false }) {
                    Text("এখানে থাকুন", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun Step2QuestionRow(
    questionNumber: Int,
    candidateAnswer: Int?,
    correctAnswerKey: Int?,
    onSelectKey: (Int) -> Unit
) {
    val isEvaluated = correctAnswerKey != null
    val isCandidateAnswered = candidateAnswer != null
    val isMatch = isEvaluated && isCandidateAnswered && candidateAnswer == correctAnswerKey

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("step2_row_$questionNumber"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(
            1.dp,
            when {
                !isEvaluated -> DarkSurfaceBorderHighlight
                isMatch -> EmeraldGreen.copy(alpha = 0.5f)
                else -> Color(0xFFFF5252).copy(alpha = 0.5f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top mini row: Question number & User answer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(DarkSurfaceElevated, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$questionNumber",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "আপনার উত্তর: ",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    if (candidateAnswer != null) {
                        Surface(
                            shape = CircleShape,
                            color = CyanNeon.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, CyanNeon),
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = BengaliOptions.getOrElse(candidateAnswer) { "-" },
                                    color = CyanNeon,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "অনুত্তরীত (Skipped)",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Status indicator if matched
                if (isEvaluated) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isMatch) EmeraldGreen.copy(alpha = 0.15f) else Color(0xFFFF5252).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isMatch) "সঠিক" else "ভুল",
                            color = if (isMatch) EmeraldGreen else Color(0xFFFF5252),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row: Correct Answer Key Selector (ক, খ, গ, ঘ)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "সঠিক উত্তরমালা:",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BengaliOptions.forEachIndexed { optIndex, label ->
                        val isSelected = correctAnswerKey == optIndex
                        KeyOptionBubble(
                            label = label,
                            isSelected = isSelected,
                            onClick = { onSelectKey(optIndex) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyOptionBubble(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) EmeraldGreen else BubbleDefaultBg,
        label = "keyBubbleBg"
    )
    val textColor = if (isSelected) AmoledBlack else TextPrimary
    val borderColor = if (isSelected) EmeraldGreen else BubbleDefaultBorder

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = bgColor,
            border = BorderStroke(1.5.dp, borderColor),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                )
            }
        }
    }
}
