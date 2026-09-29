import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'models/exam_model.dart';
import 'screens/splash_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final prefs = await SharedPreferences.getInstance();
  runApp(
    ChangeNotifierProvider(
      create: (_) => ExamProvider(prefs),
      child: const RipExamApp(),
    ),
  );
}

class ExamProvider extends ChangeNotifier {
  final SharedPreferences prefs;

  // Active Exam state (Crash / Call resilience)
  bool _hasActiveExam = false;
  String _activeTitle = 'মডেল টেস্ট';
  int _activeTotalQuestions = 100;
  int _activeDurationMinutes = 60;
  int _activeRemainingSeconds = 3600;
  double _activeNegativeRate = 0.50;
  double _activePassPercentage = 80.0;
  int _activeStep = 1; // 1 = Exam, 2 = Answer Key
  Map<int, int> _userAnswers = {};
  Map<int, int> _answerKeys = {};
  Timer? _timer;

  List<ExamHistoryModel> _historyList = [];

  ExamProvider(this.prefs) {
    _loadState();
  }

  bool get hasActiveExam => _hasActiveExam;
  String get activeTitle => _activeTitle;
  int get activeTotalQuestions => _activeTotalQuestions;
  int get activeDurationMinutes => _activeDurationMinutes;
  int get activeRemainingSeconds => _activeRemainingSeconds;
  double get activeNegativeRate => _activeNegativeRate;
  double get activePassPercentage => _activePassPercentage;
  int get activeStep => _activeStep;
  Map<int, int> get userAnswers => _userAnswers;
  Map<int, int> get answerKeys => _answerKeys;
  List<ExamHistoryModel> get historyList => _historyList;

  void _loadState() {
    // Load history
    final historyJson = prefs.getStringList('exam_history_list') ?? [];
    _historyList = historyJson.map((e) => ExamHistoryModel.fromJson(e)).toList();

    // Check active exam
    _hasActiveExam = prefs.getBool('has_active_exam') ?? false;
    if (_hasActiveExam) {
      _activeTitle = prefs.getString('active_title') ?? 'মডেল টেস্ট';
      _activeTotalQuestions = prefs.getInt('active_total_questions') ?? 100;
      _activeDurationMinutes = prefs.getInt('active_duration_minutes') ?? 60;
      _activeRemainingSeconds = prefs.getInt('active_remaining_seconds') ?? 3600;
      _activeNegativeRate = prefs.getDouble('active_negative_rate') ?? 0.50;
      _activePassPercentage = prefs.getDouble('active_pass_percentage') ?? 80.0;
      _activeStep = prefs.getInt('active_step') ?? 1;

      final uStr = prefs.getString('active_user_answers') ?? '{}';
      final Map<String, dynamic> uMap = json.decode(uStr);
      _userAnswers = uMap.map((k, v) => MapEntry(int.parse(k), v as int));

      final kStr = prefs.getString('active_answer_keys') ?? '{}';
      final Map<String, dynamic> kMap = json.decode(kStr);
      _answerKeys = kMap.map((k, v) => MapEntry(int.parse(k), v as int));
    }
    notifyListeners();
  }

  void _saveActiveExam() {
    prefs.setBool('has_active_exam', _hasActiveExam);
    if (_hasActiveExam) {
      prefs.setString('active_title', _activeTitle);
      prefs.setInt('active_total_questions', _activeTotalQuestions);
      prefs.setInt('active_duration_minutes', _activeDurationMinutes);
      prefs.setInt('active_remaining_seconds', _activeRemainingSeconds);
      prefs.setDouble('active_negative_rate', _activeNegativeRate);
      prefs.setDouble('active_pass_percentage', _activePassPercentage);
      prefs.setInt('active_step', _activeStep);

      final uMap = _userAnswers.map((k, v) => MapEntry(k.toString(), v));
      prefs.setString('active_user_answers', json.encode(uMap));

      final kMap = _answerKeys.map((k, v) => MapEntry(k.toString(), v));
      prefs.setString('active_answer_keys', json.encode(kMap));
    }
  }

  void startNewExam({
    required String title,
    required int totalQuestions,
    required int durationMinutes,
    required double negativeRate,
    required double passPercentage,
  }) {
    _hasActiveExam = true;
    _activeTitle = title.trim().isEmpty ? 'মডেল টেস্ট' : title;
    _activeTotalQuestions = totalQuestions;
    _activeDurationMinutes = durationMinutes;
    _activeRemainingSeconds = durationMinutes * 60;
    _activeNegativeRate = negativeRate;
    _activePassPercentage = passPercentage;
    _activeStep = 1;
    _userAnswers = {};
    _answerKeys = {};

    _saveActiveExam();
    _startTimer();
    notifyListeners();
  }

  void _startTimer() {
    _timer?.cancel();
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (_activeRemainingSeconds > 0) {
        _activeRemainingSeconds--;
        if (_activeRemainingSeconds % 5 == 0) {
          _saveActiveExam();
        }
        notifyListeners();
      } else {
        advanceToStep2();
      }
    });
  }

  void resumeActiveExam() {
    if (_activeStep == 1 && _activeRemainingSeconds > 0) {
      _startTimer();
    }
    notifyListeners();
  }

  void selectUserAnswer(int questionNum, int optionIndex) {
    if (_activeStep != 1) return;
    // Strict one-touch rule: if already answered, cannot change!
    if (_userAnswers.containsKey(questionNum)) return;

    _userAnswers[questionNum] = optionIndex;
    _saveActiveExam();
    notifyListeners();
  }

  void advanceToStep2() {
    _timer?.cancel();
    _activeStep = 2;
    _saveActiveExam();
    notifyListeners();
  }

  void selectAnswerKey(int questionNum, int optionIndex) {
    if (_activeStep != 2) return;
    _answerKeys[questionNum] = optionIndex;
    _saveActiveExam();
    notifyListeners();
  }

  ExamHistoryModel submitFinalResult() {
    _timer?.cancel();
    int correct = 0;
    int wrong = 0;
    int skipped = 0;

    for (int q = 1; q <= _activeTotalQuestions; q++) {
      final u = _userAnswers[q];
      final k = _answerKeys[q];
      if (u == null) {
        skipped++;
      } else if (k != null) {
        if (u == k) {
          correct++;
        } else {
          wrong++;
        }
      } else {
        skipped++;
      }
    }

    final double negativeDeducted = wrong * _activeNegativeRate;
    final double finalScore = (correct * 1.0) - negativeDeducted;
    final bool isPassed = finalScore >= (_activeTotalQuestions * (_activePassPercentage / 100.0));
    final int timeSpent = (_activeDurationMinutes * 60) - _activeRemainingSeconds;

    final result = ExamHistoryModel(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      title: _activeTitle,
      totalQuestions: _activeTotalQuestions,
      durationMinutes: _activeDurationMinutes,
      timeSpentSeconds: timeSpent < 0 ? 0 : timeSpent,
      negativeMarkRate: _activeNegativeRate,
      passPercentage: _activePassPercentage,
      correctCount: correct,
      wrongCount: wrong,
      skippedCount: skipped,
      negativeMarksDeducted: negativeDeducted,
      finalScore: finalScore,
      isPassed: isPassed,
      userAnswers: Map.from(_userAnswers),
      answerKeys: Map.from(_answerKeys),
      timestamp: DateTime.now(),
    );

    _historyList.insert(0, result);
    final historyJson = _historyList.map((e) => e.toJson()).toList();
    prefs.setStringList('exam_history_list', historyJson);

    // Clear active exam
    discardActiveExam();

    return result;
  }

  void discardActiveExam() {
    _timer?.cancel();
    _hasActiveExam = false;
    _userAnswers.clear();
    _answerKeys.clear();
    prefs.remove('has_active_exam');
    prefs.remove('active_user_answers');
    prefs.remove('active_answer_keys');
    notifyListeners();
  }

  void deleteHistory(String id) {
    _historyList.removeWhere((e) => e.id == id);
    final historyJson = _historyList.map((e) => e.toJson()).toList();
    prefs.setStringList('exam_history_list', historyJson);
    notifyListeners();
  }

  void clearAllHistory() {
    _historyList.clear();
    prefs.remove('exam_history_list');
    notifyListeners();
  }
}

class RipExamApp extends StatelessWidget {
  const RipExamApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'RIP-Exam',
      debugShowCheckedModeBanner: false,
      theme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: const Color(0xFF000000), // AMOLED Pure Black
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFF00E5FF),
          secondary: Color(0xFFFFD54F),
          surface: Color(0xFF0C0C0C),
          surfaceContainer: Color(0xFF161616),
          error: Color(0xFFFF5252),
        ),
        appBarTheme: const AppBarTheme(
          backgroundColor: Color(0xFF000000),
          elevation: 0,
        ),
      ),
      home: const SplashScreen(),
    );
  }
}
