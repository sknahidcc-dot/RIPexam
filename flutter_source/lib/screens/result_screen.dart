import 'package:flutter/material.dart';
import '../models/exam_model.dart';
import 'home_screen.dart';

class ResultScreen extends StatelessWidget {
  final ExamHistoryModel result;

  const ResultScreen({super.key, required this.result});

  static const List<String> bengaliOptions = ["ক", "খ", "গ", "ঘ"];

  @override
  Widget build(BuildContext context) {
    final isPassed = result.isPassed;
    final bannerColor = isPassed ? const Color(0xFF00E676) : const Color(0xFFFF5252);

    final int accuracy = (result.correctCount + result.wrongCount > 0)
        ? ((result.correctCount / (result.correctCount + result.wrongCount)) * 100).toInt()
        : 0;

    return WillPopScope(
      onWillPop: () async {
        Navigator.pushAndRemoveUntil(context, MaterialPageRoute(builder: (_) => const HomeScreen()), (r) => false);
        return false;
      },
      child: Scaffold(
        backgroundColor: const Color(0xFF000000),
        appBar: AppBar(
          backgroundColor: const Color(0xFF0C0C0C),
          title: const Text("পরীক্ষার চূড়ান্ত ফলাফল", style: TextStyle(fontWeight: FontWeight.bold)),
          leading: IconButton(
            icon: const Icon(Icons.home),
            onPressed: () {
              Navigator.pushAndRemoveUntil(context, MaterialPageRoute(builder: (_) => const HomeScreen()), (r) => false);
            },
          ),
        ),
        body: ListView(
          padding: const EdgeInsets.all(20),
          children: [
            // Status Card
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(20),
                gradient: LinearGradient(
                  colors: [
                    isPassed ? const Color(0xFF00381B) : const Color(0xFF380808),
                    const Color(0xFF0C0C0C),
                  ],
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                ),
                border: Border.all(color: bannerColor, width: 1.5),
              ),
              child: Column(
                children: [
                  Icon(
                    isPassed ? Icons.check_circle : Icons.cancel,
                    color: bannerColor,
                    size: 48,
                  ),
                  const SizedBox(height: 10),
                  Text(
                    isPassed ? "অভিনন্দন! আপনি পাস করেছেন 🎉" : "দুঃখিত! পাস মার্ক অর্জন হয়নি ⚠️",
                    style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 4),
                  Text(result.title, style: const TextStyle(color: Colors.grey, fontSize: 13)),
                  const SizedBox(height: 16),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                    decoration: BoxDecoration(
                      color: Colors.black,
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(color: bannerColor.withOpacity(0.5)),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.baseline,
                      textBaseline: TextBaseline.alphabetic,
                      children: [
                        Text(
                          result.finalScore.toStringAsFixed(2),
                          style: TextStyle(color: bannerColor, fontSize: 32, fontWeight: FontWeight.w900),
                        ),
                        Text(
                          " / ${result.totalQuestions}",
                          style: const TextStyle(color: Colors.grey, fontSize: 16, fontWeight: FontWeight.bold),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    "পাস মার্ক লক্ষ্যমাত্রা: ${(result.totalQuestions * (result.passPercentage / 100)).toInt()} (${result.passPercentage.toInt()}%)",
                    style: const TextStyle(color: Colors.grey, fontSize: 12),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 20),

            // Metrics Grid
            Row(
              children: [
                _metricCard("সঠিক উত্তর", "${result.correctCount} টি", "+${result.correctCount}.00", const Color(0xFF00E676)),
                const SizedBox(width: 10),
                _metricCard("ভুল উত্তর", "${result.wrongCount} টি", "-${result.negativeMarksDeducted.toStringAsFixed(2)} কাটা", const Color(0xFFFF5252)),
              ],
            ),
            const SizedBox(height: 10),
            Row(
              children: [
                _metricCard("অনুত্তরীত (Skipped)", "${result.skippedCount} টি", "কোনো মার্ক কাটেনি", Colors.grey),
                const SizedBox(width: 10),
                _metricCard("নির্ভুলতা (Accuracy)", "$accuracy%", "চেষ্টার ওপর", const Color(0xFF00E5FF)),
              ],
            ),

            const SizedBox(height: 24),

            // Review List
            const Text("সম্পূর্ণ OMR উত্তরপত্র পর্যালোচনা", style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),

            ...List.generate(result.totalQuestions, (index) {
              final qNum = index + 1;
              final uAns = result.userAnswers[qNum];
              final kAns = result.answerKeys[qNum];

              final isSkipped = uAns == null;
              final isCorrect = !isSkipped && kAns != null && uAns == kAns;
              final isWrong = !isSkipped && kAns != null && uAns != kAns;

              return Container(
                margin: const EdgeInsets.only(bottom: 8),
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                decoration: BoxDecoration(
                  color: const Color(0xFF0C0C0C),
                  borderRadius: BorderRadius.circular(10),
                  border: Border.all(
                    color: isCorrect
                        ? const Color(0xFF00E676).withOpacity(0.4)
                        : isWrong
                            ? const Color(0xFFFF5252).withOpacity(0.4)
                            : const Color(0xFF222222),
                  ),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        Container(
                          width: 28,
                          height: 28,
                          decoration: const BoxDecoration(color: Color(0xFF161616), shape: BoxShape.circle),
                          alignment: Alignment.Center,
                          child: Text("$qNum", style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 12)),
                        ),
                        const SizedBox(width: 12),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              children: [
                                const Text("আপনার উত্তর: ", style: TextStyle(color: Colors.grey, fontSize: 12)),
                                Text(
                                  uAns != null ? bengaliOptions[uAns] : "অনুত্তরীত",
                                  style: TextStyle(
                                    color: isCorrect ? const Color(0xFF00E676) : (isWrong ? const Color(0xFFFF5252) : Colors.grey),
                                    fontWeight: FontWeight.bold,
                                    fontSize: 12,
                                  ),
                                ),
                              ],
                            ),
                            if (kAns != null)
                              Row(
                                children: [
                                  const Text("সঠিক উত্তর: ", style: TextStyle(color: Colors.grey, fontSize: 11)),
                                  Text(
                                    bengaliOptions[kAns],
                                    style: const TextStyle(color: Color(0xFF00E676), fontWeight: FontWeight.bold, fontSize: 11),
                                  ),
                                ],
                              ),
                          ],
                        ),
                      ],
                    ),
                    Icon(
                      isCorrect ? Icons.check_circle : (isWrong ? Icons.cancel : Icons.remove_circle_outline),
                      color: isCorrect ? const Color(0xFF00E676) : (isWrong ? const Color(0xFFFF5252) : Colors.grey),
                      size: 18,
                    ),
                  ],
                ),
              );
            }),
          ],
        ),
      ),
    );
  }

  Widget _metricCard(String title, String val, String sub, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: const Color(0xFF161616),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: const Color(0xFF262626)),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, style: const TextStyle(color: Colors.grey, fontSize: 12)),
            const SizedBox(height: 4),
            Text(val, style: TextStyle(color: color, fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 2),
            Text(sub, style: const TextStyle(color: Colors.grey, fontSize: 10)),
          ],
        ),
      ),
    );
  }
}
