import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class AboutScreen extends StatelessWidget {
  const AboutScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF000000),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0C0C0C),
        title: const Text("ডেভেলপার পরিচিতি", style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            // Profile Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(20),
                gradient: const LinearGradient(
                  colors: [Color(0xFF00222A), Color(0xFF0C0C0C)],
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                ),
                border: Border.all(color: const Color(0xFF00E5FF).withOpacity(0.4)),
              ),
              child: Column(
                children: [
                  Container(
                    width: 76,
                    height: 76,
                    decoration: BoxDecoration(
                      color: const Color(0xFF00E5FF).withOpacity(0.15),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(Icons.person, color: Color(0xFF00E5FF), size: 40),
                  ),
                  const SizedBox(height: 12),
                  const Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text("Nahid Hasan", style: TextStyle(color: Colors.white, fontSize: 22, fontWeight: FontWeight.bold)),
                      SizedBox(width: 6),
                      Icon(Icons.verified, color: Color(0xFF00E5FF), size: 18),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                    decoration: BoxDecoration(color: Colors.black, borderRadius: BorderRadius.circular(6)),
                    child: const Text("Lead Developer & Creator", style: TextStyle(color: Color(0xFFFFD54F), fontSize: 12, fontWeight: FontWeight.w600)),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 20),

            _detailCard(Icons.person, "Name", "Nahid Hasan", const Color(0xFF00E5FF)),
            const SizedBox(height: 10),
            _detailCard(Icons.school, "Institute", "University of Barishal", const Color(0xFF00E676)),
            const SizedBox(height: 10),
            _detailCard(Icons.location_on, "Address", "Harinakundu, Jhenaidah", const Color(0xFFFFD54F)),
            const SizedBox(height: 10),
            _detailCard(
              Icons.email,
              "Email",
              "sknahid.study@gmail.com",
              const Color(0xFF00E5FF),
              onTap: () async {
                final uri = Uri.parse("mailto:sknahid.study@gmail.com");
                if (await canLaunchUrl(uri)) {
                  await launchUrl(uri);
                }
              },
            ),

            const SizedBox(height: 24),

            // App Philosophy
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFF161616),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: const Color(0xFF262626)),
              ),
              child: const Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text("RIP-Exam সম্পর্কে", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 15)),
                  SizedBox(height: 8),
                  Text(
                    "RIP-Exam হলো BCS, ব্যাংক, বিশ্ববিদ্যালয় ভর্তি ও অন্যান্য প্রতিযোগিতামূলক পরীক্ষার জন্য একটি আধুনিক ডিজিটাল OMR প্ল্যাটফর্ম। ওয়ান-টাচ সিলেকশন, লাইভ টাইমার এবং রিয়েল-টাইম ক্র্যাশ ব্যাকআপের সাহায্যে পরীক্ষার নিখুঁত অভিজ্ঞতা নিশ্চিত করাই এই অ্যাপের মূল লক্ষ্য।",
                    style: TextStyle(color: Colors.grey, fontSize: 13, height: 1.4),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _detailCard(IconData icon, String label, String value, Color color, {VoidCallback? onTap}) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: const Color(0xFF161616),
          borderRadius: BorderRadius.circular(14),
          border: Border.all(color: const Color(0xFF262626)),
        ),
        child: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(color: color.withOpacity(0.12), shape: BoxShape.circle),
              child: Icon(icon, color: color, size: 20),
            ),
            const SizedBox(width: 14),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(label, style: const TextStyle(color: Colors.grey, fontSize: 11)),
                Text(value, style: const TextStyle(color: Colors.white, fontSize: 15, fontWeight: FontWeight.w600)),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
