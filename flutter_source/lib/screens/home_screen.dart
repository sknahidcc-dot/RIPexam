import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../main.dart';
import 'about_screen.dart';
import 'exam_step1_screen.dart';
import 'exam_step2_screen.dart';
import 'history_analytics_screen.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final examProvider = context.watch<ExamProvider>();
    final historyList = examProvider.historyList;

    return Scaffold(
      backgroundColor: const Color(0xFF000000),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Container(
                            width: 10,
                            height: 10,
                            decoration: const BoxDecoration(
                              color: Color(0xFF00E5FF),
                              shape: BoxShape.circle,
                            ),
                          ),
                          const SizedBox(width: 8),
                          const Text(
                            "RIP-Exam",
                            style: TextStyle(
                              color: Colors.white,
                              fontSize: 24,
                              fontWeight: FontWeight.w900,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 2),
                      const Text(
                        "স্মার্ট OMR শিট ও মূল্যায়ন প্ল্যাটফর্ম",
                        style: TextStyle(color: Colors.grey, fontSize: 13),
                      ),
                    ],
                  ),
                  IconButton(
                    onPressed: () {
                      Navigator.push(context, MaterialPageRoute(builder: (_) => const AboutScreen()));
                    },
                    icon: const Icon(Icons.info_outline, color: Color(0xFF00E5FF)),
                  ),
                ],
              ),

              const SizedBox(height: 20),

              // Active Exam Recovery Banner
              if (examProvider.hasActiveExam) ...[
                Container(
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: const Color(0xFF161616),
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: const Color(0xFFFFD54F), width: 1.5),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Row(
                            children: [
                              Icon(Icons.timer_outlined, color: Color(0xFFFFD54F), size: 20),
                              SizedBox(width: 8),
                              Text(
                                "অসম্পূর্ণ পরীক্ষা সক্রিয় আছে",
                                style: TextStyle(color: Color(0xFFFFD54F), fontWeight: FontWeight.bold),
                              ),
                            ],
                          ),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                            decoration: BoxDecoration(
                              color: Colors.black,
                              borderRadius: BorderRadius.circular(6),
                            ),
                            child: Text(
                              examProvider.activeStep == 1 ? "ধাপ ১ (পরীক্ষা)" : "ধাপ ২ (উত্তরমালা)",
                              style: const TextStyle(color: Colors.white, fontSize: 11),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 10),
                      Text(
                        examProvider.activeTitle,
                        style: const TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        "উত্তর সম্পন্ন: ${examProvider.userAnswers.length}/${examProvider.activeTotalQuestions} • বাকি সময়: ${examProvider.activeRemainingSeconds ~/ 60}m",
                        style: const TextStyle(color: Colors.grey, fontSize: 13),
                      ),
                      const SizedBox(height: 14),
                      Row(
                        children: [
                          Expanded(
                            child: ElevatedButton.icon(
                              onPressed: () {
                                examProvider.resumeActiveExam();
                                if (examProvider.activeStep == 1) {
                                  Navigator.push(context, MaterialPageRoute(builder: (_) => const ExamStep1Screen()));
                                } else {
                                  Navigator.push(context, MaterialPageRoute(builder: (_) => const ExamStep2Screen()));
                                }
                              },
                              style: ElevatedButton.styleFrom(
                                backgroundColor: const Color(0xFF00E5FF),
                                foregroundColor: Colors.black,
                                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                              ),
                              icon: const Icon(Icons.play_arrow),
                              label: const Text("পুনরুদ্ধার করুন", style: TextStyle(fontWeight: FontWeight.bold)),
                            ),
                          ),
                          const SizedBox(width: 10),
                          IconButton(
                            onPressed: () => examProvider.discardActiveExam(),
                            icon: const Icon(Icons.delete_outline, color: Color(0xFFFF5252)),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 20),
              ],

              // Hero CTA Card
              GestureDetector(
                onTap: () => _showSetupDialog(context),
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(20),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(20),
                    gradient: const LinearGradient(
                      colors: [Color(0xFF002930), Color(0xFF0C0C0C)],
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                    ),
                    border: Border.all(color: const Color(0xFF00E5FF).withOpacity(0.4)),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: const Color(0xFF00E5FF).withOpacity(0.15),
                          borderRadius: BorderRadius.circular(6),
                        ),
                        child: const Text(
                          "ডিজিটাল OMR টেস্ট",
                          style: TextStyle(color: Color(0xFF00E5FF), fontSize: 12, fontWeight: FontWeight.bold),
                        ),
                      ),
                      const SizedBox(height: 12),
                      const Text(
                        "নতুন পরীক্ষা শুরু করুন",
                        style: TextStyle(color: Colors.white, fontSize: 22, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 6),
                      const Text(
                        "১০০টি প্রশ্ন • ৬০ মিনিট • নেগেটিভ ০.৫০ • ওয়ান-টাচ OMR",
                        style: TextStyle(color: Colors.grey, fontSize: 13),
                      ),
                      const SizedBox(height: 18),
                      SizedBox(
                        width: double.infinity,
                        height: 48,
                        child: ElevatedButton(
                          onPressed: () => _showSetupDialog(context),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFF00E5FF),
                            foregroundColor: Colors.black,
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                          ),
                          child: const Text("কনফিগার ও শুরু করুন", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                        ),
                      ),
                    ],
                  ),
                ),
              ),

              const SizedBox(height: 24),

              // Summary Stats
              const Text("সারসংক্ষেপ ও পারফর্মেন্স", style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              Row(
                children: [
                  _statBox("মোট পরীক্ষা", "${historyList.length}", const Color(0xFF00E5FF)),
                  const SizedBox(width: 10),
                  _statBox(
                    "পাস রেট",
                    historyList.isEmpty ? "0%" : "${((historyList.where((e) => e.isPassed).length / historyList.length) * 100).toInt()}%",
                    const Color(0xFF00E676),
                  ),
                  const SizedBox(width: 10),
                  _statBox(
                    "গড় স্কোর",
                    historyList.isEmpty ? "0.0" : (historyList.map((e) => e.finalScore).reduce((a, b) => a + b) / historyList.length).toStringAsFixed(1),
                    const Color(0xFFFFD54F),
                  ),
                ],
              ),

              const SizedBox(height: 24),

              // Navigation Cards
              const Text("মেন্যু ও টুলস", style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              _menuTile(
                title: "বিগত পরীক্ষার হিস্ট্রি",
                subtitle: "পূর্ববর্তী পরীক্ষার ফলাফল ও সম্পূর্ণ উত্তরপত্র",
                icon: Icons.history,
                color: const Color(0xFF00E5FF),
                onTap: () {
                  Navigator.push(context, MaterialPageRoute(builder: (_) => const HistoryAnalyticsScreen(initialTab: 0)));
                },
              ),
              const SizedBox(height: 10),
              _menuTile(
                title: "ডাটা এনালাইসিস ও গ্রাফ",
                subtitle: "প্রোগ্রেস চার্ট ও প্রস্তুতি যাচাইকরণ",
                icon: Icons.analytics_outlined,
                color: const Color(0xFF00E676),
                onTap: () {
                  Navigator.push(context, MaterialPageRoute(builder: (_) => const HistoryAnalyticsScreen(initialTab: 1)));
                },
              ),
              const SizedBox(height: 10),
              _menuTile(
                title: "ডেভেলপার পরিচিতি",
                subtitle: "Nahid Hasan • University of Barishal",
                icon: Icons.person_outline,
                color: const Color(0xFFFFD54F),
                onTap: () {
                  Navigator.push(context, MaterialPageRoute(builder: (_) => const AboutScreen()));
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _statBox(String title, String value, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 14),
        decoration: BoxDecoration(
          color: const Color(0xFF161616),
          borderRadius: BorderRadius.circular(14),
          border: Border.all(color: const Color(0xFF262626)),
        ),
        child: Column(
          children: [
            Text(value, style: TextStyle(color: color, fontSize: 20, fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            Text(title, style: const TextStyle(color: Colors.grey, fontSize: 12)),
          ],
        ),
      ),
    );
  }

  Widget _menuTile({
    required String title,
    required String subtitle,
    required IconData icon,
    required Color color,
    required VoidCallback onTap,
  }) {
    return Container(
      decoration: BoxDecoration(
        color: const Color(0xFF161616),
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: const Color(0xFF262626)),
      ),
      child: ListTile(
        leading: Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(color: color.withOpacity(0.12), shape: BoxShape.circle),
          child: Icon(icon, color: color, size: 20),
        ),
        title: Text(title, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w600, fontSize: 15)),
        subtitle: Text(subtitle, style: const TextStyle(color: Colors.grey, fontSize: 12)),
        trailing: const Icon(Icons.arrow_forward_ios, color: Colors.grey, size: 14),
        onTap: onTap,
      ),
    );
  }

  void _showSetupDialog(BuildContext context) {
    final titleCtrl = TextEditingController(text: 'মডেল টেস্ট');
    int totalQuestions = 100;
    int durationMinutes = 60;
    double negativeRate = 0.50;
    double passPercentage = 80.0;

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: const Color(0xFF121212),
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(20))),
      builder: (ctx) {
        return StatefulBuilder(
          builder: (context, setModalState) {
            return Padding(
              padding: EdgeInsets.only(
                left: 20,
                right: 20,
                top: 20,
                bottom: MediaQuery.of(context).viewInsets.bottom + 20,
              ),
              child: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text("পরীক্ষা কনফিগারেশন", style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 14),
                    TextField(
                      controller: titleCtrl,
                      style: const TextStyle(color: Colors.white),
                      decoration: InputDecoration(
                        labelText: "পরীক্ষার বিষয় / নাম",
                        labelStyle: const TextStyle(color: Colors.grey),
                        filled: true,
                        fillColor: const Color(0xFF1A1A1A),
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                      ),
                    ),
                    const SizedBox(height: 14),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text("মোট প্রশ্ন সংখ্যা:", style: TextStyle(color: Colors.white)),
                        Text("$totalQuestions টি", style: const TextStyle(color: Color(0xFF00E5FF), fontWeight: FontWeight.bold)),
                      ],
                    ),
                    Slider(
                      value: totalQuestions.toDouble(),
                      min: 10,
                      max: 200,
                      divisions: 19,
                      activeColor: const Color(0xFF00E5FF),
                      onChanged: (val) => setModalState(() => totalQuestions = val.toInt()),
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text("সময় (মিনিট):", style: TextStyle(color: Colors.white)),
                        Text("$durationMinutes মিনিট", style: const TextStyle(color: Color(0xFFFFD54F), fontWeight: FontWeight.bold)),
                      ],
                    ),
                    Slider(
                      value: durationMinutes.toDouble(),
                      min: 10,
                      max: 180,
                      divisions: 17,
                      activeColor: const Color(0xFFFFD54F),
                      onChanged: (val) => setModalState(() => durationMinutes = val.toInt()),
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text("নেগেটিভ মার্কিং:", style: TextStyle(color: Colors.white)),
                        Text("-$negativeRate", style: const TextStyle(color: Color(0xFFFF5252), fontWeight: FontWeight.bold)),
                      ],
                    ),
                    Slider(
                      value: negativeRate,
                      min: 0.0,
                      max: 1.0,
                      divisions: 4,
                      activeColor: const Color(0xFFFF5252),
                      onChanged: (val) => setModalState(() => negativeRate = val),
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text("পাস মার্ক শতাংশ:", style: TextStyle(color: Colors.white)),
                        Text("${passPercentage.toInt()}%", style: const TextStyle(color: Color(0xFF00E676), fontWeight: FontWeight.bold)),
                      ],
                    ),
                    Slider(
                      value: passPercentage,
                      min: 40.0,
                      max: 100.0,
                      divisions: 12,
                      activeColor: const Color(0xFF00E676),
                      onChanged: (val) => setModalState(() => passPercentage = val),
                    ),
                    const SizedBox(height: 16),
                    SizedBox(
                      width: double.infinity,
                      height: 50,
                      child: ElevatedButton(
                        onPressed: () {
                          Navigator.pop(ctx);
                          final provider = Provider.of<ExamProvider>(context, listen: false);
                          provider.startNewExam(
                            title: titleCtrl.text,
                            totalQuestions: totalQuestions,
                            durationMinutes: durationMinutes,
                            negativeRate: negativeRate,
                            passPercentage: passPercentage,
                          );
                          Navigator.push(context, MaterialPageRoute(builder: (_) => const ExamStep1Screen()));
                        },
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF00E5FF),
                          foregroundColor: Colors.black,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                        ),
                        child: const Text("পরীক্ষা শুরু করুন", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                      ),
                    ),
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }
}
