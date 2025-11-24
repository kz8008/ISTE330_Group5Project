import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PresentationLayerGUI {
    private static final MainDataLayer dl = new MainDataLayer();;
    private static int currentAccountId = -1;
    private static String currentRole = null;

    // Fonts and constants for SWING
    public static Font myFontForOutput = new Font("Courier", Font.BOLD, 20);

    public PresentationLayerGUI() {

        if (!dl.connect()) {
            System.out.println("Could not connect to DB. Exiting.");
            exit();
        }

        showMainMenu();
    }

    // region ---------MAIN MENU -----------------

    // Pulls up the main menu gui
    public void showMainMenu() {
        // Make a frame for the menu
        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 250);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 0));

        // Create buttons, add to panel, and format with my helper
        JButton btnLogin = createButton("Login", panel);
        JButton btnRegister = createButton("Register", panel);
        JButton btnExit = createButton("Exit", panel);

        frame.add(panel);
        frame.setLocationRelativeTo(null); // center on screen
        frame.setVisible(true);

        // button event listeners

        // Pull up login menu
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showLoginMenu();
                frame.dispose();
            }
        });

        // Pull up register menu
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showRegisterMenu();
                frame.dispose();
            }
        });

        // Exits the program
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                exit();
            }
        });

    }

    // Pulls up the login menu
    private void showLoginMenu() {
        JFrame frame = new JFrame("Log In");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        // Text fields
        JTextField usernameTF = new JTextField();
        JTextField passwordTF = new JPasswordField();

        // Create labels for each field using helperr
        createLabel("Username", panel, usernameTF);
        createLabel("Password", panel, passwordTF);

        // Buttons
        JButton btnBack = createButton("Back", panel);
        JButton btnSubmit = createButton("Submit", panel);

        // lets enter trigger submit
        frame.getRootPane().setDefaultButton(btnSubmit);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Submit button handler
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameTF.getText();
                String password = passwordTF.getText();

                // check if login is valid
                System.out.println(username + " " + password);
                int aid = dl.authenticateAccount(username, password);

                // if invalid
                if (aid <= 0) {
                    JOptionPane.showMessageDialog(null, "Login failed. Please check credentials.",
                            "Login Failure",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                // if valid
                else {
                    currentAccountId = aid;
                    currentRole = dl.getRoleByAccountId(aid);
                    // check role is valid and found
                    if (currentRole == null) {
                        JOptionPane.showMessageDialog(null, "Could not determine role. Please create a new account.",
                                "Role Not Found",
                                JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                        // if role not found, direct them to make a new account
                        showRegisterMenu();
                    }

                    JOptionPane.showMessageDialog(null,
                            "Successfully logged in as " + username + " with role " + currentRole,
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("Login successful. Role: " + currentRole);

                    switch (currentRole) {
                        case "Professor":
                            showProfessorMenu();
                            break;
                        case "Student":
                            showStudentMenu();
                            break;
                        case "Public":
                            showPublicMenu();
                            break;
                        default:
                            JOptionPane.showMessageDialog(null,
                                    "Could not determine role. Please create a new account.",
                                    "Role Not Found",
                                    JOptionPane.INFORMATION_MESSAGE);
                            showRegisterMenu();
                            break;
                    }
                }

                frame.dispose();
            }
        });

        // Back button handler
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showMainMenu();
            }
        });

    }

    public void showRegisterMenu() {
    }

    // endregion ---------MAIN MENU -----------------

    // region ----------- STUDENT MENU -----------------
    private void showStudentMenu() {
        System.out.println("Showing Student Menu (unimplemented)");
    }

    // endregion ------------------ STUDENT MENU -------------------

    // region ----------- PROFESSOR MENU -----------------
    private void showProfessorMenu() {
        System.out.println("Showing Professor Menu (unimplemented)");
    }

    // endregion ------------------ PROFESSOR MENU -------------------

    // region --------------------- PUBLIC MENU ------------------------
    private void showPublicMenu() {
        System.out.println("Showing Public Menu (unimplemented)");
    }

    // endregion --------------------- PUBLIC MENU ------------------------

    // region --------------HELPER FACTORIES -----------------

    /**
     * Creates a button and formats it given text and a panel
     * 
     * @param text
     * @param panel
     * @param font
     * @return The button
     */
    private JButton createButton(String text, JPanel panel) {
        JButton button = new JButton(text);
        button.setFont(myFontForOutput);
        panel.add(button);
        return button;
    }

    // creates a label for a text field
    private void createLabel(String text, JPanel panel, JTextField textField) {
        JLabel label = new JLabel(text);
        label.setFont(myFontForOutput);

        textField.setFont(myFontForOutput);
        textField.setForeground(Color.BLUE);

        panel.add(label);
        panel.add(textField);
    }

    // endregion

    // region ------------- GLOBAL MENU OPTIONS ---------------------

    private boolean confirmExit() {
        int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        return choice == JOptionPane.YES_OPTION;
    }

    private void exit() {
        if (confirmExit()) {
            dl.close();
        }

        // End Of Job data - EOJ routines
        java.util.Date today = new java.util.Date();
        System.out.println("\nProgram terminated @ " + today + "\n");
        System.exit(0);
    }

    // endregion ------------- GLOBAL MENU OPTIONS ---------------------

    // Start the GUI
    public static void main(String[] args) {
        System.out.println("Created by:");
        System.out.println("Sarina Drake (Database Designer)");
        System.out.println("Dhiyanesh Kumaravelu (Backend Developer)");
        System.out.println("Bodhi Woll (Backend Developer)");
        System.out.println("Katiya Zawrotny (Frontend GUI Planner)");
        System.out.println("Key Zollner (Project Lead)");
        new PresentationLayerGUI();
    }
}
