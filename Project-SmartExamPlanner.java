import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

/**
 * SMART EXAM PLANNER
 * A console-based Java application that helps students plan, track and
 * manage examination preparation. All exam records are persisted in a
 * MySQL relational database via JDBC (no in-memory-only storage), so
 * data survives across program runs.
 *
 * Department : Computer Science and Engineering
 * Author     : Shaik Malika Masarrath (Reg. No. 410625104157)
 */
public class SmartExamPlanner1 {

    // Single shared Scanner for all console input across the program
    private static final Scanner scanner = new Scanner(System.in);

    // Date pattern used for all exam-date prompts (YYYY-MM-DD)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // JDBC connection shared by the whole application
    private static Connection connection;

    // ---- MySQL connection configuration ----
    // Update these three values to match your local MySQL setup.
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/smart_exam_planner?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "your_password";

    public static void main(String[] args) {

        initDatabase();

        boolean running = true;

        // Main menu loop — keeps prompting until user chooses Exit (11)
        while (running) {
            printHeader("SMART EXAM PLANNER - MAIN MENU");

            System.out.println("1. Add Examination");
            System.out.println("2. View All Examinations");
            System.out.println("3. View Upcoming Examination");
            System.out.println("4. Study Plan Generator");
            System.out.println("5. Preparation Tracker");
            System.out.println("6. Update Examination");
            System.out.println("7. Delete Examination");
            System.out.println("8. Search Examination");
            System.out.println("9. Sort by Exam Date");
            System.out.println("10. Exam Statistics");
            System.out.println("11. Exit");

            int choice = readInt("\nEnter your choice: ");

            switch (choice) {
                case 1 -> addExam();
                case 2 -> viewAllExams();
                case 3 -> viewUpcomingExam();
                case 4 -> studyPlan();
                case 5 -> preparationTracker();
                case 6 -> updateExam();
                case 7 -> deleteExam();
                case 8 -> searchExam();
                case 9 -> sortExams();
                case 10 -> statistics();
                case 11 -> {
                    running = false;
                    System.out.println("\nThank you for using Smart Exam Planner!");
                }
                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }

        closeDatabase();
        scanner.close();
    }

    /**
     * Opens the MySQL connection and creates the EXAMS table if absent.
     * The whole application depends on this single relational table.
     */
    private static void initDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = connection.createStatement();

            String createExams = "CREATE TABLE IF NOT EXISTS EXAMS (" +
                    "exam_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "subject VARCHAR(100) NOT NULL, " +
                    "exam_date DATE NOT NULL, " +
                    "total_chapters INT NOT NULL, " +
                    "completed_chapters INT NOT NULL, " +
                    "study_hours DOUBLE NOT NULL, " +
                    "priority VARCHAR(10) NOT NULL, " +
                    "completed BOOLEAN NOT NULL DEFAULT FALSE" +
                    ")";
            stmt.execute(createExams);

            System.out.println("[SYSTEM] Connected to MySQL. Database ready.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Could not connect to MySQL: " + e.getMessage());
            System.out.println("Check DB_URL / DB_USER / DB_PASS in SmartExamPlanner.java, then restart.");
            System.exit(1);
        }
    }

    private static void closeDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("[WARN] Error while closing DB connection: " + e.getMessage());
        }
    }

    // Collects all fields for a new exam and inserts it into MySQL
    private static void addExam() {
        printHeader("ADD EXAMINATION");

        String subject = readText("Enter subject name: ");
        LocalDate date = readDate("Enter exam date (YYYY-MM-DD): ");
        int totalChapters = readPositiveInt("Enter total number of chapters: ");
        double studyHours = readNonNegativeDouble("Enter total study hours required: ");
        String priority = readPriority();
        int completedChapters = readChapterProgress(totalChapters);
        boolean completed = completedChapters == totalChapters;

        String sql = "INSERT INTO EXAMS (subject, exam_date, total_chapters, completed_chapters, " +
                "study_hours, priority, completed) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, subject);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setInt(3, totalChapters);
            pstmt.setInt(4, completedChapters);
            pstmt.setDouble(5, studyHours);
            pstmt.setString(6, priority);
            pstmt.setBoolean(7, completed);
            pstmt.executeUpdate();

            System.out.println("\nExamination added successfully!");
        } catch (SQLException e) {
            System.out.println("[ERROR] Insert failed: " + e.getMessage());
        }
    }

    // Prints every stored exam with its full details
    private static void viewAllExams() {
        printHeader("ALL EXAMINATIONS");

        String sql = "SELECT * FROM EXAMS";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean any = false;
            int i = 1;
            while (rs.next()) {
                any = true;
                System.out.println("\nExam " + (i++));
                displayExam(rs);
            }
            if (!any) {
                System.out.println("No examination records found.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Fetch failed: " + e.getMessage());
        }
    }

    // Finds the nearest not-yet-completed exam that hasn't passed today
    private static void viewUpcomingExam() {
        printHeader("UPCOMING EXAMINATION");

        String sql = "SELECT * FROM EXAMS WHERE completed = FALSE AND exam_date >= CURDATE() " +
                "ORDER BY exam_date ASC LIMIT 1";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                displayExam(rs);
            } else {
                System.out.println("No upcoming pending examination found.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Query failed: " + e.getMessage());
        }
    }

    // Computes a per-day chapter/study-hour target based on days remaining
    private static void studyPlan() {
        printHeader("STUDY PLAN GENERATOR");

        ResultSet rs = findExamBySubject();
        if (rs == null) return;

        try {
            LocalDate examDate = rs.getDate("exam_date").toLocalDate();
            int totalChapters = rs.getInt("total_chapters");
            int completedChapters = rs.getInt("completed_chapters");
            double studyHours = rs.getDouble("study_hours");
            String priority = rs.getString("priority");
            String subject = rs.getString("subject");

            long days = ChronoUnit.DAYS.between(LocalDate.now(), examDate);
            int remainingChapters = totalChapters - completedChapters;

            if (days <= 0) {
                days = 1;
            }

            double dailyChapters = (double) remainingChapters / days;
            double dailyHours = studyHours / days;

            System.out.println("\nSubject              : " + subject);
            System.out.println("Exam Date            : " + examDate);
            System.out.println("Days Remaining       : " + days + " days");
            System.out.println("Remaining Chapters   : " + remainingChapters);
            System.out.printf("Daily Chapter Target : %.2f chapters/day%n", dailyChapters);
            System.out.println("Study Hours Required : " + studyHours);
            System.out.printf("Daily Study Target   : %.2f hours/day%n", dailyHours);
            System.out.println("Priority             : " + priority);
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } finally {
            closeSilently(rs);
        }
    }

    // Shows completion percentage and status label for one exam
    private static void preparationTracker() {
        printHeader("PREPARATION TRACKER");

        ResultSet rs = findExamBySubject();
        if (rs == null) return;

        try {
            String subject = rs.getString("subject");
            int totalChapters = rs.getInt("total_chapters");
            int completedChapters = rs.getInt("completed_chapters");
            double progress = (double) completedChapters / totalChapters * 100;

            System.out.println("\nSubject            : " + subject);
            System.out.println("Completed Chapters : " + completedChapters);
            System.out.println("Total Chapters     : " + totalChapters);
            System.out.printf("Progress           : %.2f%%%n", progress);

            if (completedChapters == totalChapters) {
                System.out.println("Status             : Completed");
            } else if (completedChapters == 0) {
                System.out.println("Status             : Not Started");
            } else {
                System.out.println("Status             : In Progress");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } finally {
            closeSilently(rs);
        }
    }

    // Overwrites an existing exam's details in MySQL (subject stays fixed)
    private static void updateExam() {
        printHeader("UPDATE EXAMINATION");

        ResultSet rs = findExamBySubject();
        if (rs == null) return;

        try {
            int examId = rs.getInt("exam_id");
            closeSilently(rs);

            System.out.println("\nEnter new details:");
            LocalDate date = readDate("Enter new exam date (YYYY-MM-DD): ");
            int totalChapters = readPositiveInt("Enter new total chapters: ");
            double studyHours = readNonNegativeDouble("Enter new study hours: ");
            String priority = readPriority();
            int completedChapters = readChapterProgress(totalChapters);
            boolean completed = completedChapters == totalChapters;

            String sql = "UPDATE EXAMS SET exam_date = ?, total_chapters = ?, completed_chapters = ?, " +
                    "study_hours = ?, priority = ?, completed = ? WHERE exam_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setDate(1, Date.valueOf(date));
                pstmt.setInt(2, totalChapters);
                pstmt.setInt(3, completedChapters);
                pstmt.setDouble(4, studyHours);
                pstmt.setString(5, priority);
                pstmt.setBoolean(6, completed);
                pstmt.setInt(7, examId);
                pstmt.executeUpdate();
                System.out.println("\nExamination updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Update failed: " + e.getMessage());
        }
    }

    // Removes an exam row from MySQL after locating it by subject
    private static void deleteExam() {
        printHeader("DELETE EXAMINATION");

        ResultSet rs = findExamBySubject();
        if (rs == null) return;

        try {
            int examId = rs.getInt("exam_id");
            closeSilently(rs);

            String sql = "DELETE FROM EXAMS WHERE exam_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, examId);
                pstmt.executeUpdate();
                System.out.println("\nExamination deleted successfully!");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Delete failed: " + e.getMessage());
        }
    }

    // Case-insensitive partial match search on subject name
    private static void searchExam() {
        printHeader("SEARCH EXAMINATION");

        String search = readText("Enter subject name to search: ");
        String sql = "SELECT * FROM EXAMS WHERE LOWER(subject) LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + search.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                displayExam(rs);
                found = true;
            }
            if (!found) {
                System.out.println("\nNo matching examination found.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Search failed: " + e.getMessage());
        }
    }

    // Sorts and lists exams in ascending exam-date order via SQL ORDER BY
    private static void sortExams() {
        printHeader("SORT EXAMINATIONS");

        String sql = "SELECT * FROM EXAMS ORDER BY exam_date ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Examinations sorted successfully by date.");
            int i = 1;
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.println("\nExam " + (i++));
                displayExam(rs);
            }
            if (!any) {
                System.out.println("No examination records found.");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Sort failed: " + e.getMessage());
        }
    }

    // Aggregates counts and average progress across all exams using SQL
    private static void statistics() {
        printHeader("EXAM STATISTICS");

        String countSql = "SELECT COUNT(*) AS total, " +
                "SUM(CASE WHEN completed = TRUE THEN 1 ELSE 0 END) AS completed_count " +
                "FROM EXAMS";
        String progressSql = "SELECT completed_chapters, total_chapters FROM EXAMS";

        try (Statement stmt = connection.createStatement()) {

            ResultSet countRs = stmt.executeQuery(countSql);
            int total = 0, completed = 0;
            if (countRs.next()) {
                total = countRs.getInt("total");
                completed = countRs.getInt("completed_count");
            }
            countRs.close();

            if (total == 0) {
                System.out.println("No examination records found.");
                return;
            }

            ResultSet progRs = stmt.executeQuery(progressSql);
            double totalProgress = 0;
            while (progRs.next()) {
                int c = progRs.getInt("completed_chapters");
                int t = progRs.getInt("total_chapters");
                totalProgress += (double) c / t * 100;
            }
            progRs.close();

            int pending = total - completed;
            double averageProgress = totalProgress / total;

            System.out.println("Total Examinations     : " + total);
            System.out.println("Completed Examinations : " + completed);
            System.out.println("Pending Examinations   : " + pending);
            System.out.printf("Average Preparation    : %.2f%%%n", averageProgress);
        } catch (SQLException e) {
            System.out.println("[ERROR] Statistics query failed: " + e.getMessage());
        }
    }

    // Shared lookup helper: prompts for a subject, returns a live ResultSet
    // positioned on the first matching row, or null if not found.
    private static ResultSet findExamBySubject() {
        String subject = readText("Enter subject name: ");
        String sql = "SELECT * FROM EXAMS WHERE LOWER(subject) = LOWER(?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, subject);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs;
            }
            System.out.println("Examination not found.");
            rs.close();
            return null;
        } catch (SQLException e) {
            System.out.println("[ERROR] Lookup failed: " + e.getMessage());
            return null;
        }
    }

    // Prints a full detail block for one exam row from an open ResultSet
    private static void displayExam(ResultSet rs) {
        try {
            String subject = rs.getString("subject");
            LocalDate date = rs.getDate("exam_date").toLocalDate();
            int totalChapters = rs.getInt("total_chapters");
            int completedChapters = rs.getInt("completed_chapters");
            double studyHours = rs.getDouble("study_hours");
            String priority = rs.getString("priority");
            boolean completed = rs.getBoolean("completed");

            long days = ChronoUnit.DAYS.between(LocalDate.now(), date);
            double progress = (double) completedChapters / totalChapters * 100;

            System.out.println("--------------------------------");
            System.out.println("Subject          : " + subject);
            System.out.println("Exam Date        : " + date);
            System.out.println("Total Chapters   : " + totalChapters);
            System.out.println("Completed        : " + completedChapters);
            System.out.println("Study Hours      : " + studyHours);
            System.out.println("Priority         : " + priority);
            System.out.printf("Progress         : %.2f%%%n", progress);
            System.out.println("Days Remaining   : " + days);
            System.out.println("Status           : " + (completed ? "Completed" : "Pending"));
            System.out.println("--------------------------------");
        } catch (SQLException e) {
            System.out.println("[ERROR] Could not display exam: " + e.getMessage());
        }
    }

    private static void closeSilently(ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException ignored) {
        }
    }

    // Keeps re-prompting until a non-empty string is entered
    private static String readText(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    // Keeps re-prompting until a parseable integer is entered
    private static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // Wraps readInt() with a "> 0" validation rule
    private static int readPositiveInt(String message) {
        while (true) {
            int value = readInt(message);

            if (value > 0) {
                return value;
            }

            System.out.println("Value must be greater than zero.");
        }
    }

    // Keeps re-prompting until a parseable, non-negative double is entered
    private static double readNonNegativeDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                double value = Double.parseDouble(scanner.nextLine().trim());

                if (value >= 0) {
                    return value;
                }

                System.out.println("Value cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Validates completed chapters fall within [0, totalChapters]
    private static int readChapterProgress(int totalChapters) {
        while (true) {
            int completed = readInt("Enter completed chapters: ");

            if (completed >= 0 && completed <= totalChapters) {
                return completed;
            }

            System.out.println("Completed chapters must be between 0 and " + totalChapters + ".");
        }
    }

    // Restricts priority input to exactly High / Medium / Low (case-insensitive)
    private static String readPriority() {
        while (true) {
            String priority = readText("Enter priority (High/Medium/Low): ");

            if (priority.equalsIgnoreCase("High")
                    || priority.equalsIgnoreCase("Medium")
                    || priority.equalsIgnoreCase("Low")) {
                return priority;
            }

            System.out.println("Enter High, Medium, or Low.");
        }
    }

    // Parses a date string using FORMATTER, re-prompting on invalid input
    private static LocalDate readDate(String message) {
        while (true) {
            try {
                System.out.print(message);
                return LocalDate.parse(scanner.nextLine().trim(), FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use YYYY-MM-DD format.");
            }
        }
    }

    // Simple decorative section header printed before each menu/screen
    private static void printHeader(String title) {
        System.out.println("\n======================================");
        System.out.println("       " + title);
        System.out.println("======================================");
    }
}
