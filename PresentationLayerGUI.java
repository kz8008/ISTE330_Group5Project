import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PresentationLayerGUI {
    private static final MainDataLayer dl = new MainDataLayer();
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

    // endregion ---------MAIN MENU -----------------

    // region ---------LOGIN MENU -----------------

    // Pulls up the login menu
    private void showLoginMenu() {
        JFrame frame = new JFrame("Log In");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 0));

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
                // System.out.println(username + " " + password);
                int aid = dl.authenticateAccount(username, password);

                // if invalid
                if (aid <= 0) {
                    JOptionPane.showMessageDialog(null, "Login failed. Please check credentials.",
                            "Login Failure",
                            JOptionPane.INFORMATION_MESSAGE);
                    showLoginMenu();
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

    // endregion ---------LOGIN MENU -----------------

    // region ---------REGISTER MENU -----------------

    // Pulls up register menu to select between roles
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

    // pulls up student registration menu
    private void showRegisterStudentMenu() {
        JFrame frame = new JFrame("Register Student");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);

        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 0));

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

        // submit handler
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

                // if it worked, submit and open login menu
                if (sid > 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Student account created successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    frame.dispose();
                    showLoginMenu();
                }
                // otherwise, keep everything open and let user try again
                else {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to create student profile.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // back button handler
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showRegisterMenu();
            }
        });
    } // end showStudentRegister

    // shows prof register menu
    private void showRegisterProfessorMenu() {
        JFrame frame = new JFrame("Register Professor");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);

        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 0));

        // Text fields
        JTextField usernameTF = new JTextField();
        JTextField passwordTF = new JPasswordField();
        JTextField firstNameTF = new JTextField();
        JTextField lastNameTF = new JTextField();
        JTextField buildingTF = new JTextField();
        JTextField officeTF = new JTextField();
        JTextField emailTF = new JTextField();
        JTextField phoneTF = new JTextField();

        // Create labels + fields with helper
        createLabel("Username", panel, usernameTF);
        createLabel("Password", panel, passwordTF);
        createLabel("First Name", panel, firstNameTF);
        createLabel("Last Name", panel, lastNameTF);
        createLabel("Building Code", panel, buildingTF);
        createLabel("Office Number", panel, officeTF);
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

        // submit handler
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String username = usernameTF.getText().trim();
                String password = passwordTF.getText().trim();
                String fn = firstNameTF.getText().trim();
                String ln = lastNameTF.getText().trim();
                String building = buildingTF.getText().trim();
                String office = officeTF.getText().trim();
                String email = emailTF.getText().trim();
                String phone = phoneTF.getText().trim();

                // Basic required fields
                if (username.isEmpty() || password.isEmpty() || fn.isEmpty() || ln.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Please fill in username, password, first name, and last name.",
                            "Missing Information",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Create account
                int accountId = dl.registerAccount(username, password, "Professor");
                if (accountId <= 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not create account. Username may already exist.",
                            "Registration Failed",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create professor record
                int pid = dl.addProfessor(0, fn, ln, building, office, email, phone, accountId);

                // If success
                if (pid > 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Professor account created successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    frame.dispose();
                    showLoginMenu(); // same behavior as Student registration
                }
                // Failure
                else {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to create professor profile.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // back handler
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showRegisterMenu();
            }
        });
    } // end registerProfessorMenu

    // brings up public user registration
    private void showRegisterPublicUserMenu() {
        JFrame frame = new JFrame("Register Public User");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 350);

        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 0));

        // Text fields
        JTextField usernameTF = new JTextField();
        JTextField passwordTF = new JPasswordField();
        JTextField firstNameTF = new JTextField();
        JTextField lastNameTF = new JTextField();
        JTextField organizationTF = new JTextField();
        JTextField emailTF = new JTextField();

        // Create labels + fields with helper
        createLabel("Username", panel, usernameTF);
        createLabel("Password", panel, passwordTF);
        createLabel("First Name", panel, firstNameTF);
        createLabel("Last Name", panel, lastNameTF);
        createLabel("Organization", panel, organizationTF);
        createLabel("Email", panel, emailTF);

        // Buttons
        JButton btnBack = createButton("Back", panel);
        JButton btnSubmit = createButton("Submit", panel);

        // Pressing Enter triggers Submit
        frame.getRootPane().setDefaultButton(btnSubmit);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // submit handler
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String username = usernameTF.getText().trim();
                String password = passwordTF.getText().trim();
                String fn = firstNameTF.getText().trim();
                String ln = lastNameTF.getText().trim();
                String org = organizationTF.getText().trim();
                String email = emailTF.getText().trim();

                // Validate basic fields
                if (username.isEmpty() || password.isEmpty() || fn.isEmpty() || ln.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Please fill in username, password, first name, and last name.",
                            "Missing Information",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Create account
                int accountId = dl.registerAccount(username, password, "Public");
                if (accountId <= 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not create account. Username may already exist.",
                            "Registration Failed",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create public user record
                int pid = dl.addPublicUser(fn, ln, org, email, accountId);

                // if it worked, submit and open login menu
                if (pid > 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Public user account created successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    frame.dispose();
                    showLoginMenu();
                }
                // otherwise, keep everything open and let user try again
                else {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to create public profile.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // back handler
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showRegisterMenu();
            }
        });
    } // end showRegisterPublicUserMenu

    // endregion ---------REGISTER MENU -----------------

    // region ----------- STUDENT MENU -----------------
    // pulls up gui for student menu
    public void showStudentMenu() {
        int stuId = dl.findStudentIdByAccountId(currentAccountId);
        if (stuId < 0) {
            JOptionPane.showMessageDialog(null,
                    "Student profile not found for this account.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Frame setup
        JFrame frame = new JFrame("Student Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(430, 500);

        JPanel panel = new JPanel(new GridLayout(9, 1, 10, 0));

        // Create buttons
        JButton btnAddInterests = createButton("Add Your Interests", panel);
        JButton btnModifyInterests = createButton("Remove An Interest", panel);
        JButton btnViewInterests = createButton("View Your Interests", panel);
        JButton btnViewAllProfAbs = createButton("View All Professor Abstracts", panel);
        JButton btnFindMutual = createButton("Find Professors By Mutual Interests", panel);
        JButton btnSearchByInterests = createButton("Search Professors By Interests", panel);
        JButton btnSearchByAbstract = createButton("Search Professors By Abstract Text", panel);
        JButton btnLogout = createButton("Logout", panel);
        JButton btnExit = createButton("Exit", panel);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        btnAddInterests.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddInterestMenu(stuId);
                frame.dispose();
            }
        });

        btnModifyInterests.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRemoveInterestsMenu(stuId);
                frame.dispose();
            }
        });

        btnViewInterests.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showViewInterestsMenu(stuId);
                frame.dispose();
            }
        });

        btnViewAllProfAbs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showProfessorAbstractList();
                frame.dispose();
            }
        });

        btnFindMutual.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMutualInterestProfessorMenu(stuId);
                frame.dispose();
            }
        });

        btnSearchByInterests.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSearchProfessorByInterestMenu();
                frame.dispose();
            }
        });

        btnSearchByAbstract.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSearchProfessorByAbstractTextMenu();
                frame.dispose();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
                frame.dispose();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
    }

    // menu to add an interest
    public void showAddInterestMenu(int stuId) {
        String topic = JOptionPane.showInputDialog(
                null,
                "Enter an interest (1–3 words):",
                "Add Interest",
                JOptionPane.PLAIN_MESSAGE);

        // if user cancels
        if (topic == null) {
            showStudentMenu();
            return;
        }

        topic = topic.trim().toLowerCase();
        if (topic.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You must enter a topic.");
            showAddInterestMenu(stuId);
            return;
        }

        int kid = dl.ensureKeyword(topic);
        if (kid > 0) {
            dl.addStudentKeyword(stuId, kid);
            JOptionPane.showMessageDialog(null, "Interest added.");
        }

        showStudentMenu();
    }

    // meny for remove (modify) interest
    public void showRemoveInterestsMenu(int stuId) {
        String toRemove = JOptionPane.showInputDialog(
                null,
                "Enter interest to remove (exact text):",
                "Remove Interest",
                JOptionPane.PLAIN_MESSAGE);

        if (toRemove == null) { // user canceled
            showStudentMenu();
            return;
        }

        toRemove = toRemove.trim().toLowerCase();
        if (toRemove.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You must enter an interest name.");
            showRemoveInterestsMenu(stuId);
            return;
        }

        int rid = dl.ensureKeyword(toRemove);
        if (rid > 0) {
            try {
                dl.deleteStudentKeyword(stuId, rid);
                JOptionPane.showMessageDialog(null, "Interest removed from your profile.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Delete keyword not implemented in Data Layer.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Interest was not found.");
        }

        showStudentMenu();
    }

    // menu for showing interests
    public void showViewInterestsMenu(int stuId) {
        List<String> interests = dl.getAllStudentInterests(stuId);

        List<JPanel> cards = new ArrayList<>();
        // make a card for each line of data for better display
        for (String i : interests) {
            cards.add(createInterestCard(i));
        }

        showCardWindow("", cards);
    }

    // helper for creating interest cards
    private JPanel createInterestCard(String rawInterest) {
        String interest = capitalizeWords(rawInterest);
        return createCard("Interest", interest);
    }

    // menu for showing abstracts list
    public void showProfessorAbstractList() {
        List<String> list = dl.listAllAbstracts();

        List<JPanel> cards = new ArrayList<>();
        for (String a : list) {
            cards.add(createAbstractCard(a));
        }

        showCardWindow("All Professor Abstracts", cards);
    }

    // helper for abstract card formatting
    private JPanel createAbstractCard(String raw) {
        String title = "Professor Abstract";
        String body = raw;

        if (raw.contains("\n")) {
            int idx = raw.indexOf("\n");
            title = raw.substring(0, idx).trim();
            body = raw.substring(idx + 1).trim();
        }

        return createCard(title, body);
    }

    // Mututal interests menu
    public void showMutualInterestProfessorMenu(int stuId) {
        List<String> matches = dl.findMatchingFaculty(stuId);

        List<JPanel> cards = new ArrayList<>();
        for (String m : matches) {
            cards.add(createCard("Matching Faculty", m));
        }
        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No professors with mutual interests found.");
        } else {
            showCardWindow("Professors With Mutual Interests", cards);
        }

    }

    // search prof by interest
    public void showSearchProfessorByInterestMenu() {
        String key = JOptionPane.showInputDialog(
                null,
                "Enter keyword to search professors:",
                "Search by Interest",
                JOptionPane.PLAIN_MESSAGE);

        if (key == null) {
            showStudentMenu();
            return;
        }

        key = key.trim();
        List<String> profs = dl.searchFacultyByKeyword(key);

        List<JPanel> cards = new ArrayList<>();
        for (String prof : profs) {
            cards.add(createCard("Professor", prof));
        }
        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No professors found.");
        } else {
            showCardWindow("Faculty Search Results", cards);
        }
    }

    // search prof by abstract text
    public void showSearchProfessorByAbstractTextMenu() {
        String text = JOptionPane.showInputDialog(
                null,
                "Enter text to search abstracts:",
                "Search by Abstract Text",
                JOptionPane.PLAIN_MESSAGE);

        if (text == null) {
            showStudentMenu();
            return;
        }

        text = text.trim();
        List<String> results = dl.searchProfessorsByAbstractText(text);

        List<JPanel> cards = new ArrayList<>();
        for (String result : results) {
            cards.add(createCard("Abstract Match", result));
        }
        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No abstracts found.");
        } else {
            showCardWindow("Abstract Search Results", cards);
        }
    }

    // endregion ------------------ STUDENT MENU -------------------

    // region ----------- PROFESSOR MENU -----------------
    // main menu for professors
    public void showProfessorMenu() {
        int profId = dl.findProfessorIdByAccountId(currentAccountId);
        if (profId < 0) {
            JOptionPane.showMessageDialog(null,
                    "Professor profile not found for this account.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Frame setup
        JFrame frame = new JFrame("Professor Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 500);

        JPanel panel = new JPanel(new GridLayout(9, 1, 10, 0));

        // Create buttons
        JButton btnAddAbstract = createButton("Add Abstract", panel);
        JButton btnUpdateAbstract = createButton("Update Abstract", panel);
        JButton btnDeleteAbstract = createButton("Delete Abstract", panel);
        JButton btnViewAbstracts = createButton("View My Abstracts", panel);
        JButton btnAddInterest = createButton("Add Interests", panel);
        JButton btnViewInterests = createButton("View My Interests", panel);
        JButton btnSearchStudents = createButton("Search Students by Interest", panel);
        JButton btnLogout = createButton("Logout", panel);
        JButton btnExit = createButton("Exit", panel);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        btnAddAbstract.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddAbstractMenu(profId);
            }
        });

        btnUpdateAbstract.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUpdateAbstractMenu(profId);
                frame.dispose();
            }
        });

        btnDeleteAbstract.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDeleteAbstractMenu(profId);
                frame.dispose();
            }
        });

        btnViewAbstracts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showProfViewAbstractsMenu(profId);
                frame.dispose();
            }
        });

        btnAddInterest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showProfAddInterestMenu(profId);
                frame.dispose();
            }
        });

        btnViewInterests.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showProfViewInterestsMenu(profId);
                frame.dispose();
            }
        });

        btnSearchStudents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSearchStudentsByInterestMenu(profId);
                frame.dispose();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showMainMenu();

            }
        });

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
    }

    // add abstract
    private void showAddAbstractMenu(int profId) {
        String title = JOptionPane.showInputDialog(
                null,
                "Title:",
                "Add Abstract",
                JOptionPane.PLAIN_MESSAGE);
        if (title == null)
            return;

        String text = JOptionPane.showInputDialog(
                null,
                "Abstract Text:",
                "Add Abstract",
                JOptionPane.PLAIN_MESSAGE);
        if (text == null)
            return;

        String fp = JOptionPane.showInputDialog(
                null,
                "File Path (optional):",
                "Add Abstract",
                JOptionPane.PLAIN_MESSAGE);
        if (fp != null && fp.trim().isEmpty())
            fp = null;

        int aId = dl.addAbstract(0, title, text, fp);
        if (aId > 0) {
            dl.addProfessorAbstract(profId, aId, "Author");
            JOptionPane.showMessageDialog(null, "Abstract added with id: " + aId);
        } else {
            JOptionPane.showMessageDialog(null, "Failed to add abstract.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // update abstract
    private void showUpdateAbstractMenu(int profId) {
        String upIdS = JOptionPane.showInputDialog(
                null,
                "Abstract ID to update:",
                "Update Abstract",
                JOptionPane.PLAIN_MESSAGE);
        if (upIdS == null)
            return;

        String newTitle = JOptionPane.showInputDialog(
                null,
                "New Title:",
                "Update Abstract",
                JOptionPane.PLAIN_MESSAGE);
        if (newTitle == null)
            return;

        String newText = JOptionPane.showInputDialog(
                null,
                "New Text:",
                "Update Abstract",
                JOptionPane.PLAIN_MESSAGE);
        if (newText == null)
            return;

        try {
            int upId = Integer.parseInt(upIdS.trim());
            boolean res = dl.updateAbstractIfOwned(profId, upId, newTitle, newText);
            JOptionPane.showMessageDialog(null,
                    res ? "Abstract updated." : "Unable to update abstract (not yours or invalid ID).");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Invalid ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // delete abstract
    private void showDeleteAbstractMenu(int profId) {
        String delIdS = JOptionPane.showInputDialog(
                null,
                "Abstract ID to delete:",
                "Delete Abstract",
                JOptionPane.PLAIN_MESSAGE);
        if (delIdS == null)
            return;

        try {
            int delId = Integer.parseInt(delIdS.trim());
            boolean res = dl.deleteAbstractIfOwned(profId, delId);
            JOptionPane.showMessageDialog(null,
                    res ? "Abstract deleted." : "Unable to delete abstract (not yours or invalid ID).");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Invalid ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // shows all of the current professor's abstracts
    public void showProfViewAbstractsMenu(int profId) {
        List<String> list = dl.getProfessorAbstracts(profId);

        List<JPanel> cards = new ArrayList<>();
        for (String a : list) {
            cards.add(createAbstractCard(a));
        }

        showCardWindow("All Professor Abstracts", cards);
    }

    // add prof interest
    private void showProfAddInterestMenu(int profId) {
        String kw = JOptionPane.showInputDialog(
                null,
                "Enter an interest (1–3 words):",
                "Add Interest",
                JOptionPane.PLAIN_MESSAGE);
        if (kw == null)
            return;

        kw = kw.trim().toLowerCase();
        if (!kw.isEmpty()) {
            int kwId = dl.ensureKeyword(kw);
            if (kwId > 0)
                dl.addProfessorKeyword(profId, kwId);
            JOptionPane.showMessageDialog(null, "Interest added.");
        } else {
            JOptionPane.showMessageDialog(null, "You must enter a valid interest.");
        }
    }

    // menu for showing interests
    public void showProfViewInterestsMenu(int profId) {
        List<String> interests = dl.viewProfessorInterests(profId);

        List<JPanel> cards = new ArrayList<>();
        // make a card for each line of data for better display
        for (String i : interests) {
            cards.add(createInterestCard(i));
        }

        showCardWindow("Your Interests", cards);
    }

    // search students by interest
    private void showSearchStudentsByInterestMenu(int profId) {
        String interest = JOptionPane.showInputDialog(
                null,
                "Enter interest to search students for (exact match):",
                "Search Students",
                JOptionPane.PLAIN_MESSAGE);
        if (interest == null)
            return;

        interest = interest.trim();
        try {
            List<String> students = dl.searchStudentByInterest(interest);
            if (students == null || students.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No students found.");
            } else {
                List<JPanel> cards = new ArrayList<>();
                for (String s : students) {
                    cards.add(createCard("Student", s));
                }
                showCardWindow("Students Matching Interest: " + interest, cards);
            }
        } catch (NoSuchMethodError | AbstractMethodError ex) {
            JOptionPane.showMessageDialog(null,
                    "Search by student interest not implemented in data layer.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error searching students: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // endregion ------------------ PROFESSOR MENU -------------------

    // region --------------------- PUBLIC MENU ------------------------
    // public user main menu
    public void showPublicMenu() {
        int pubId = dl.findPublicIdByAccountId(currentAccountId);
        if (pubId < 0) {
            JOptionPane.showMessageDialog(null,
                    "Public user profile not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Frame setup
        JFrame frame = new JFrame("Public User Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(430, 500);

        JPanel panel = new JPanel(new GridLayout(9, 1, 10, 0));

        // Create buttons
        JButton btnAddInterest = createButton("Add Your Interests", panel);
        JButton btnModifyInterest = createButton("Remove An Interest", panel);
        JButton btnViewInterest = createButton("View Your Interests", panel);
        JButton btnViewMutuals = createButton("Find Professors with Mutual Interests", panel);
        JButton btnSearchByInterest = createButton("Search Professors By Interests", panel);
        JButton btnSearchByAbstract = createButton("Search Professors By Abstract Text", panel);
        JButton btnViewAllAbstracts = createButton("View Professor Abstracts (All)", panel);
        JButton btnLogout = createButton("Logout", panel);
        JButton btnExit = createButton("Exit", panel);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        btnAddInterest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddPublicInterestMenu(pubId);
                frame.dispose();
            }
        });

        btnModifyInterest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRemovePublicInterestMenu(pubId);
                frame.dispose();
            }
        });

        btnViewInterest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showViewPublicInterests(pubId);
                frame.dispose();
            }
        });

        btnViewMutuals.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPublicMutualInterestsMenu(pubId);
                frame.dispose();
            }
        });

        btnSearchByInterest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showGuestSearchProfessorByInterestMenu();
                frame.dispose();
            }
        });

        btnSearchByAbstract.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showGuestSearchProfessorByAbstractTextMenu();
                frame.dispose();
            }
        });

        btnViewAllAbstracts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showGuestProfessorAbstractList();
                frame.dispose();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
                frame.dispose();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
    }

    // add public interst
    private void showAddPublicInterestMenu(int pubId) {
        String interest = JOptionPane.showInputDialog(
                null,
                "Enter an interest (1–3 words):",
                "Add Interest",
                JOptionPane.PLAIN_MESSAGE);

        if (interest == null) {
            showPublicMenu();
            return;
        }

        interest = interest.trim().toLowerCase();
        if (interest.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You must enter a topic.");
            showAddPublicInterestMenu(pubId);
            return;
        }

        int k1 = dl.ensureKeyword(interest);
        if (k1 > 0) {
            dl.addPublicKeyword(pubId, k1);
            JOptionPane.showMessageDialog(null, "Interest added.");
        }

        showPublicMenu();
    }

    // remove public interest
    private void showRemovePublicInterestMenu(int pubId) {
        String remove = JOptionPane.showInputDialog(
                null,
                "Enter the exact interest to remove:",
                "Remove Interest",
                JOptionPane.PLAIN_MESSAGE);

        if (remove == null) {
            showPublicMenu();
            return;
        }

        remove = remove.trim().toLowerCase();
        if (remove.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You must enter an interest name.");
            showRemovePublicInterestMenu(pubId);
            return;
        }

        int k2 = dl.ensureKeyword(remove);
        if (k2 > 0) {
            dl.deletePublicKeyword(pubId, k2);
            JOptionPane.showMessageDialog(null, "Interest removed.");
        } else {
            JOptionPane.showMessageDialog(null, "Interest not found.");
        }

        showPublicMenu();
    }

    // view public interests
    private void showViewPublicInterests(int pubId) {
        List<String> interests = dl.listPublicKeywords(pubId);

        List<JPanel> cards = new ArrayList<>();
        for (String i : interests) {
            cards.add(createCard("Interest", capitalizeWords(i)));
        }

        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You have no interests added.");
            showPublicMenu();
        } else {
            showCardWindow("Your Interests", cards);
        }
    }

    // search by mutual interest
    private void showPublicMutualInterestsMenu(int pubId) {
        List<String> matches = dl.findMatchingFaculty(pubId);

        List<JPanel> cards = new ArrayList<>();
        for (String m : matches) {
            cards.add(createCard("Matching Faculty", m));
        }
        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No professors with mutual interests found.");
        } else {
            showCardWindow("Professors With Mutual Interests", cards);
        }

    }

    // search profs by interest
    private void showGuestSearchProfessorByInterestMenu() {
        String key = JOptionPane.showInputDialog(
                null,
                "Enter interest:",
                "Search Professors By Interest",
                JOptionPane.PLAIN_MESSAGE);

        if (key == null) {
            showPublicMenu();
            return;
        }

        key = key.trim();
        List<String> profs = dl.searchFacultyByKeyword(key);

        List<JPanel> cards = new ArrayList<>();
        for (String p : profs) {
            cards.add(createCard("Professor", p));
        }

        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No professors found.");
        } else {
            showCardWindow("Faculty Search Results", cards);
        }
    }

    // search by prof abstracts
    private void showGuestSearchProfessorByAbstractTextMenu() {
        String term = JOptionPane.showInputDialog(
                null,
                "Enter text to search abstracts:",
                "Search Professors By Abstract Text",
                JOptionPane.PLAIN_MESSAGE);

        if (term == null) {
            showPublicMenu();
            return;
        }

        term = term.trim();
        List<String> absMatches = dl.searchFacultyByAbstract(term);

        List<JPanel> cards = new ArrayList<>();
        for (String a : absMatches) {
            cards.add(createCard("Abstract Match", a));
        }

        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No professors found.");
        } else {
            showCardWindow("Abstract Search Results", cards);
        }
    }

    // view all abstracts
    private void showGuestProfessorAbstractList() {
        List<String> abstracts = dl.listAllAbstracts();

        List<JPanel> cards = new ArrayList<>();
        for (String a : abstracts) {
            cards.add(createAbstractCard(a));
        }

        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No abstracts available.");
        } else {
            showCardWindow("All Professor Abstracts", cards);
        }
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

    // shows a window of cards to display multiple lines of data
    private void showCardWindow(String windowTitle, List<JPanel> cardList) {
        JFrame frame = new JFrame(windowTitle);
        frame.setSize(500, 600); // window size
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Container panel for all cards
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        for (JPanel card : cardList) {
            // Force card to fixed height but stretch to window width
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
            container.add(card);
        }

        // Wrap container in a scroll pane
        JScrollPane scrollPane = new JScrollPane(container,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        frame.add(scrollPane);
        frame.setVisible(true);

        // go back to menu on close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
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
        });

    }

    // creates "cards" for display of multiple lines of data
    private JPanel createCard(String title, String body) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JTextArea bodyArea = new JTextArea(body);
        bodyArea.setEditable(false);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        bodyArea.setBackground(new Color(245, 245, 245));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(bodyArea, BorderLayout.CENTER);

        // Wrapper adds spacing around card
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        wrapper.add(card);

        return wrapper;
    }

    // capitalizes words
    private String capitalizeWords(String text) {
        if (text == null || text.isEmpty())
            return text;

        StringBuilder out = new StringBuilder();
        for (String word : text.split("\\s+")) {
            if (word.length() > 0) {
                out.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return out.toString().trim();
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
            // End Of Job data - EOJ routines
            java.util.Date today = new java.util.Date();
            System.out.println("\nProgram terminated @ " + today + "\n");
            System.exit(0);
        }

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
