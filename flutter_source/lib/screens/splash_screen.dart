import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'home_screen.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    Future.delayed(const Duration(milliseconds: 2800), () {
      if (mounted) {
        Navigator.of(context).pushReplacement(
          PageRouteBuilder(
            pageBuilder: (context, animation, secondaryAnimation) => const HomeScreen(),
            transitionsBuilder: (context, animation, secondaryAnimation, child) {
              return FadeTransition(opacity: animation, child: child);
            },
            transitionDuration: const Duration(milliseconds: 600),
          ),
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF000000), // AMOLED Pure Black
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // "Nahid Bro" with Apple-like smooth scale and fade motion
            const Text(
              "Nahid Bro",
              style: TextStyle(
                color: Color(0xFFFFFFFF),
                fontSize: 42,
                fontWeight: FontWeight.bold,
                letterSpacing: 2.0,
                fontFamily: 'sans-serif',
              ),
            )
                .animate()
                .fadeIn(duration: 1100.ms, curve: Curves.easeOutCubic)
                .scale(begin: const Offset(0.85, 0.85), end: const Offset(1.0, 1.0), duration: 1200.ms, curve: Curves.easeOutCubic)
                .then(delay: 800.ms)
                .fadeOut(duration: 500.ms),

            const SizedBox(height: 12),

            const Text(
              "RIP-Exam • OMR Engine",
              style: TextStyle(
                color: Color(0xFF00E5FF),
                fontSize: 14,
                fontWeight: FontWeight.w600,
                letterSpacing: 1.5,
              ),
            )
                .animate()
                .fadeIn(delay: 400.ms, duration: 800.ms)
                .then(delay: 700.ms)
                .fadeOut(duration: 500.ms),
          ],
        ),
      ),
    );
  }
}
