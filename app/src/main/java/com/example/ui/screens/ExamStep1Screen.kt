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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Timer
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
import com.example.ui.theme.CoralRed
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
import java.util.Locale

private val BengaliOptions = listOf("ক", "খ", "গ", "ঘ")

@Composable
fun ExamStep1Screen(
    examState: ActiveExamUiState,
    onSelectAnswer: (questionNum: Int, optionIndex: Int) -> Unit,
    onSubmitExam: () -> Unit,
    onExitToHome: () -> Unit
) {
    var showSubmitConfirmDialog by remember { mutableStateOf(false) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitConfirmDialog = true
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val total = examState.totalQuestions
    val answeredCount = examState.userAnswers.size
    val remainingCount = total - answeredCount

    // Format timer
    val hours = examState.remainingSeconds / 3600
    val minutes = (examState.remainingSeconds % 3600) / 60
    val seconds = examState.remainingSeconds % 60
    val timerString = if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    val isTimeWarning = examState.remainingSeconds < 300 // < 5 mins

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .testTag("exam_step1_container")
    ) {
        // Sticky Header with Live Timer, Pending counter, and Submit button
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
                            .testTag("step1_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Live Countdown Timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = if (isTimeWarning) CoralRed.copy(alpha = 0.15f) else DarkSurfaceElevated,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("countdown_timer_badge")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = if (isTimeWarning) CoralRed else CyanNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = timerString,
                            color = if (isTimeWarning) CoralRed else CyanNeon,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Submit Exam Button
                    Button(
                        onClick = { showSubmitConfirmDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldGreen,
                            contentColor = AmoledBlack
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("submit_exam_phase1_btn")
                    ) {
                        Text(
                            text = "জমা দিন",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats Bar: Answered vs Pending Questions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "উত্তর: $answeredCount / $total",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "বাকি আছে: $remainingCount টি",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar
                val progress = if (total > 0) answeredCount.toFloat() / total else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = CyanNeon,
                    trackColor = DarkSurfaceBorder
                )
            }
        }

        // Quick Jump Pill Bar
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

        // Notice Badge for One-Touch Rule
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF141414))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ওয়ান-টাচ নিয়ম: একবার অপশন চাপলে তা আর পরিবর্তন করা যাবে না।",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        // Question OMR List
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
                val selectedOption = examState.userAnswers[qNumber]
                val isAnswered = selectedOption != null

                OmrQuestionCard(
                    questionNumber = qNumber,
                    selectedOption = selectedOption,
                    isLocked = isAnswered,
                    onOptionClick = { optIndex ->
                        onSelectAnswer(qNumber, optIndex)
                    }
                )
            }
        }
    }

    // Submit Exam Confirmation Dialog
    if (showSubmitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmDialog = false },
            title = {
                Text(
                    text = "পরীক্ষা জমা দিতে চান?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "আপনি $answeredCount টি প্রশ্নের উত্তর দিয়েছেন। $remainingCount টি প্রশ্ন এখনো বাকি আছে।",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "জমা দিলে ১ম ধাপ শেষ হয়ে ২য় ধাপে (উত্তরমালা মূল্যায়ন) চলে যাবে।",
                        color = CyanNeon,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmDialog = false
                        onSubmitExam()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        contentColor = AmoledBlack
                    )
                ) {
                    Text("হ্যাঁ, জমা দিন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmDialog = false }) {
                    Text("বাতিল", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Exit Exam Confirmation Dialog
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
                    text = "আপনার উত্তর ও সময় সেভ থাকবে। আপনি যেকোনো সময় হোম স্ক্রিন থেকে পরীক্ষাটি পুনরায় শুরু করতে পারবেন।",
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
                    Text("পরীক্ষায় থাকুন", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun OmrQuestionCard(
    questionNumber: Int,
    selectedOption: Int?,
    isLocked: Boolean,
    onOptionClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("omr_question_$questionNumber"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(
            1.dp,
            if (isLocked) CyanNeon.copy(alpha = 0.5f) else DarkSurfaceBorderHighlight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Question Serial Number
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            if (isLocked) CyanNeon.copy(alpha = 0.15f) else DarkSurfaceElevated,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$questionNumber",
                        color = if (isLocked) CyanNeon else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isLocked) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = CyanNeon,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // OMR Bubbles: ক, খ, গ, ঘ
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BengaliOptions.forEachIndexed { optIndex, label ->
                    val isThisSelected = selectedOption == optIndex
                    OmrBubble(
                        label = label,
                        isSelected = isThisSelected,
                        isLocked = isLocked,
                        onClick = {
                            onOptionClick(optIndex)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OmrBubble(
    label: String,
    isSelected: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) CyanNeon else BubbleDefaultBg,
        label = "bubbleBg"
    )
    val textColor = if (isSelected) AmoledBlack else TextPrimary
    val borderColor = if (isSelected) CyanNeon else BubbleDefaultBorder

    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onClick() }
            .then(
                if (!isSelected) Modifier.background(BubbleDefaultBg) else Modifier
            ),
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
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                )
            }
        }
    }
}
