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
        System.out.println("=== Main Menu ===");

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
    // 3 Exit
    // -------------------------
    private static void showMainMenu() {
        System.out.println("\nMAIN MENU");
        System.out.println("1) Login");
        System.out.println("2) Register");
        System.out.println("3) Exit");
        System.out.print("Enter choice: ");
    }

    // -------------------------
    // Register Menu (numeric role choice, with Back and Exit)
    // 1 Professor
    // 2 Student
    // 3 Public User
    // 4 Back
    // 5 Exit
    // -------------------------
    private static void registerMenu() {
        while (true) {
            System.out.println("\n=== REGISTER ===");
            System.out.println("1) Register Professor");
            System.out.println("2) Register Student");
            System.out.println("3) Register Public User");
            System.out.println("4) Back");
            System.out.println("5) Exit");
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
        

        System.out.print("First name: "); String fn = scanner.nextLine().trim();
        System.out.print("Last name: "); String ln = scanner.nextLine().trim();
        System.out.print("Building code: "); String b = scanner.nextLine().trim();
        System.out.print("Office number: "); String o = scanner.nextLine().trim();
        System.out.print("Email: "); String e = scanner.nextLine().trim();
        System.out.print("Phone(format 555-555-5555): "); String ph = scanner.nextLine().trim();

        int res = dl.addProfessor(0, fn, ln, b, o, e, ph, accountId);
        if (res > 0) System.out.println("Professor account created!");
        else System.out.println("Failed to create professor profile.");
    }

    private static void registerStudent() {
        System.out.println("\n--- Register Student ---");
        System.out.print("Username: "); String user = scanner.nextLine().trim();
        System.out.print("Password: "); String pass = scanner.nextLine().trim();
        int accountId = dl.registerAccount(user, pass, "Student");
        if (accountId <= 0) { System.out.println("Could not create account."); return; }
        

        System.out.print("First name: "); String fn = scanner.nextLine().trim();
        System.out.print("Last name: "); String ln = scanner.nextLine().trim();
        System.out.print("Major: "); String major = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.print("Phone: "); String phone = scanner.nextLine().trim();

        int sid = dl.addStudent(0, fn, ln, major, email, phone, accountId);
        if (sid > 0) System.out.println("Student account created! ");
        else System.out.println("Failed to create student profile.");
    }

    private static void registerPublicUser() {
        System.out.println("\n--- Register Public User ---");
        System.out.print("Username: "); String user = scanner.nextLine().trim();
        System.out.print("Password: "); String pass = scanner.nextLine().trim();
        int accountId = dl.registerAccount(user, pass, "Public");
        if (accountId <= 0) { System.out.println("Could not create account."); return; }
 

        System.out.print("First name: "); String fn = scanner.nextLine().trim();
        System.out.print("Last name: "); String ln = scanner.nextLine().trim();
        System.out.print("Organization: "); String org = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();

        int pid = dl.addPublicUser(fn, ln, org, email, accountId);
        if (pid > 0) System.out.println("Guest account created! ");
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
    // 7 Exit 
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
            System.out.println("4) Add Interests");
            System.out.println("5) Search Students by Interest");
            System.out.println("6) Logout");
            System.out.println("7) Exit");
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
                          boolean res = dl.updateAbstractIfOwned(profId, upId, newTitle, newText);
                          System.out.println(res ? "Abstract updated." : "Unable to update abstract (not yours or invalid ID).");
                      } catch (NumberFormatException nfe) { 
                          System.out.println("Invalid ID."); 
                      }
                      break;



                  case "3":
                      System.out.print("Abstract ID to delete: ");
                      String delIdS = scanner.nextLine().trim();
                      try {
                          int delId = Integer.parseInt(delIdS);
                          boolean res = dl.deleteAbstractIfOwned(profId, delId);
                          System.out.println(res ? "Abstract deleted." : "Unable to delete abstract (not yours or invalid ID).");
                      } catch (NumberFormatException nfe) { 
                          System.out.println("Invalid ID."); 
                      }
                      break;


                case "4":
                    System.out.print("Enter an interest (1-3 words): ");
                    String kw = scanner.nextLine().trim().toLowerCase();
                    int kwId = dl.ensureKeyword(kw);
                    if (kwId > 0) dl.addProfessorKeyword(profId, kwId);
                    System.out.println("Interest added.");
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
    // -------------------------
   private static void studentMenu() {
    int stuId = dl.findStudentIdByAccountId(currentAccountId);
    if (stuId < 0) {
        System.out.println("Student profile not found for this account.");
        return;
    }
    while (true) {
        System.out.println("\n=== STUDENT MENU ===");
        System.out.println("1) Add Your Interests");
        System.out.println("2) Modify Your Interests");
        System.out.println("3) View Your Interests");
        System.out.println("4) View All Professor Abstracts");
        System.out.println("5) Find Professors By Mutual Interests");
        System.out.println("6) Search Professors By Interests");
        System.out.println("7) Search Professors By Abstract Text");
        System.out.println("8) Logout");
        System.out.println("9) Exit");
        System.out.print("Enter choice: ");
        String c = scanner.nextLine().trim();

        switch (c) {

            case "1": // Add interest
                System.out.print("Enter an interest (1-3 words): ");
                String topic = scanner.nextLine().trim().toLowerCase();
                int kid = dl.ensureKeyword(topic);
                if (kid > 0) dl.addStudentKeyword(stuId, kid);
                System.out.println("Interest added.");
                break;

            case "2": // Remove interest
                System.out.print("Enter interest to remove (exact text): ");
                String toRemove = scanner.nextLine().trim().toLowerCase();
                int rid = dl.ensureKeyword(toRemove);
                if (rid > 0) {
                    try {
                        dl.deleteStudentKeyword(stuId, rid);
                        System.out.println("Interest removed from your profile.");
                    } catch (Exception e) {
                        System.out.println("Delete keyword not implemented in data layer.");
                    }
                } else {
                    System.out.println("Interest was not found.");
                }
                break;

            case "3": // View your interests
                List<String> myInterests = dl.getAllStudentInterests(stuId);
                if (myInterests.isEmpty()) System.out.println("You have no interests added.");
                else myInterests.forEach(System.out::println);
                break;

            case "4": // View all abstracts
                List<String> all = dl.listAllAbstracts();
                if (all.isEmpty()) System.out.println("No abstracts available.");
                else all.forEach(System.out::println);
                break;

            case "5": // Find by mutual interests
                List<String> matches = dl.findMatchingFaculty(stuId);
                if (matches.isEmpty()) System.out.println("No matches found.");
                else matches.forEach(System.out::println);
                break;

            case "6": // Search professors by interest keyword
                System.out.print("Enter keyword to search a professor: ");
                String key = scanner.nextLine().trim();
                List<String> profs = dl.searchFacultyByKeyword(key);
                if (profs.isEmpty()) System.out.println("No professors found.");
                else profs.forEach(System.out::println);
                break;

            case "7": // Search professors by abstract text
                System.out.print("Enter text to search abstracts: ");
                String text = scanner.nextLine().trim();
                List<String> absMatches2 = dl.searchProfessorsByAbstractText(text);
                if (absMatches2.isEmpty()) System.out.println("No professors found.");
                else absMatches2.forEach(System.out::println);
                break;

            case "8": // Logout
                return;

            case "9": // Exit
                if (confirmExit()) {
                    dl.close();
                    System.out.println("Goodbye.");
                    System.exit(0);
                }
                break;

            default:
                System.out.println("Invalid.");
                break;
        }
    }
}


    // -------------------------
    // Public Menu
    // -------------------------
    private static void publicMenu() {
    int pubId = dl.findPublicIdByAccountId(currentAccountId);
    if (pubId < 0) {
        System.out.println("Public user profile not found.");
        return;
    }

    while (true) {
        System.out.println("\n=== PUBLIC USER MENU ===");
        System.out.println("1) Add Your Interests");
        System.out.println("2) Modify Your Interests");
        System.out.println("3) View Your Interests");
        System.out.println("4) Search Professors By Interests");
        System.out.println("5) Search Professors By Abstract Text");
        System.out.println("6) View Professor Abstracts (All)");
        System.out.println("7) Logout");
        System.out.println("8) Exit");

        System.out.print("Enter choice: ");
        String c = scanner.nextLine().trim();

        switch (c) {

            case "1": // Add interest
                System.out.print("Enter an interest (1–3 words): ");
                String interest = scanner.nextLine().trim().toLowerCase();
                int k1 = dl.ensureKeyword(interest);
                if (k1 > 0) dl.addPublicKeyword(pubId, k1);
                System.out.println("Interest added.");
                break;

            case "2": // Modify (delete) interest
                System.out.print("Enter the exact interest to remove: ");
                String remove = scanner.nextLine().trim().toLowerCase();
                int k2 = dl.ensureKeyword(remove);
                if (k2 > 0) {
                    dl.deletePublicKeyword(pubId, k2);
                    System.out.println("Interest removed.");
                } else {
                    System.out.println("Interest not found.");
                }
                break;

            case "3": // View interests
                List<String> list = dl.listPublicKeywords(pubId);
                if (list.isEmpty()) System.out.println("You have no interests added.");
                else list.forEach(System.out::println);
                break;

            case "4": // Search professors by interest
                System.out.print("Enter interest: ");
                String kw = scanner.nextLine().trim();
                List<String> profs = dl.searchFacultyByKeyword(kw);
                if (profs.isEmpty()) System.out.println("No professors found.");
                else profs.forEach(System.out::println);
                break;

            case "5": // Search professors by abstract text
                System.out.print("Enter text to search abstracts: ");
                String term = scanner.nextLine().trim();
                List<String> absMatches = dl.searchFacultyByAbstract(term);
                if (absMatches.isEmpty()) System.out.println("No professors found.");
                else absMatches.forEach(System.out::println);
                break;

            case "6": // View ALL professor abstracts
                List<String> abstracts = dl.listAllAbstracts();
                if (abstracts.isEmpty()) System.out.println("No abstracts available.");
                else abstracts.forEach(System.out::println);
                break;

            case "7": return;

            case "8":
                if (confirmExit()) { dl.close(); System.out.println("Goodbye."); System.exit(0); }
                break;

            default:
                System.out.println("Invalid choice.");
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
