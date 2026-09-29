package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.SetupConfig

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExamSetupScreen(
    config: SetupConfig,
    onTitleChange: (String) -> Unit,
    onQuestionsChange: (Int) -> Unit,
    onDurationChange: (Int) -> Unit,
    onNegativeRateChange: (Float) -> Unit,
    onPassPercentageChange: (Float) -> Unit,
    onStartExam: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val scrollState = rememberScrollState()

    var questionsInputText by remember(config.totalQuestions) {
        mutableStateOf(config.totalQuestions.toString())
    }

    var hoursPart by remember(config.durationMinutes) {
        mutableStateOf(config.durationMinutes / 60)
    }
    var minutesPart by remember(config.durationMinutes) {
        mutableStateOf(config.durationMinutes % 60)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
            .testTag("setup_screen_container")
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .background(DarkSurfaceElevated, CircleShape)
                    .testTag("setup_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "পরীক্ষা কনফিগারেশন",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "আপনার পছন্দমতো সময় ও মার্কিং সেট করুন",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Exam Title
        Text(
            text = "পরীক্ষার নাম বা বিষয়",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = config.title,
            onValueChange = onTitleChange,
            placeholder = { Text("যেমন: ৪৫তম বিসিএস প্রিলিমিনারি", color = TextMuted) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("exam_title_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceElevated,
                unfocusedContainerColor = DarkSurfaceElevated,
                focusedBorderColor = CyanNeon,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(22.dp))

        // 1. Total Questions Configuration
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "১. মোট প্রশ্ন সংখ্যা",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${config.totalQuestions} টি",
                        color = CyanNeon,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Presets
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(25, 50, 80, 100, 200).forEach { count ->
                        val isSelected = config.totalQuestions == count
                        PresetChip(
                            text = "$count টি",
                            isSelected = isSelected,
                            onClick = {
                                onQuestionsChange(count)
                                questionsInputText = count.toString()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom input
                OutlinedTextField(
                    value = questionsInputText,
                    onValueChange = { newVal ->
                        val filtered = newVal.filter { it.isDigit() }.take(3)
                        questionsInputText = filtered
                        val parsed = filtered.toIntOrNull()
                        if (parsed != null && parsed in 1..500) {
                            onQuestionsChange(parsed)
                        }
                    },
                    label = { Text("কাস্টম সংখ্যা লিখুন", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_questions_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AmoledBlack,
                        unfocusedContainerColor = AmoledBlack,
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Exam Duration (Timer)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "২. পরীক্ষার সময় (টাইমার)",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val h = config.durationMinutes / 60
                    val m = config.durationMinutes % 60
                    val durationText = buildString {
                        if (h > 0) append("$h ঘণ্টা ")
                        if (m > 0 || h == 0) append("$m মিনিট")
                    }
                    Text(
                        text = durationText,
                        color = GoldAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Presets
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        30 to "৩০ মিনিট",
                        45 to "৪৫ মিনিট",
                        60 to "১ ঘণ্টা",
                        90 to "১.৫ ঘণ্টা",
                        120 to "২ ঘণ্টা"
                    ).forEach { (mins, label) ->
                        val isSelected = config.durationMinutes == mins
                        PresetChip(
                            text = label,
                            isSelected = isSelected,
                            onClick = {
                                onDurationChange(mins)
                                hoursPart = mins / 60
                                minutesPart = mins % 60
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hour & Minute Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Hours Picker Box
                    TimeComponentPicker(
                        label = "ঘণ্টা",
                        value = hoursPart,
                        range = 0..5,
                        onValueChange = { newH ->
                            hoursPart = newH
                            val total = (newH * 60) + minutesPart
                            onDurationChange(total.coerceAtLeast(1))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Minutes Picker Box
                    TimeComponentPicker(
                        label = "মিনিট",
                        value = minutesPart,
                        range = 0..59,
                        onValueChange = { newM ->
                            minutesPart = newM
                            val total = (hoursPart * 60) + newM
                            onDurationChange(total.coerceAtLeast(1))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Negative Marking
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "৩. নেগেটিভ মার্কিং",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "-${config.negativeMarkRate}",
                        color = Color(0xFFFF5252),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "প্রতি ভুল উত্তরের জন্য কত নাম্বার কাটা যাবে",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        0.00f to "০.০০",
                        0.25f to "০.২৫",
                        0.50f to "০.৫০",
                        1.00f to "১.০০"
                    ).forEach { (rate, label) ->
                        val isSelected = config.negativeMarkRate == rate
                        PresetChip(
                            text = label,
                            isSelected = isSelected,
                            onClick = { onNegativeRateChange(rate) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Pass Mark Percentage
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "৪. পাস মার্ক শতাংশ",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${config.passPercentage.toInt()}%",
                        color = EmeraldGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val requiredScore = (config.totalQuestions * (config.passPercentage / 100f))
                Text(
                    text = "পাস করতে কমপক্ষে $requiredScore নাম্বার পেতে হবে",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = config.passPercentage,
                    onValueChange = { onPassPercentageChange(it) },
                    valueRange = 30f..100f,
                    steps = 13,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldGreen,
                        activeTrackColor = EmeraldGreen,
                        inactiveTrackColor = DarkSurfaceBorder
                    ),
                    modifier = Modifier.testTag("pass_percentage_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(40f, 50f, 60f, 80f).forEach { pct ->
                        val isSelected = config.passPercentage.toInt() == pct.toInt()
                        PresetChip(
                            text = "${pct.toInt()}%",
                            isSelected = isSelected,
                            onClick = { onPassPercentageChange(pct) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Big Start Exam Button
        Button(
            onClick = onStartExam,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("start_exam_submit_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanNeon,
                contentColor = Color.Black
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "পরীক্ষা শুরু করুন (১ম ধাপ)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun PresetChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) CyanNeon else AmoledBlack,
        border = BorderStroke(1.dp, if (isSelected) CyanNeon else DarkSurfaceBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.Black else TextPrimary,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TimeComponentPicker(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = AmoledBlack,
        border = BorderStroke(1.dp, DarkSurfaceBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 13.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        val prev = if (value > range.first) value - 1 else range.last
                        onValueChange(prev)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("-", color = CyanNeon, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "$value",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                IconButton(
                    onClick = {
                        val next = if (value < range.last) value + 1 else range.first
                        onValueChange(next)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("+", color = CyanNeon, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
