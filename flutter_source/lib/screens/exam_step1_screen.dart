import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../main.dart';
import 'exam_step2_screen.dart';

class ExamStep1Screen extends StatelessWidget {
  const ExamStep1Screen({super.key});

  static const List<String> bengaliOptions = ["ক", "খ", "গ", "ঘ"];

  @override
  Widget build(BuildContext context) {
    final examProvider = context.watch<ExamProvider>();
    final total = examProvider.activeTotalQuestions;
    final answered = examProvider.userAnswers.length;
    final remaining = total - answered;

    final mins = examProvider.activeRemainingSeconds ~/ 60;
    final secs = examProvider.activeRemainingSeconds % 60;
    final timerString = "${mins.toString().padLeft(2, '0')}:${secs.toString().padLeft(2, '0')}";

    return WillPopScope(
      onWillPop: () async {
        final exit = await showDialog<bool>(
          context: context,
          builder: (ctx) => AlertDialog(
            backgroundColor: const Color(0xFF161616),
            title: const Text("পরীক্ষা থেকে বের হবেন?", style: TextStyle(color: Colors.white)),
            content: const Text("আপনার উত্তর এবং সময় সেভ থাকবে। আপনি হোমে গিয়ে যেকোনো সময় পুনরায় চালু করতে পারবেন।", style: TextStyle(color: Colors.grey)),
            actions: [
              TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text("পরীক্ষায় থাকুন")),
              ElevatedButton(
                onPressed: () => Navigator.pop(ctx, true),
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF00E5FF), foregroundColor: Colors.black),
                child: const Text("হোমে যান"),
              ),
            ],
          ),
        );
        return exit ?? false;
      },
      child: Scaffold(
        backgroundColor: const Color(0xFF000000),
        appBar: AppBar(
          backgroundColor: const Color(0xFF0C0C0C),
          elevation: 4,
          title: Text(examProvider.activeTitle, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          actions: [
            Container(
              margin: const EdgeInsets.symmetric(vertical: 10, horizontal: 8),
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
              decoration: BoxDecoration(
                color: examProvider.activeRemainingSeconds < 300 ? const Color(0xFFFF5252).withOpacity(0.2) : const Color(0xFF1E1E1E),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: examProvider.activeRemainingSeconds < 300 ? const Color(0xFFFF5252) : const Color(0xFF00E5FF)),
              ),
              child: Row(
                children: [
                  Icon(Icons.timer, size: 14, color: examProvider.activeRemainingSeconds < 300 ? const Color(0xFFFF5252) : const Color(0xFF00E5FF)),
                  const SizedBox(width: 4),
                  Text(
                    timerString,
                    style: TextStyle(
                      color: examProvider.activeRemainingSeconds < 300 ? const Color(0xFFFF5252) : const Color(0xFF00E5FF),
                      fontWeight: FontWeight.bold,
                      fontSize: 13,
                    ),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(right: 12),
              child: ElevatedButton(
                onPressed: () => _confirmSubmit(context, examProvider),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF00E676),
                  foregroundColor: Colors.black,
                  padding: const EdgeInsets.symmetric(horizontal: 12),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
                child: const Text("জমা দিন", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
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
                  Text("উত্তর দেওয়া: $answered / $total", style: const TextStyle(color: Colors.white, fontSize: 13)),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                    decoration: BoxDecoration(color: const Color(0xFFFFD54F).withOpacity(0.15), borderRadius: BorderRadius.circular(6)),
                    child: Text("বাকি আছে: $remaining টি", style: const TextStyle(color: Color(0xFFFFD54F), fontSize: 12, fontWeight: FontWeight.bold)),
                  ),
                ],
              ),
            ),
            const LinearProgressIndicator(
              valueColor: AlwaysStoppedAnimation<Color>(Color(0xFF00E5FF)),
              backgroundColor: Color(0xFF262626),
              minHeight: 3,
            ),
            // One-touch Notice
            Container(
              width: double.infinity,
              color: const Color(0xFF181818),
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
              child: const Row(
                children: [
                  Icon(Icons.lock, size: 14, color: Color(0xFF00E5FF)),
                  SizedBox(width: 6),
                  Text("ওয়ান-টাচ নিয়ম: একবার অপশন সিলেক্ট করলে তা আর পরিবর্তন করা যাবে না।", style: TextStyle(color: Colors.grey, fontSize: 11)),
                ],
              ),
            ),

            // OMR Grid / List
            Expanded(
              child: ListView.builder(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                itemCount: total,
                itemBuilder: (ctx, index) {
                  final qNum = index + 1;
                  final selectedOpt = examProvider.userAnswers[qNum];
                  final isAnswered = selectedOpt != null;

                  return Container(
                    margin: const EdgeInsets.only(bottom: 10),
                    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                    decoration: BoxDecoration(
                      color: const Color(0xFF0C0C0C),
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(color: isAnswered ? const Color(0xFF00E5FF).withOpacity(0.5) : const Color(0xFF262626)),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Row(
                          children: [
                            Container(
                              width: 32,
                              height: 32,
                              decoration: BoxDecoration(
                                color: isAnswered ? const Color(0xFF00E5FF).withOpacity(0.15) : const Color(0xFF161616),
                                shape: BoxShape.circle,
                              ),
                              alignment: Alignment.Center,
                              child: Text(
                                "$qNum",
                                style: TextStyle(color: isAnswered ? const Color(0xFF00E5FF) : Colors.white, fontWeight: FontWeight.bold),
                              ),
                            ),
                            if (isAnswered) ...[
                              const SizedBox(width: 4),
                              const Icon(Icons.lock, size: 12, color: Color(0xFF00E5FF)),
                            ],
                          ],
                        ),

                        // Options ক, খ, গ, ঘ
                        Row(
                          children: List.generate(4, (optIndex) {
                            final isThisSelected = selectedOpt == optIndex;
                            return GestureDetector(
                              onTap: () {
                                if (isAnswered) {
                                  ScaffoldMessenger.of(context).showSnackBar(
                                    const SnackBar(
                                      content: Text("১ম ধাপে একবার সিলেক্ট করার পর অপশন পরিবর্তন করা যাবে না!"),
                                      duration: Duration(milliseconds: 1200),
                                    ),
                                  );
                                } else {
                                  examProvider.selectUserAnswer(qNum, optIndex);
                                }
                              },
                              child: Container(
                                width: 38,
                                height: 38,
                                margin: const EdgeInsets.symmetric(horizontal: 4),
                                decoration: BoxDecoration(
                                  color: isThisSelected ? const Color(0xFF00E5FF) : const Color(0xFF121212),
                                  shape: BoxShape.circle,
                                  border: Border.all(color: isThisSelected ? const Color(0xFF00E5FF) : const Color(0xFF404040), width: 1.5),
                                ),
                                alignment: Alignment.Center,
                                child: Text(
                                  bengaliOptions[optIndex],
                                  style: TextStyle(
                                    color: isThisSelected ? Colors.black : Colors.white,
                                    fontWeight: isThisSelected ? FontWeight.bold : FontWeight.normal,
                                    fontSize: 14,
                                  ),
                                ),
                              ),
                            );
                          }),
                        ),
                      ],
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _confirmSubmit(BuildContext context, ExamProvider provider) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: const Color(0xFF161616),
        title: const Text("১ম ধাপ জমা দিতে চান?", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        content: Text(
          "উত্তর সম্পন্ন: ${provider.userAnswers.length} টি\nবাকি আছে: ${provider.activeTotalQuestions - provider.userAnswers.length} টি\n\nজমা দিলে ১ম ধাপ শেষ হয়ে ২য় ধাপে উত্তরমালা মেলাতে হবে।",
          style: const TextStyle(color: Colors.grey),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text("বাতিল")),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(ctx);
              provider.advanceToStep2();
              Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const ExamStep2Screen()));
            },
            style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF00E676), foregroundColor: Colors.black),
            child: const Text("হ্যাঁ, জমা দিন", style: TextStyle(fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }
}
