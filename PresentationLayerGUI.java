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
            }
        });

        // Pull up register menu
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showRegisterMenu();
            }
        });

        // Exits the program
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                exit();
            }
        });
    }

    private void showLoginMenu() {
        System.out.println("Showing Login Menu (unimplemented)");
    }

    private void showRegisterMenu() {
         System.out.println("Showing Register Menu (unimplemented)");
    }

    // endregion ---------MAIN MENU -----------------

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
