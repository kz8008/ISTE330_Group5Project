// Group 5 - 2025-11-17
// PresentationLayer - final (menu)

import java.util.*;
import java.io.*;

public class PresentationLayer {
    private static final Scanner scanner = new Scanner(System.in);
    private static final MainDataLayer dl = new MainDataLayer();
    private static int currentAccountId = -1;
    private static String currentRole = null;

    public static void main(String[] args) {
        System.out.println("=== Research Collaboration CLI (Group 5) ===");

        if (!dl.connect()) {
            System.out.println("Could not connect to DB. Exiting.");
            return;
        }

        while (true) {
            showMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": login(); break;
                case "2": registerMenu(); break;
                case "3":
                    if (confirmExit()) { dl.close(); System.out.println("Goodbye."); return; }
                    break;
                default: System.out.println("Invalid choice."); break;
            }
        }
    }

    // -------------------------
    // Main Menu
    // 1 Login
    // 2 Register
    // 3 Exit Program
    // -------------------------
    private static void showMainMenu() {
        System.out.println("\nMAIN MENU");
        System.out.println("1) Login");
        System.out.println("2) Register");
        System.out.println("3) Exit Program");
        System.out.print("Enter choice: ");
    }

    // -------------------------
    // Register Menu (numeric role choice, with Back and Exit)
    // 1 Professor
    // 2 Student
    // 3 Public User
    // 4 Back
    // 5 Exit Program
    // -------------------------
    private static void registerMenu() {
        while (true) {
            System.out.println("\n=== REGISTER ===");
            System.out.println("1) Register Professor");
            System.out.println("2) Register Student");
            System.out.println("3) Register Public User");
            System.out.println("4) Back");
            System.out.println("5) Exit Program");
            System.out.print("Enter choice: ");
            String c = scanner.nextLine().trim();
            switch (c) {
                case "1": registerProfessor(); break;
                case "2": registerStudent(); break;
                case "3": registerPublicUser(); break;
                case "4": return;
                case "5":
                    if (confirmExit()) { dl.close(); System.out.println("Goodbye."); System.exit(0); }
                    break;
                default: System.out.println("Invalid."); break;
            }
        }
    }

    private static void registerProfessor() {
        System.out.println("\n--- Register Professor ---");
        System.out.print("Username: "); String user = scanner.nextLine().trim();
        System.out.print("Password: "); String pass = scanner.nextLine().trim();
        int accountId = dl.registerAccount(user, pass, "Professor");
        if (accountId <= 0) { System.out.println("Could not create account."); return; }
        System.out.println("Account created. AccountID: " + accountId);

        System.out.print("First name: "); String fn = scanner.nextLine().trim();
        System.out.print("Last name: "); String ln = scanner.nextLine().trim();
        System.out.print("Building code: "); String b = scanner.nextLine().trim();
        System.out.print("Office number: "); String o = scanner.nextLine().trim();
        System.out.print("Email: "); String e = scanner.nextLine().trim();
        System.out.print("Phone: "); String ph = scanner.nextLine().trim();

        int res = dl.addProfessor(0, fn, ln, b, o, e, ph, accountId);
        if (res > 0) System.out.println("Professor created (id/result): " + res);
        else System.out.println("Failed to create professor profile.");
    }

    private static void registerStudent() {
        System.out.println("\n--- Register Student ---");
        System.out.print("Username: "); String user = scanner.nextLine().trim();
        System.out.print("Password: "); String pass = scanner.nextLine().trim();
        int accountId = dl.registerAccount(user, pass, "Student");
        if (accountId <= 0) { System.out.println("Could not create account."); return; }
        System.out.println("Account created. AccountID: " + accountId);

        System.out.print("First name: "); String fn = scanner.nextLine().trim();
        System.out.print("Last name: "); String ln = scanner.nextLine().trim();
        System.out.print("Major: "); String major = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.print("Phone: "); String phone = scanner.nextLine().trim();

        int sid = dl.addStudent(0, fn, ln, major, email, phone, accountId);
        if (sid > 0) System.out.println("Student created id: " + sid);
        else System.out.println("Failed to create student profile.");
    }

    private static void registerPublicUser() {
        System.out.println("\n--- Register Public User ---");
        System.out.print("Username: "); String user = scanner.nextLine().trim();
        System.out.print("Password: "); String pass = scanner.nextLine().trim();
        int accountId = dl.registerAccount(user, pass, "Public");
        if (accountId <= 0) { System.out.println("Could not create account."); return; }
        System.out.println("Account created. AccountID: " + accountId);

        System.out.print("First name: "); String fn = scanner.nextLine().trim();
        System.out.print("Last name: "); String ln = scanner.nextLine().trim();
        System.out.print("Organization: "); String org = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();

        int pid = dl.addPublicUser(fn, ln, org, email, accountId);
        if (pid > 0) System.out.println("Public user created id: " + pid);
        else System.out.println("Failed to create public profile.");
    }

    // -------------------------
    // LOGIN - username then password prompts separately
    // -------------------------
    private static void login() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Username: "); String username = scanner.nextLine().trim();
        System.out.print("Password: "); String password = scanner.nextLine().trim();

        int aid = dl.authenticateAccount(username, password);
        if (aid <= 0) { System.out.println("Login failed. Check credentials."); return; }

        currentAccountId = aid;
        currentRole = dl.getRoleByAccountId(aid);
        if (currentRole == null) { System.out.println("Could not determine role."); return; }

        System.out.println("Login successful. Role: " + currentRole);

        switch (currentRole) {
            case "Professor": professorMenu(); break;
            case "Student": studentMenu(); break;
            case "Public": publicMenu(); break;
            default: System.out.println("Unknown role."); break;
        }
    }

    // -------------------------
    // Professor Menu (numeric options)
    // 1 Add Abstract
    // 2 Update Abstract
    // 3 Delete Abstract
    // 4 Add Keyword
    // 5 Search Students by Interest
    // 6 Back
    // 7 Exit Program
    // -------------------------
    private static void professorMenu() {
        int profId = dl.findProfessorIdByAccountId(currentAccountId);
        if (profId < 0) {
            System.out.println("Professor profile not found for this account.");
            return;
        }

        while (true) {
            System.out.println("\n=== PROFESSOR MENU ===");
            System.out.println("1) Add Abstract");
            System.out.println("2) Update Abstract");
            System.out.println("3) Delete Abstract");
            System.out.println("4) Add Keyword");
            System.out.println("5) Search Students by Interest");
            System.out.println("6) Back");
            System.out.println("7) Exit Program");
            System.out.print("Enter choice: ");
            String c = scanner.nextLine().trim();

            switch (c) {
                case "1":
                    System.out.print("Title: "); String title = scanner.nextLine();
                    System.out.print("Abstract text: "); String text = scanner.nextLine();
                    System.out.print("File path (optional): "); String fp = scanner.nextLine().trim();
                    int aId = dl.addAbstract(0, title, text, fp.isEmpty() ? null : fp);
                    if (aId > 0) {
                        dl.addProfessorAbstract(profId, aId, "Author");
                        System.out.println("Abstract added with id: " + aId);
                    } else System.out.println("Failed to add abstract.");
                    break;

                case "2":
                    System.out.print("Abstract ID to update: ");
                    String upIdS = scanner.nextLine().trim();
                    System.out.print("New Title: "); String newTitle = scanner.nextLine();
                    System.out.print("New Text: "); String newText = scanner.nextLine();
                    try {
                        int upId = Integer.parseInt(upIdS);
                        int res = dl.updateAbstract(upId, newTitle, newText);
                        System.out.println(res > 0 ? "Abstract updated." : "No update performed.");
                    } catch (NumberFormatException nfe) { System.out.println("Invalid ID."); }
                    break;

                case "3":
                    System.out.print("Abstract ID to delete: ");
                    String delIdS = scanner.nextLine().trim();
                    try {
                        int delId = Integer.parseInt(delIdS);
                        int res = dl.deleteAbstract(delId);
                        System.out.println(res > 0 ? "Abstract deleted." : "No deletion performed.");
                    } catch (NumberFormatException nfe) { System.out.println("Invalid ID."); }
                    break;

                case "4":
                    System.out.print("Keyword to add: ");
                    String kw = scanner.nextLine().trim().toLowerCase();
                    int kwId = dl.ensureKeyword(kw);
                    if (kwId > 0) dl.addProfessorKeyword(profId, kwId);
                    System.out.println("Keyword added/linked.");
                    break;

                case "5":
                    System.out.print("Enter interest to search students for (exact match): ");
                    String interest = scanner.nextLine().trim();
                    // Expecting MainDataLayer to have a searchStudentByInterest method that returns List<String>
                    try {
                        // try to call a List-returning method
                        List<String> students = dl.searchStudentByInterest(interest);
                        if (students == null || students.isEmpty()) System.out.println("No students found.");
                        else students.forEach(System.out::println);
                    } catch (NoSuchMethodError | AbstractMethodError ex) {
                        System.out.println("Search by student interest not implemented in data layer.");
                    } catch (Exception ex) {
                        // If MainDataLayer returns ResultSet instead, attempt a fallback via reflection (best-effort)
                        System.out.println("Error searching students: " + ex.getMessage());
                    }
                    break;

                case "6": return;
                case "7":
                    if (confirmExit()) { dl.close(); System.out.println("Goodbye."); System.exit(0); }
                    break;
                default: System.out.println("Invalid."); break;
            }
        }
    }

    // -------------------------
    // Student Menu
    // 1 Add Student Keyword
    // 2 Delete Student Keyword
    // 3 View All Faculty Abstracts
    // 4 Find Matching Professors
    // 5 Search Faculty by Keyword
    // 6 Back
    // 7 Exit Program
    // -------------------------
    private static void studentMenu() {
        int stuId = dl.findStudentIdByAccountId(currentAccountId);
        if (stuId < 0) {
            System.out.println("Student profile not found for this account.");
            return;
        }
        while (true) {
            System.out.println("\n=== STUDENT MENU ===");
            System.out.println("1) Add Student Keyword");
            System.out.println("2) Delete Student Keyword");
            System.out.println("3) View All Faculty Abstracts");
            System.out.println("4) Find Matching Professors");
            System.out.println("5) Search Faculty by Keyword");
            System.out.println("6) Back");
            System.out.println("7) Exit Program");
            System.out.print("Enter choice: ");
            String c = scanner.nextLine().trim();

            switch (c) {
                case "1":
                    System.out.print("Enter topic (1-3 words): ");
                    String topic = scanner.nextLine().trim().toLowerCase();
                    int kid = dl.ensureKeyword(topic);
                    if (kid > 0) dl.addStudentKeyword(stuId, kid);
                    System.out.println("Topic added.");
                    break;

                case "2":
                      System.out.print("Enter keyword to remove (exact text): ");
                      String toRemove = scanner.nextLine().trim().toLowerCase();
                      int rid = dl.ensureKeyword(toRemove);
                  
                      if (rid > 0) {
                          try {
                              dl.deleteStudentKeyword(stuId, rid);
                              System.out.println("Keyword removed from your profile.");
                          } catch (Exception e) {
                              System.out.println("Delete keyword not implemented in data layer.");
                          }
                      } else {
                          System.out.println("Keyword not found.");
                      }
                      break;
   
                case "3":
                    List<String> all = dl.listAllAbstracts();
                    if (all.isEmpty()) System.out.println("No abstracts available.");
                    else all.forEach(System.out::println);
                    break;

                case "4":
                    List<String> matches = dl.findMatchingFaculty(stuId);
                    if (matches.isEmpty()) System.out.println("No matches found.");
                    else matches.forEach(System.out::println);
                    break;

                case "5":
                    System.out.print("Enter keyword to search faculty: ");
                    String key = scanner.nextLine().trim();
                    List<String> profs = dl.searchFacultyByKeyword(key);
                    if (profs.isEmpty()) System.out.println("No professors found.");
                    else profs.forEach(System.out::println);
                    break;

                case "6": return;
                case "7":
                    if (confirmExit()) { dl.close(); System.out.println("Goodbye."); System.exit(0); }
                    break;
                default: System.out.println("Invalid."); break;
            }
        }
    }

    // -------------------------
    // Public Menu
    // 1 Search by Keyword
    // 2 Back
    // 3 Exit Program
    // -------------------------
    private static void publicMenu() {
        while (true) {
            System.out.println("\n=== PUBLIC USER MENU ===");
            System.out.println("1) Search Professors by Keyword");
            System.out.println("2) Back");
            System.out.println("3) Exit Program");
            System.out.print("Enter choice: ");
            String c = scanner.nextLine().trim();

            switch (c) {
                case "1":
                    System.out.print("Enter keyword: ");
                    String kw = scanner.nextLine().trim();
                    List<String> profs = dl.searchFacultyByKeyword(kw);
                    if (profs.isEmpty()) System.out.println("No professors found.");
                    else profs.forEach(System.out::println);
                    break;
                case "2": return;
                case "3":
                    if (confirmExit()) { dl.close(); System.out.println("Goodbye."); System.exit(0); }
                    break;
                default: System.out.println("Invalid."); break;
            }
        }
    }

    // -------------------------
    // Exit confirmation helper
    // -------------------------
    private static boolean confirmExit() {
        System.out.print("Are you sure you want to exit? (y/n): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        return ans.equals("y") || ans.equals("yes");
    }
}
