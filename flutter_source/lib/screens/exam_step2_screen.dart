import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../main.dart';
import 'result_screen.dart';

class ExamStep2Screen extends StatelessWidget {
  const ExamStep2Screen({super.key});

  static const List<String> bengaliOptions = ["ক", "খ", "গ", "ঘ"];

  @override
  Widget build(BuildContext context) {
    final examProvider = context.watch<ExamProvider>();
    final total = examProvider.activeTotalQuestions;
    final keysCount = examProvider.answerKeys.length;
    final remainingKeys = total - keysCount;

    return Scaffold(
      backgroundColor: const Color(0xFF000000),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0C0C0C),
        elevation: 4,
        title: const Text("ধাপ ২: উত্তরমালা মেলাব", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 12),
            child: ElevatedButton.icon(
              onPressed: () => _confirmFinalResult(context, examProvider),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF00E676),
                foregroundColor: Colors.black,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              ),
              icon: const Icon(Icons.check_circle_outline, size: 16),
              label: const Text("ফলাফল", style: TextStyle(fontWeight: FontWeight.bold)),
            ),
          ),
        ],
      ),
      body: Column(
        children: [
          // Status bar
          Container(
            color: const Color(0xFF121212),
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text("উত্তরমালা ইনপুট: $keysCount / $total", style: const TextStyle(color: Colors.white, fontSize: 13)),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: remainingKeys == 0 ? const Color(0xFF00E676).withOpacity(0.15) : const Color(0xFFFFD54F).withOpacity(0.15),
                    borderRadius: BorderRadius.circular(6),
                  ),
                  child: Text(
                    remainingKeys == 0 ? "সব মেলানো সম্পন্ন!" : "উত্তরমালা বাকি: $remainingKeys টি",
                    style: TextStyle(
                      color: remainingKeys == 0 ? const Color(0xFF00E676) : const Color(0xFFFFD54F),
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
          ),
          const LinearProgressIndicator(
            valueColor: AlwaysStoppedAnimation<Color>(Color(0xFF00E676)),
            backgroundColor: Color(0xFF262626),
            minHeight: 3,
          ),
          Container(
            width: double.infinity,
            color: const Color(0xFF181818),
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
            child: const Text(
              "বামপাশে আপনার উত্তর ও ডানপাশে সঠিক উত্তরমালা সিলেক্ট করুন (প্রয়োজনে পরিবর্তনযোগ্য)।",
              style: TextStyle(color: Colors.grey, fontSize: 11),
            ),
          ),

          // List of comparison
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              itemCount: total,
              itemBuilder: (ctx, index) {
                final qNum = index + 1;
                final candidateAns = examProvider.userAnswers[qNum];
                final correctKey = examProvider.answerKeys[qNum];
                final isEvaluated = correctKey != null;
                final isMatch = isEvaluated && candidateAns != null && candidateAns == correctKey;

                return Container(
                  margin: const EdgeInsets.only(bottom: 10),
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: const Color(0xFF0C0C0C),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: !isEvaluated
                          ? const Color(0xFF262626)
                          : isMatch
                              ? const Color(0xFF00E676).withOpacity(0.5)
                              : const Color(0xFFFF5252).withOpacity(0.5),
                    ),
                  ),
                  child: Column(
                    children: [
                      Row(
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
                              const SizedBox(width: 8),
                              const Text("আপনার উত্তর: ", style: TextStyle(color: Colors.grey, fontSize: 12)),
                              if (candidateAns != null)
                                Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                  decoration: BoxDecoration(
                                    color: const Color(0xFF00E5FF).withOpacity(0.2),
                                    shape: BoxShape.circle,
                                    border: Border.all(color: const Color(0xFF00E5FF)),
                                  ),
                                  child: Text(
                                    bengaliOptions[candidateAns],
                                    style: const TextStyle(color: Color(0xFF00E5FF), fontWeight: FontWeight.bold, fontSize: 11),
                                  ),
                                )
                              else
                                const Text("অনুত্তরীত", style: TextStyle(color: Colors.grey, fontSize: 11)),
                            ],
                          ),
                          if (isEvaluated)
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                              decoration: BoxDecoration(
                                color: isMatch ? const Color(0xFF00E676).withOpacity(0.15) : const Color(0xFFFF5252).withOpacity(0.15),
                                borderRadius: BorderRadius.circular(4),
                              ),
                              child: Text(
                                isMatch ? "সঠিক" : "ভুল",
                                style: TextStyle(
                                  color: isMatch ? const Color(0xFF00E676) : const Color(0xFFFF5252),
                                  fontSize: 10,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                        ],
                      ),
                      const SizedBox(height: 10),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Text("সঠিক উত্তরমালা:", style: TextStyle(color: Colors.white, fontSize: 12)),
                          Row(
                            children: List.generate(4, (optIndex) {
                              final isSelected = correctKey == optIndex;
                              return GestureDetector(
                                onTap: () => examProvider.selectAnswerKey(qNum, optIndex),
                                child: Container(
                                  width: 34,
                                  height: 34,
                                  margin: const EdgeInsets.symmetric(horizontal: 4),
                                  decoration: BoxDecoration(
                                    color: isSelected ? const Color(0xFF00E676) : const Color(0xFF121212),
                                    shape: BoxShape.circle,
                                    border: Border.all(color: isSelected ? const Color(0xFF00E676) : const Color(0xFF404040)),
                                  ),
                                  alignment: Alignment.Center,
                                  child: Text(
                                    bengaliOptions[optIndex],
                                    style: TextStyle(
                                      color: isSelected ? Colors.black : Colors.white,
                                      fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                                    ),
                                  ),
                                ),
                              );
                            }),
                          ),
                        ],
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  void _confirmFinalResult(BuildContext context, ExamProvider provider) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: const Color(0xFF161616),
        title: const Text("ফলাফল বের করতে চান?", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        content: Text(
          "উত্তরমালা ইনপুট: ${provider.answerKeys.length} টি\nবাকি আছে: ${provider.activeTotalQuestions - provider.answerKeys.length} টি\n\nফলাফল হিসাব করে ফাইনাল স্কোর ও ওএমআর শিট সেভ করা হবে।",
          style: const TextStyle(color: Colors.grey),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text("বাতিল")),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(ctx);
              final result = provider.submitFinalResult();
              Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => ResultScreen(result: result)));
            },
            style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF00E676), foregroundColor: Colors.black),
            child: const Text("হ্যাঁ, ফলাফল দেখুন", style: TextStyle(fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }
}
