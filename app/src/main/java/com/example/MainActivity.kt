package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.ExamReviewDetailScreen
import com.example.ui.screens.ExamSetupScreen
import com.example.ui.screens.ExamStep1Screen
import com.example.ui.screens.ExamStep2Screen
import com.example.ui.screens.HistoryAnalyticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppRoot()
            }
        }
    }
}

@Composable
fun MainAppRoot(viewModel: ExamViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val savedActiveExam by viewModel.savedActiveExam.collectAsStateWithLifecycle()
    val historyList by viewModel.allHistory.collectAsStateWithLifecycle()
    val examState by viewModel.examState.collectAsStateWithLifecycle()
    val setupConfig by viewModel.setupConfig.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        containerColor = AmoledBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    is Screen.Splash -> {
                        SplashScreen(
                            onAnimationFinish = {
                                viewModel.navigateToHome()
                            }
                        )
                    }

                    is Screen.Home -> {
                        HomeScreen(
                            savedActiveExam = savedActiveExam,
                            historyList = historyList,
                            onResumeActiveExam = {
                                viewModel.resumeActiveExam()
                            },
                            onDiscardActiveExam = {
                                viewModel.discardActiveExam()
                            },
                            onStartExamSetup = {
                                viewModel.navigateTo(Screen.Setup)
                            },
                            onNavigateToHistory = {
                                viewModel.navigateTo(Screen.HistoryAnalytics(0))
                            },
                            onNavigateToAnalytics = {
                                viewModel.navigateTo(Screen.HistoryAnalytics(1))
                            },
                            onNavigateToAbout = {
                                viewModel.navigateTo(Screen.About)
                            }
                        )
                    }

                    is Screen.Setup -> {
                        ExamSetupScreen(
                            config = setupConfig,
                            onTitleChange = { viewModel.updateSetupTitle(it) },
                            onQuestionsChange = { viewModel.updateSetupQuestions(it) },
                            onDurationChange = { viewModel.updateSetupDuration(it) },
                            onNegativeRateChange = { viewModel.updateSetupNegativeRate(it) },
                            onPassPercentageChange = { viewModel.updateSetupPassPercentage(it) },
                            onStartExam = {
                                viewModel.startNewExam()
                            },
                            onBack = {
                                viewModel.popBack()
                            }
                        )
                    }

                    is Screen.Step1Exam -> {
                        ExamStep1Screen(
                            examState = examState,
                            onSelectAnswer = { qNum, optIndex ->
                                viewModel.selectUserAnswer(qNum, optIndex)
                            },
                            onSubmitExam = {
                                viewModel.advanceToStep2()
                            },
                            onExitToHome = {
                                viewModel.navigateToHome()
                            }
                        )
                    }

                    is Screen.Step2AnswerKey -> {
                        ExamStep2Screen(
                            examState = examState,
                            onSelectAnswerKey = { qNum, optIndex ->
                                viewModel.selectAnswerKey(qNum, optIndex)
                            },
                            onSubmitFinalResult = {
                                viewModel.submitFinalResult()
                            },
                            onExitToHome = {
                                viewModel.navigateToHome()
                            }
                        )
                    }

                    is Screen.Result -> {
                        ResultScreen(
                            historyId = screen.historyId,
                            getHistoryEntity = { id ->
                                viewModel.getHistoryItem(id)
                            },
                            onGoToHome = {
                                viewModel.navigateToHome()
                            },
                            onStartNewExam = {
                                viewModel.navigateTo(Screen.Setup)
                            },
                            onViewHistory = {
                                viewModel.navigateTo(Screen.HistoryAnalytics(0))
                            }
                        )
                    }

                    is Screen.HistoryAnalytics -> {
                        HistoryAnalyticsScreen(
                            initialTab = screen.initialTab,
                            historyList = historyList,
                            onSelectExamDetail = { id ->
                                viewModel.navigateTo(Screen.ExamReviewDetail(id))
                            },
                            onDeleteExam = { id ->
                                viewModel.deleteHistoryItem(id)
                            },
                            onClearAll = {
                                viewModel.clearAllHistory()
                            },
                            onBack = {
                                viewModel.popBack()
                            }
                        )
                    }

                    is Screen.ExamReviewDetail -> {
                        ExamReviewDetailScreen(
                            historyId = screen.historyId,
                            getHistoryEntity = { id ->
                                viewModel.getHistoryItem(id)
                            },
                            onBack = {
                                viewModel.popBack()
                            }
                        )
                    }

                    is Screen.About -> {
                        AboutScreen(
                            onBack = {
                                viewModel.popBack()
                            }
                        )
                    }
                }
            }
        }
    }
}
