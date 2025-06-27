

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class QuizPlatform {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int min = 1;
        int max = 20;
        Random random = new Random();
        String questionsFile = "questions.txt";
        String answersFile = "answers.txt";

        // Game statistics
        int correctAnswers = 0;
        int wrongAnswers = 0;
        int totalQuestions = 0;
        int questionsToPlay = 0;

        // Ask user how many questions they want to play
        System.out.println("🎯 Welcome to the Java Quiz Platform! 🎯");
        System.out.println("Available questions: 1-" + max);
        while (questionsToPlay <= 0 || questionsToPlay > max) {
            System.out.print("How many questions would you like to play? (1-" + max + "): ");
            try {
                questionsToPlay = Integer.parseInt(scanner.nextLine().trim());
                if (questionsToPlay <= 0 || questionsToPlay > max) {
                    System.out.println("❌ Please enter a number between 1 and " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number!");
            }
        }

        System.out.println("🚀 Great! You'll play " + questionsToPlay + " questions. Let's start!\n");

        // Track used questions to avoid repetition
        List<Integer> availableQuestions = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            availableQuestions.add(i);
        }

        Map<Integer, String> answersMap = new HashMap<>();

        // Read answers into map
        try (BufferedReader answerReader = new BufferedReader(new FileReader(answersFile))) {
            String line;
            while ((line = answerReader.readLine()) != null) {
                if (line.matches("^\\d+\\.\\s.*")) {
                    int dotIndex = line.indexOf('.');
                    int qNum = Integer.parseInt(line.substring(0, dotIndex).trim());
                    String answer = line.substring(dotIndex + 1).trim();
                    answersMap.put(qNum, answer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            scanner.close();
            return;
        }

        // Main game loop
        while (totalQuestions < questionsToPlay && !availableQuestions.isEmpty()) {
            // Select a random question from available questions
            int randomIndex = random.nextInt(availableQuestions.size());
            int targetQuestionNumber = availableQuestions.get(randomIndex);

            // Remove the selected question from available questions
            availableQuestions.remove(randomIndex);

            // Show progress
            System.out.println("📋 Question " + (totalQuestions + 1) + " of " + questionsToPlay);
            System.out.println("═".repeat(50));

            // Read the specific question and options
            try (BufferedReader questionReader = new BufferedReader(new FileReader(questionsFile))) {
                String line;
                int currentQuestion = 0;

                while ((line = questionReader.readLine()) != null) {
                    if (line.matches("^\\d+\\.\\s+.*")) {
                        currentQuestion++;
                        if (currentQuestion == targetQuestionNumber) {
                            System.out.println("\n" + line); // Print question
                            for (int i = 0; i < 4; i++) { // Print 4 options
                                line = questionReader.readLine();
                                if (line != null) {
                                    System.out.println(line);
                                }
                            }
                            String correctAnswer = answersMap.get(targetQuestionNumber);

                            // Get user's answer
                            System.out.print("\nEnter your answer (A, B, C, or D): ");
                            String userAnswer = scanner.nextLine().trim().toUpperCase();
                            totalQuestions++;

                            // Check if the answer is correct
                            if (correctAnswer != null && correctAnswer.toUpperCase().contains(userAnswer)) {
                                correctAnswers++;
                            } else {
                                System.out.println("\n❌ Sorry, that's incorrect.");
                                System.out.println("The correct answer is: "
                                        + (correctAnswer != null ? correctAnswer : "[Not found]"));
                                wrongAnswers++;
                            }

                            break;
                        } else {
                            // Skip 4 option lines for other questions
                            for (int i = 0; i < 4; i++) {
                                questionReader.readLine();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Display final score
        System.out.println("\n\n");
        System.out.println("╔" + "═".repeat(60) + "╗");
        System.out.println("║" + " ".repeat(18) + "🏆 FINAL QUIZ RESULTS 🏆" + " ".repeat(18) + "║");
        System.out.println("╚" + "═".repeat(60) + "╝");
        System.out.println();

        if (totalQuestions > 0) {
            // Score display with visual bars - properly aligned like performance section
            System.out.println("┌─────────────────── SCORE SUMMARY ────────────────────┐");
            System.out.println("│                                                      │");
            System.out.println("│             📊 Total Questions Attempted: " + String.format("%-2d", totalQuestions)
                    + "         │");
            System.out.println("│             ✅ Correct Answers:           " + String.format("%-2d", correctAnswers)
                    + "         │");
            System.out.println(
                    "│             ❌ Wrong Answers:             " + String.format("%-2d", wrongAnswers) + "         │");
            System.out.println("│                                                      │");

            double percentage = (double) correctAnswers / totalQuestions * 100;
            System.out.println(
                    "│             📈 Score Percentage:          " + String.format("%.1f", percentage) + "%      │");

            // Visual progress bar
            int barLength = 30;
            int filledLength = (int) (percentage / 100 * barLength);
            String progressBar = "█".repeat(filledLength) + "░".repeat(barLength - filledLength);
            System.out.println("│                                                      │");
            System.out.println("│        Progress: [" + progressBar + "]    │");
            System.out.println("│                                                      │");
            System.out.println("└──────────────────────────────────────────────────────┘");
            System.out.println();

            // Congratulations and grade based on percentage
            if (percentage >= 90) {
                System.out.println("┌─────────────────── PERFORMANCE ──────────────────────┐");
                System.out.println("│           🎉🎉🎉 CONGRATULATIONS! 🎉🎉🎉             │");
                System.out.println("│                                                      │");
                System.out.println("│              🌟 Grade: A+ - OUTSTANDING! 🌟          │");
                System.out.println("│                  🥇 QUIZ CHAMPION! 🥇                │");
                System.out.println("│                                                      │");
                System.out.println("│           🎊 Absolutely phenomenal work! 🎊          │");
                System.out.println("│                                                      │");
                System.out.println("└──────────────────────────────────────────────────────┘");
            } else if (percentage >= 80) {
                System.out.println("┌─────────────────── PERFORMANCE ──────────────────────┐");
                System.out.println("│               🎊🎊 WELL DONE! 🎊🎊                   │");
                System.out.println("│                                                      │");
                System.out.println("│               ⭐ Grade: A - EXCELLENT! ⭐             │");
                System.out.println("│                 🏆 Great Performance! 🏆             │");
                System.out.println("│                                                      │");
                System.out.println("│              👏 Keep up the great work! 👏           │");
                System.out.println("│                                                      │");
                System.out.println("└──────────────────────────────────────────────────────┘");
            } else if (percentage >= 70) {
                System.out.println("┌─────────────────── PERFORMANCE ──────────────────────┐");
                System.out.println("│                   👏 GOOD JOB! 👏                    │");
                System.out.println("│                                                      │");
                System.out.println("│                🎯 Grade: B - GOOD! 🎯                │");
                System.out.println("│                 💪 Nice Progress! 💪                 │");
                System.out.println("│                                                      │");
                System.out.println("│               🌟 You're doing well! 🌟               │");
                System.out.println("│                                                      │");
                System.out.println("└──────────────────────────────────────────────────────┘");
            } else if (percentage >= 60) {
                System.out.println("┌─────────────────── PERFORMANCE ──────────────────────┐");
                System.out.println("│                  👍 KEEP GOING! 👍                   │");
                System.out.println("│                                                      │");
                System.out.println("│               📝 Grade: C - FAIR! 📝                 │");
                System.out.println("│                🌱 Keep Practicing! 🌱                │");
                System.out.println("│                                                      │");
                System.out.println("│              💡 You're improving! 💡                 │");
                System.out.println("│                                                      │");
                System.out.println("└──────────────────────────────────────────────────────┘");
            } else {
                System.out.println("┌─────────────────── PERFORMANCE ──────────────────────┐");
                System.out.println("│                📚 DON'T GIVE UP! 📚                  │");
                System.out.println("│                                                      │");
                System.out.println("│               📖 Grade: D - STUDY MORE! 📖           │");
                System.out.println("│               🔥 Practice Makes Perfect! 🔥          │");
                System.out.println("│                                                      │");
                System.out.println("│              🚀 Keep learning and grow! 🚀           │");
                System.out.println("│                                                      │");
                System.out.println("└──────────────────────────────────────────────────────┘");
            }
        } else {
            System.out.println("┌──────────────────────────────────────────────────────┐");
            System.out.println("│                                                      │");
            System.out.println("│              😔 No questions attempted.              │");
            System.out.println("│              💭 Better luck next time!               │");
            System.out.println("│                                                      │");
            System.out.println("└──────────────────────────────────────────────────────┘");
        }

        System.out.println();
        System.out.println("╔" + "═".repeat(60) + "╗");
        System.out.println("║" + " ".repeat(12) + "🎮 Thanks for playing! Come back soon! 🎮" + " ".repeat(12) + "║");
        System.out.println("╚" + "═".repeat(60) + "╝");
        System.out.println();

        scanner.close();
    }
}
