import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../main.dart';
import '../models/exam_model.dart';
import 'result_screen.dart';

class HistoryAnalyticsScreen extends StatefulWidget {
  final int initialTab;
  const HistoryAnalyticsScreen({super.key, this.initialTab = 0});

  @override
  State<HistoryAnalyticsScreen> createState() => _HistoryAnalyticsScreenState();
}

class _HistoryAnalyticsScreenState extends State<HistoryAnalyticsScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this, initialIndex: widget.initialTab);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<ExamProvider>();
    final historyList = provider.historyList;

    return Scaffold(
      backgroundColor: const Color(0xFF000000),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0C0C0C),
        title: const Text("হিস্ট্রি ও এনালাইসিস", style: TextStyle(fontWeight: FontWeight.bold)),
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: const Color(0xFF00E5FF),
          labelColor: const Color(0xFF00E5FF),
          unselectedLabelColor: Colors.grey,
          tabs: [
            Tab(text: "হিস্ট্রি (${historyList.length})"),
            const Tab(text: "ডাটা এনালাইসিস"),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          // Tab 1: History
          historyList.isEmpty
              ? const Center(
                  child: Text("এখনো কোনো পরীক্ষার হিস্ট্রি নেই", style: TextStyle(color: Colors.grey)),
                )
              : ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: historyList.length,
                  itemBuilder: (ctx, index) {
                    final item = historyList[index];
                    final dateStr = DateFormat("dd MMM yyyy, hh:mm a").format(item.timestamp);
                    final isPassed = item.isPassed;

                    return Container(
                      margin: const EdgeInsets.only(bottom: 12),
                      decoration: BoxDecoration(
                        color: const Color(0xFF161616),
                        borderRadius: BorderRadius.circular(14),
                        border: Border.all(color: const Color(0xFF262626)),
                      ),
                      child: ListTile(
                        onTap: () {
                          Navigator.push(context, MaterialPageRoute(builder: (_) => ResultScreen(result: item)));
                        },
                        title: Text(item.title, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(dateStr, style: const TextStyle(color: Colors.grey, fontSize: 11)),
                            const SizedBox(height: 4),
                            Text(
                              "সঠিক: ${item.correctCount} • ভুল: ${item.wrongCount} • বাদ: ${item.skippedCount}",
                              style: const TextStyle(color: Colors.white70, fontSize: 12),
                            ),
                          ],
                        ),
                        trailing: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              item.finalScore.toStringAsFixed(1),
                              style: const TextStyle(color: Color(0xFF00E5FF), fontSize: 18, fontWeight: FontWeight.bold),
                            ),
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                              decoration: BoxDecoration(
                                color: isPassed ? const Color(0xFF00E676).withOpacity(0.15) : const Color(0xFFFF5252).withOpacity(0.15),
                                borderRadius: BorderRadius.circular(4),
                              ),
                              child: Text(
                                isPassed ? "পাস" : "ফেল",
                                style: TextStyle(
                                  color: isPassed ? const Color(0xFF00E676) : const Color(0xFFFF5252),
                                  fontSize: 10,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                ),

          // Tab 2: Analytics
          _buildAnalyticsView(historyList),
        ],
      ),
    );
  }

  Widget _buildAnalyticsView(List<ExamHistoryModel> list) {
    if (list.isEmpty) {
      return const Center(child: Text("এনালাইসিসের জন্য পর্যাপ্ত পরীক্ষা নেই", style: TextStyle(color: Colors.grey)));
    }

    final total = list.length;
    final passed = list.where((e) => e.isPassed).length;
    final passRate = ((passed / total) * 100).toInt();
    final avgScore = list.map((e) => e.finalScore).reduce((a, b) => a + b) / total;
    final highest = list.map((e) => e.finalScore).reduce((a, b) => a > b ? a : b);
    final totalCorrect = list.map((e) => e.correctCount).reduce((a, b) => a + b);
    final totalWrong = list.map((e) => e.wrongCount).reduce((a, b) => a + b);

    return ListView(
      padding: const EdgeInsets.all(20),
      children: [
        Container(
          padding: const EdgeInsets.all(18),
          decoration: BoxDecoration(
            color: const Color(0xFF161616),
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: const Color(0xFF262626)),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text("সার্বিক পারফর্মেন্স সূচক", style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
              const SizedBox(height: 16),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  _statCol("সর্বোচ্চ স্কোর", highest.toStringAsFixed(1), const Color(0xFFFFD54F)),
                  _statCol("গড় স্কোর", avgScore.toStringAsFixed(1), const Color(0xFF00E5FF)),
                  _statCol("পাস রেট", "$passRate%", const Color(0xFF00E676)),
                ],
              ),
            ],
          ),
        ),

        const SizedBox(height: 16),

        Container(
          padding: const EdgeInsets.all(18),
          decoration: BoxDecoration(
            color: const Color(0xFF161616),
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: const Color(0xFF262626)),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text("প্রশ্নোত্তর নির্ভুলতা অনুপাত", style: TextStyle(color: Colors.white, fontSize: 15, fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              _rowText("সর্বমোট সঠিক উত্তর:", "$totalCorrect টি", const Color(0xFF00E676)),
              const SizedBox(height: 8),
              _rowText("সর্বমোট ভুল উত্তর:", "$totalWrong টি", const Color(0xFFFF5252)),
              const SizedBox(height: 8),
              _rowText("মোট পরীক্ষা সংখ্যা:", "$total টি", const Color(0xFF00E5FF)),
            ],
          ),
        ),
      ],
    );
  }

  Widget _statCol(String label, String val, Color color) {
    return Column(
      children: [
        Text(val, style: TextStyle(color: color, fontSize: 22, fontWeight: FontWeight.bold)),
        const SizedBox(height: 4),
        Text(label, style: const TextStyle(color: Colors.grey, fontSize: 12)),
      ],
    );
  }

  Widget _rowText(String label, String val, Color color) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: const TextStyle(color: Colors.grey, fontSize: 13)),
        Text(val, style: TextStyle(color: color, fontWeight: FontWeight.bold, fontSize: 14)),
      ],
    );
  }
}
