import javax.swing.*;
import java.awt.*;

public class StudentRegistrationSystem {

    private final JFrame frame;
    private final DatabaseHelper dbHelper;
    private DataPanel dataPanel;
    private RegistrationPanel registrationPanel;

    public StudentRegistrationSystem() {
        dbHelper = new DatabaseHelper();
        frame = new JFrame("Student Registration System By Zion & Elinor");
        initialize();
    }

    // Use to initialize the panels
    private void initialize() {
        // Configure the main frame.
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Initialize the data and registration panels into tabbed pane.
        dataPanel = new DataPanel(dbHelper,frame);
        registrationPanel = new RegistrationPanel(dbHelper,frame);
        JTabbedPane tabbedPane = createTabbedPanePanel();

        // Add the tabbed pane to the main frame and activate.
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Create and configure the tabbed pane.
    private JTabbedPane createTabbedPanePanel(){
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Registration Panel", registrationPanel.getRegistrationPanel());
        tabbedPane.addTab("Data Panel", dataPanel.getDataPanel());
        tabbedPane.setToolTipTextAt(0, "Registration panel for new records.");
        tabbedPane.setToolTipTextAt(1, "Data panel for handling existing records.");

        // Uses to clean the data from the filed and the text inputs while changing tabs.
        tabbedPane.addChangeListener(e -> {
            dataPanel.cleanData();
            registrationPanel.cleanData();
        });
        return tabbedPane;
    }

    // Start the program.
    public static void main(String[] args) {
        new StudentRegistrationSystem();
    }
}
