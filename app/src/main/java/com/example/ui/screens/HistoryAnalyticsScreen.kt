package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamHistoryEntity
import com.example.ui.theme.AmoledBlack
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryAnalyticsScreen(
    initialTab: Int = 0,
    historyList: List<ExamHistoryEntity>,
    onSelectExamDetail: (Long) -> Unit,
    onDeleteExam: (Long) -> Unit,
    onClearAll: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedTabIndex by remember { mutableIntStateOf(initialTab) }
    var itemToDelete by remember { mutableStateOf<Long?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .testTag("history_analytics_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkSurfaceElevated, CircleShape)
                        .testTag("history_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "হিস্ট্রি ও এনালাইসিস",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (selectedTabIndex == 0 && historyList.isNotEmpty()) {
                IconButton(
                    onClick = { showClearAllDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkSurfaceElevated, CircleShape)
                        .testTag("clear_history_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear All",
                        tint = CoralRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Custom M3 Tab Row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = AmoledBlack,
            contentColor = CyanNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = CyanNeon,
                    height = 3.dp
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DarkSurfaceBorder)
                )
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedTabIndex == 0) CyanNeon else TextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "পরীক্ষার হিস্ট্রি (${historyList.size})",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 0) CyanNeon else TextSecondary
                        )
                    }
                }
            )

            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedTabIndex == 1) CyanNeon else TextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ডাটা এনালাইসিস",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 1) CyanNeon else TextSecondary
                        )
                    }
                }
            )
        }

        if (selectedTabIndex == 0) {
            // Tab 1: History List
            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "এখনো কোনো পরীক্ষার হিস্ট্রি নেই",
                            color = TextSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "নতুন পরীক্ষা দিয়ে নিজের প্রস্তুতি যাচাই করুন।",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historyList) { item ->
                        HistoryCard(
                            history = item,
                            onClick = { onSelectExamDetail(item.id) },
                            onDelete = { itemToDelete = item.id }
                        )
                    }
                }
            }
        } else {
            // Tab 2: Analytics & Trends
            AnalyticsTabContent(historyList)
        }
    }

    // Delete single item dialog
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("পরীক্ষার হিস্ট্রি মুছবেন?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("এই পরীক্ষার রেকর্ড ও উত্তরপত্র চিরতরে মুছে ফেলা হবে।", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let { onDeleteExam(it) }
                        itemToDelete = null
                    }
                ) {
                    Text("মুছে ফেলুন", color = CoralRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("বাতিল", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Clear all dialog
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("সব হিস্ট্রি মুছে ফেলতে চান?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("আপনার সংরক্ষিত সব পরীক্ষার ফলাফল ও ডাটা মুছে যাবে।", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearAllDialog = false
                        onClearAll()
                    }
                ) {
                    Text("সব মুছুন", color = CoralRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("বাতিল", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun HistoryCard(
    history: ExamHistoryEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(history.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(history.timestamp))
    }
    val badgeColor = if (history.isPassed) EmeraldGreen else CoralRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("history_item_${history.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = BorderStroke(1.dp, DarkSurfaceBorderHighlight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (history.isPassed) "পাস (PASSED)" else "ফেল (FAILED)",
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dateStr, color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = history.title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format(Locale.US, "%.2f", history.finalScore),
                        color = CyanNeon,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = " / ${history.totalQuestions}",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 2.dp, start = 3.dp)
                    )
                }

                Text(
                    text = "সঠিক: ${history.correctCount} • ভুল: ${history.wrongCount} • বাদ: ${history.skippedCount}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun AnalyticsTabContent(historyList: List<ExamHistoryEntity>) {
    if (historyList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "এনালাইসিসের জন্য পর্যাপ্ত পরীক্ষা নেই",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    val totalExams = historyList.size
    val totalPassed = historyList.count { it.isPassed }
    val passRate = ((totalPassed.toFloat() / totalExams) * 100).toInt()
    val avgScore = historyList.map { it.finalScore }.average()
    val highestScore = historyList.maxOf { it.finalScore }
    val totalCorrect = historyList.sumOf { it.correctCount }
    val totalWrong = historyList.sumOf { it.wrongCount }
    val totalSkipped = historyList.sumOf { it.skippedCount }
    val overallAccuracy = if (totalCorrect + totalWrong > 0) {
        ((totalCorrect.toFloat() / (totalCorrect + totalWrong)) * 100).toInt()
    } else 0

    // Reverse history to show trend chronologically (oldest -> newest)
    val chronologicalList = remember(historyList) { historyList.reversed() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Trend Chart Card
        item {
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
                            text = "স্কোর প্রোগ্রেস ট্রেন্ড",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyanNeon.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "বিগত পরীক্ষাগুলো",
                                color = CyanNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Canvas Line Chart
                    ScoreTrendChart(
                        scores = chronologicalList.map { it.finalScore },
                        maxScale = (chronologicalList.maxOfOrNull { it.totalQuestions } ?: 100).toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "বিন্দুগুলো প্রতিটি পরীক্ষার অর্জিত নম্বর নির্দেশ করছে",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Stats Matrix
        item {
            Text(
                text = "সার্বিক পারফর্মেন্স সূচক",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsStatBox(
                    label = "সর্বোচ্চ স্কোর",
                    value = String.format(Locale.US, "%.1f", highestScore),
                    color = GoldAccent,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatBox(
                    label = "গড় স্কোর",
                    value = String.format(Locale.US, "%.1f", avgScore),
                    color = CyanNeon,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatBox(
                    label = "পাস রেট",
                    value = "$passRate%",
                    color = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "প্রশ্নোত্তর নির্ভুলতা অনুপাত",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "মোট নির্ভুলতা (Accuracy):", color = TextSecondary, fontSize = 13.sp)
                        Text(text = "$overallAccuracy%", color = CyanNeon, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "সর্বমোট সঠিক উত্তর:", color = TextSecondary, fontSize = 13.sp)
                        Text(text = "$totalCorrect টি", color = EmeraldGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "সর্বমোট ভুল উত্তর:", color = TextSecondary, fontSize = 13.sp)
                        Text(text = "$totalWrong টি", color = CoralRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "সর্বমোট স্কিপ করা প্রশ্ন:", color = TextSecondary, fontSize = 13.sp)
                        Text(text = "$totalSkipped টি", color = TextMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Preparation Recommendation
        item {
            val recommendation = when {
                passRate >= 80 -> "আপনার প্রস্তুতি অসাধারণ! নিয়মিত রিভিশন বজায় রাখুন এবং গতি আরও বাড়ানোর চেষ্টা করুন।"
                passRate >= 60 -> "আপনার প্রস্তুতি সন্তোষজনক। তবে নেগেটিভ মার্কিং কমাতে ভুল উত্তরের সংখ্যা হ্রাস করুন।"
                else -> "প্রস্তুতিতে আরও জোর দিন। বেশি বেশি মডেল টেস্ট দিয়ে দুর্বল বিষয়গুলো চিহ্নিত করুন।"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "স্মার্ট অ্যানালাইসিস পর্যবেক্ষণ",
                            color = GoldAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = recommendation,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AnalyticsStatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ScoreTrendChart(
    scores: List<Float>,
    maxScale: Float,
    modifier: Modifier = Modifier
) {
    val safeMax = (if (maxScale > 0) maxScale else 100f).coerceAtLeast(10f)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val padding = 24f

        val effectiveW = w - (padding * 2)
        val effectiveH = h - (padding * 2)

        // Draw horizontal grid lines (0%, 25%, 50%, 75%, 100%)
        for (i in 0..4) {
            val y = padding + (effectiveH * (i / 4f))
            drawLine(
                color = Color(0xFF222222),
                start = Offset(padding, y),
                end = Offset(w - padding, y),
                strokeWidth = 1f
            )
        }

        if (scores.isEmpty()) return@Canvas

        val pointCount = scores.size
        val stepX = if (pointCount > 1) effectiveW / (pointCount - 1) else effectiveW / 2

        val points = scores.mapIndexed { index, score ->
            val normScore = (score / safeMax).coerceIn(0f, 1f)
            val x = if (pointCount > 1) padding + (index * stepX) else w / 2
            val y = padding + effectiveH - (normScore * effectiveH)
            Offset(x, y)
        }

        // Draw Trend Line Path
        if (points.size > 1) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val pPrev = points[i - 1]
                    val pCurr = points[i]
                    // smooth cubic bezier
                    val cx = (pPrev.x + pCurr.x) / 2
                    cubicTo(cx, pPrev.y, cx, pCurr.y, pCurr.x, pCurr.y)
                }
            }

            drawPath(
                path = path,
                color = CyanNeon,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Draw glowing point circles
        points.forEach { pt ->
            drawCircle(
                color = CyanNeon,
                radius = 5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = AmoledBlack,
                radius = 2.5.dp.toPx(),
                center = pt
            )
        }
    }
}
