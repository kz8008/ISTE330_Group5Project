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
                String username = usernameTF.getText().trim();
                String password = passwordTF.getText().trim();

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

    } // end showloginMenu

    //Pulls up register menu to select between roles
    public void showRegisterMenu() {
        JFrame frame = new JFrame("Register");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 0));

        // Create buttons using helper
        JButton btnStudent = createButton("Register Student", panel);
        JButton btnProf = createButton("Register Professor", panel);
        JButton btnPublic = createButton("Register Public User", panel);
        JButton btnBack = createButton("Back", panel);
        JButton btnExit = createButton("Exit", panel);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Register professor
        btnProf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showRegisterProfessorMenu();
            }
        });

        // Register student
        btnStudent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showRegisterStudentMenu();
            }
        });

        // Register public user
        btnPublic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showRegisterPublicUserMenu();
            }
        });

        // Back button handlers
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showMainMenu();
            }
        });

        // Exit program
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                exit();
            }
        });
    } // end showRegisterMenu

    //pulls up student registration menu
    private void showRegisterStudentMenu() {
        JFrame frame = new JFrame("Register Student");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);

        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));

        // Text fields
        JTextField usernameTF = new JTextField();
        JTextField passwordTF = new JPasswordField();
        JTextField firstNameTF = new JTextField();
        JTextField lastNameTF = new JTextField();
        JTextField majorTF = new JTextField();
        JTextField emailTF = new JTextField();
        JTextField phoneTF = new JTextField();

        // Create labels + fields with helper
        createLabel("Username", panel, usernameTF);
        createLabel("Password", panel, passwordTF);
        createLabel("First Name", panel, firstNameTF);
        createLabel("Last Name", panel, lastNameTF);
        createLabel("Major", panel, majorTF);
        createLabel("Email", panel, emailTF);
        createLabel("Phone (xxx-xxx-xxxx)", panel, phoneTF);

        // Buttons
        JButton btnBack = createButton("Back", panel);
        JButton btnSubmit = createButton("Submit", panel);

        // Pressing Enter triggers Submit
        frame.getRootPane().setDefaultButton(btnSubmit);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //submit handler
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String username = usernameTF.getText().trim();
                String password = passwordTF.getText().trim();
                String fn = firstNameTF.getText().trim();
                String ln = lastNameTF.getText().trim();
                String major = majorTF.getText().trim();
                String email = emailTF.getText().trim();
                String phone = phoneTF.getText().trim();

                // Validate basic fields
                if (username.isEmpty() || password.isEmpty() || fn.isEmpty() || ln.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Please fill in username, password, first name, and last name.",
                            "Missing Information",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Create account
                int accountId = dl.registerAccount(username, password, "Student");
                if (accountId <= 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not create account. Username may already exist.",
                            "Registration Failed",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create student record
                int sid = dl.addStudent(0, fn, ln, major, email, phone, accountId);

                //if it worked, submit and open login menu
                if (sid > 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Student account created successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    frame.dispose();
                    showLoginMenu();
                } 
                //otherwise, keep everything open and let user try again
                else {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to create student profile.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // ===============================
        // Back Button Handler
        // ===============================
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showRegisterMenu();
            }
        });
    } // end showStudentRegister

    private void showRegisterProfessorMenu() {
        System.out.println("Showing Student Menu (unimplemented)");
    }

    private void showRegisterPublicUserMenu() {
        System.out.println("Showing Student Menu (unimplemented)");
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
