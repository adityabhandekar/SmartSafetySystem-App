import 'package:flutter_test/flutter_test.dart';
import 'package:women_safety_app/main.dart';

void main() {
  testWidgets('Women Safety App loads', (WidgetTester tester) async {
    await tester.pumpWidget(const WomenSafetyApp());

    expect(find.byType(WomenSafetyApp), findsOneWidget);
  });
}