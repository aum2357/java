
/*
 * Online Quiz Platform – Result Processor
 * 
 * Scenario: A quiz platform processes user answers and compares them to correct answers.
 * 
 * Problem Requirements:
 * • Use String[] for correct answers and user answers.
 * • Use methods to compare answers, calculate scores, and return grade.
 * • Store scores of multiple users in a List<Integer>.
 * • Validate input (length mismatch) and throw an InvalidQuizSubmissionException.
 * 
 * Features:
 * - Interactive quiz with random question selection
 * - No question repetition during a session
 * - Beautiful formatted score display with progress bars
 * - Grade-based performance feedback with emojis
 * - User-defined number of questions to attempt
 * 
 * @author Quiz Platform Team
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main class for the Quiz Platform application.
 * Handles quiz game logic, user interaction, and score calculation.
 */
public class QuizPlatform {

    /**
     * Main method - Entry point of the quiz application.
     * Manages the complete quiz flow from user input to final results.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize scanner for user input
        Scanner scanner = new Scanner(System.in);
        
        // Define question range (1-20 available questions)
        int min = 1;
        int max = 20;
        Random random = new Random();
        
        // File paths for questions and answers
        String questionsFile = "questions.txt";
        String answersFile = "answers.txt";

        // Game statistics tracking
        int correctAnswers = 0;      // Count of correct answers
        int wrongAnswers = 0;        // Count of incorrect answers  
        int totalQuestions = 0;      // Total questions attempted
        int questionsToPlay = 0;     // User-selected number of questions

        // ============================================================================
        // USER INPUT SECTION: Get number of questions to play
        // ============================================================================
        System.out.println("🎯 Welcome to the Java Quiz Platform! 🎯");
        System.out.println("Available questions: 1-" + max);
        
        // Input validation loop - ensure user enters valid number of questions
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

        // ============================================================================
        // INITIALIZATION SECTION: Setup data structures
        // ============================================================================
        
        // Create list of available question numbers to prevent repetition
        List<Integer> availableQuestions = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            availableQuestions.add(i);
        }

        // Map to store question number -> correct answer mapping
        Map<Integer, String> answersMap = new HashMap<>();

        // ============================================================================
        // FILE READING SECTION: Load answers from file
        // ============================================================================
        
        // Read answers from answers.txt and populate the answers map
        // Expected format: "1. A) answer", "2. B) answer", etc.
        try (BufferedReader answerReader = new BufferedReader(new FileReader(answersFile))) {
            String line;
            while ((line = answerReader.readLine()) != null) {
                // Parse lines that start with number followed by dot
                if (line.matches("^\\d+\\.\\s.*")) {
                    int dotIndex = line.indexOf('.');
                    int qNum = Integer.parseInt(line.substring(0, dotIndex).trim());
                    String answer = line.substring(dotIndex + 1).trim();
                    answersMap.put(qNum, answer);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error reading answers file: " + e.getMessage());
            e.printStackTrace();
            scanner.close();
            return;
        }

        // ============================================================================
        // MAIN GAME LOOP: Present questions and collect answers
        // ============================================================================
        
        // Continue until we've asked the desired number of questions or run out
        while (totalQuestions < questionsToPlay && !availableQuestions.isEmpty()) {
            // Randomly select a question from remaining available questions
            int randomIndex = random.nextInt(availableQuestions.size());
            int targetQuestionNumber = availableQuestions.get(randomIndex);

            // Remove selected question to prevent repetition
            availableQuestions.remove(randomIndex);

            // Display progress to user
            System.out.println("📋 Question " + (totalQuestions + 1) + " of " + questionsToPlay);
            System.out.println("═".repeat(50));

            // Read and display the selected question from questions.txt
            try (BufferedReader questionReader = new BufferedReader(new FileReader(questionsFile))) {
                String line;
                int currentQuestion = 0;

                // Parse through questions file to find target question
                while ((line = questionReader.readLine()) != null) {
                    // Check if line starts with question number format
                    if (line.matches("^\\d+\\.\\s+.*")) {
                        currentQuestion++;
                        if (currentQuestion == targetQuestionNumber) {
                            // Display the question
                            System.out.println("\n" + line);
                            
                            // Display the 4 multiple choice options
                            for (int i = 0; i < 4; i++) {
                                line = questionReader.readLine();
                                if (line != null) {
                                    System.out.println(line);
                                }
                            }
                            
                            // Get correct answer for this question
                            String correctAnswer = answersMap.get(targetQuestionNumber);

                            // Collect user's answer
                            System.out.print("\nEnter your answer (A, B, C, or D): ");
                            String userAnswer = scanner.nextLine().trim().toUpperCase();
                            totalQuestions++;

                            // Check if answer is correct and update counters
                            if (correctAnswer != null && correctAnswer.toUpperCase().contains(userAnswer)) {
                                correctAnswers++;
                            } else {
                                // Show correct answer for learning purposes
                                System.out.println("\n❌ Sorry, that's incorrect.");
                                System.out.println("The correct answer is: "
                                        + (correctAnswer != null ? correctAnswer : "[Not found]"));
                                wrongAnswers++;
                            }

                            break;
                        } else {
                            // Skip the 4 option lines for questions we don't want
                            for (int i = 0; i < 4; i++) {
                                questionReader.readLine();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("❌ Error reading questions file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // ============================================================================
        // RESULTS DISPLAY SECTION: Show final score and performance analysis
        // ============================================================================
        
        // Display header with beautiful formatting
        System.out.println("\n\n");
        System.out.println("╔" + "═".repeat(60) + "╗");
        System.out.println("║" + " ".repeat(18) + "🏆 FINAL QUIZ RESULTS 🏆" + " ".repeat(18) + "║");
        System.out.println("╚" + "═".repeat(60) + "╝");
        System.out.println();

        if (totalQuestions > 0) {
            // ========================================================================
            // SCORE SUMMARY: Display statistics in formatted box
            // ========================================================================
            System.out.println("┌─────────────────── SCORE SUMMARY ────────────────────┐");
            System.out.println("│                                                      │");
            System.out.println("│             📊 Total Questions Attempted: " + String.format("%-2d", totalQuestions)
                    + "         │");
            System.out.println("│             ✅ Correct Answers:           " + String.format("%-2d", correctAnswers)
                    + "         │");
            System.out.println(
                    "│             ❌ Wrong Answers:             " + String.format("%-2d", wrongAnswers) + "         │");
            System.out.println("│                                                      │");

            // Calculate percentage score
            double percentage = (double) correctAnswers / totalQuestions * 100;
            System.out.println(
                    "│             📈 Score Percentage:          " + String.format("%.2f", percentage) + "%      │");

            // Create visual progress bar representation
            int barLength = 30;
            int filledLength = (int) (percentage / 100 * barLength);
            String progressBar = "█".repeat(filledLength) + "░".repeat(barLength - filledLength);
            System.out.println("│                                                      │");
            System.out.println("│        Progress: [" + progressBar + "]    │");
            System.out.println("│                                                      │");
            System.out.println("└──────────────────────────────────────────────────────┘");
            System.out.println();

            // ========================================================================
            // PERFORMANCE ANALYSIS: Grade-based feedback with motivational messages
            // ========================================================================
            // Grade A+ (90-100%): Outstanding performance
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
            // Grade A (80-89%): Excellent performance
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
            // Grade B (70-79%): Good performance
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
            // Grade C (60-69%): Fair performance, needs improvement
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
            // Grade D (0-59%): Needs significant improvement
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
        // Handle case where no questions were attempted
        } else {
            System.out.println("┌──────────────────────────────────────────────────────┐");
            System.out.println("│                                                      │");
            System.out.println("│              😔 No questions attempted.              │");
            System.out.println("│              💭 Better luck next time!               │");
            System.out.println("│                                                      │");
            System.out.println("└──────────────────────────────────────────────────────┘");
        }

        // ============================================================================
        // CLEANUP SECTION: Display goodbye message and close resources
        // ============================================================================
        
        // Display farewell message with beautiful formatting
        System.out.println();
        System.out.println("╔" + "═".repeat(60) + "╗");
        System.out.println("║" + " ".repeat(12) + "🎮 Thanks for playing! Come back soon! 🎮" + " ".repeat(12) + "║");
        System.out.println("╚" + "═".repeat(60) + "╝");
        System.out.println();

        // Close scanner to prevent resource leak
        scanner.close();
    }
}
