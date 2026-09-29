import 'dart:convert';

class ExamHistoryModel {
  final String id;
  final String title;
  final int totalQuestions;
  final int durationMinutes;
  final int timeSpentSeconds;
  final double negativeMarkRate;
  final double passPercentage;
  final int correctCount;
  final int wrongCount;
  final int skippedCount;
  final double negativeMarksDeducted;
  final double finalScore;
  final bool isPassed;
  final Map<int, int> userAnswers;
  final Map<int, int> answerKeys;
  final DateTime timestamp;

  ExamHistoryModel({
    required this.id,
    required this.title,
    required this.totalQuestions,
    required this.durationMinutes,
    required this.timeSpentSeconds,
    required this.negativeMarkRate,
    required this.passPercentage,
    required this.correctCount,
    required this.wrongCount,
    required this.skippedCount,
    required this.negativeMarksDeducted,
    required this.finalScore,
    required this.isPassed,
    required this.userAnswers,
    required this.answerKeys,
    required this.timestamp,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'totalQuestions': totalQuestions,
      'durationMinutes': durationMinutes,
      'timeSpentSeconds': timeSpentSeconds,
      'negativeMarkRate': negativeMarkRate,
      'passPercentage': passPercentage,
      'correctCount': correctCount,
      'wrongCount': wrongCount,
      'skippedCount': skippedCount,
      'negativeMarksDeducted': negativeMarksDeducted,
      'finalScore': finalScore,
      'isPassed': isPassed,
      'userAnswers': userAnswers.map((k, v) => MapEntry(k.toString(), v)),
      'answerKeys': answerKeys.map((k, v) => MapEntry(k.toString(), v)),
      'timestamp': timestamp.toIso8601String(),
    };
  }

  factory ExamHistoryModel.fromMap(Map<String, dynamic> map) {
    Map<int, int> parseMap(dynamic source) {
      if (source == null) return {};
      final m = source as Map<String, dynamic>;
      return m.map((k, v) => MapEntry(int.parse(k), v as int));
    }

    return ExamHistoryModel(
      id: map['id'] ?? '',
      title: map['title'] ?? 'Exam',
      totalQuestions: map['totalQuestions'] ?? 100,
      durationMinutes: map['durationMinutes'] ?? 60,
      timeSpentSeconds: map['timeSpentSeconds'] ?? 0,
      negativeMarkRate: (map['negativeMarkRate'] as num?)?.toDouble() ?? 0.50,
      passPercentage: (map['passPercentage'] as num?)?.toDouble() ?? 80.0,
      correctCount: map['correctCount'] ?? 0,
      wrongCount: map['wrongCount'] ?? 0,
      skippedCount: map['skippedCount'] ?? 0,
      negativeMarksDeducted: (map['negativeMarksDeducted'] as num?)?.toDouble() ?? 0.0,
      finalScore: (map['finalScore'] as num?)?.toDouble() ?? 0.0,
      isPassed: map['isPassed'] ?? false,
      userAnswers: parseMap(map['userAnswers']),
      answerKeys: parseMap(map['answerKeys']),
      timestamp: DateTime.tryParse(map['timestamp'] ?? '') ?? DateTime.now(),
    );
  }

  String toJson() => json.encode(toMap());
  factory ExamHistoryModel.fromJson(String source) => ExamHistoryModel.fromMap(json.decode(source));
}
