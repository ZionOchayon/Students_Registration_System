import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataPanel {
    private final DatabaseHelper dbHelper;
    private final JFrame frame;
    private JTextField txtSearch;
    private JComboBox<String> filterOptions;
    private String ChosenStudentId, ChosenCourseCode, dialogErrors;
    private JTable studentTable;
    private JPanel dataPanel;
    private List<Student> students;
    private String[][] tableData;

    public DataPanel(DatabaseHelper dbHelper,JFrame frame){
        this.dbHelper = dbHelper;
        this.frame = frame;
        createDataPanel();
    }

    private void createDataPanel(){
        dataPanel = new JPanel(new BorderLayout());

        // Initialize the search section and add to the dataPanel.
        JPanel searchPanel = createSearchPanel();
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        dataPanel.add(searchPanel, BorderLayout.NORTH);

        // Initialize the data (table) section and add to the dataPanel.
        JPanel tablePanel = createTablePanel();
        dataPanel.add(tablePanel, BorderLayout.CENTER);

        // Initialize the actions section and add to the dataPanel.
        JPanel actionPanel = createTableActionPanel();
        dataPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    // Creating search panel.
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        // Dropdown to select search criterion.
        String[] searchCriteria = {"By Name", "By Academic ID", "By Course Name", "By Course Code"};
        filterOptions = new JComboBox<>(searchCriteria);
        // clear input and table filer data after selection.
        filterOptions.addActionListener(e -> cleanData());
        filterOptions.setToolTipText("Select a category to search");
        searchPanel.add(filterOptions);

        // Text field for search term.
        txtSearch = new JTextField(15);
        txtSearch.setToolTipText("Type text to search");
        searchPanel.add(txtSearch);

        // Search button.
        JButton btnSearch = new JButton("Search");
        // Handle search on button click.
        btnSearch.addActionListener(e -> handleSearchButtonAction());
        btnSearch.setToolTipText("Tap to search");
        searchPanel.add(btnSearch);

        // Reset button to clear search.
        JButton btnReset = new JButton("All");
        // clear input and table filer data on button click.
        btnReset.addActionListener(e -> cleanData());
        btnReset.setToolTipText("View all registered students");
        searchPanel.add(btnReset);

        return searchPanel;
    }

    // Start search by the search term and search criterion.
    private void handleSearchButtonAction() {
        String searchTerm = txtSearch.getText();
        switch (filterOptions.getSelectedIndex()) {
            case 0 -> refreshTableData("Name", searchTerm);
            case 1 -> refreshTableData("AcademicID", searchTerm);
            case 2 -> refreshTableData("CourseName", searchTerm);
            case 3 -> refreshTableData("CourseCode", searchTerm);
        }
    }

    // Creating table panel.
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        // Create the student table with all the records.
        studentTable = new JTable();
        studentTable.setToolTipText("Press enter to confirm a change in row");
        refreshTableData("All", "None");

        // Add a ListSelectionListener to the table selection model.
        ListSelectionModel selectionModel = studentTable.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            // Ensure it's not in the middle of selection changes.
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });

        // Add the Table to the panel.
        tablePanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return tablePanel;
    }

    // Show students details upon selections
    private void handleTableSelection(){
        int[] selectedRows = studentTable.getSelectedRows();

        if (selectedRows.length == 0) return;

        // Save old StudentId and CourseCode for update operation.
        ChosenStudentId = (String) studentTable.getValueAt(selectedRows[selectedRows.length - 1], 0);
        ChosenCourseCode = (String) studentTable.getValueAt(selectedRows[selectedRows.length - 1], 3);

        // get the current modal of the table.
        DefaultTableModel studentTableModel = (DefaultTableModel) studentTable.getModel();

        // reset all the student details and changes from the former selection.
        for (int rowIndex = 0; rowIndex < studentTable.getRowCount(); rowIndex++) {
            // insert old data
            studentTableModel.setValueAt(tableData[rowIndex][0], rowIndex, 0);
            studentTableModel.setValueAt(tableData[rowIndex][1], rowIndex, 1);
            studentTableModel.setValueAt(tableData[rowIndex][2], rowIndex, 2);
            studentTableModel.setValueAt(tableData[rowIndex][3], rowIndex, 3);
            // reset the student details
            studentTableModel.setValueAt("", rowIndex, 4);
            studentTableModel.setValueAt("", rowIndex, 5);
            studentTableModel.setValueAt("", rowIndex, 6);
        }

        // Add the student details for the selected rows.
        for (int row : selectedRows) {
            studentTableModel.setValueAt(students.get(row).getContactDetails(), row, 4);
            studentTableModel.setValueAt(students.get(row).getAddress(), row, 5);
            studentTableModel.setValueAt(students.get(row).getBirthDate(), row, 6);
        }

    }

    // Refresh the table data by the search term and search criterion,
    // if not provided it will load all the data.
    private void refreshTableData(String fetchBy,String value) {
        // Fetch data by the search term and search criterion from the database.
        students = dbHelper.fetchStudents(fetchBy, value);

        // Convert the students list into a two-dimensional array for the new JTable.
        tableData = new String[students.size()][7];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            tableData[i][0] = s.getAcademicID();
            tableData[i][1] = s.getName();
            tableData[i][2] = s.getCourseName();
            tableData[i][3] = s.getCourseCode();
        }

        // Define column names for the new JTable.
        String[] columnNames = {
                "Academic ID",
                "Name",
                "Course Name",
                "Course Code",
                "Contact Details",
                "Address",
                "Birth Date"
        };

        // Update the JTable with the new data.
        DefaultTableModel model = new DefaultTableModel(tableData, columnNames);
        studentTable.setModel(model);
    }

    // Create action panel.
    private JPanel createTableActionPanel(){
        JPanel panel = new JPanel();

        // Update last selected row.
        JButton btnUpdate = new JButton("Update");
        // Handle the update button click.
        btnUpdate.addActionListener(e -> handleUpdateButtonAction());
        btnUpdate.setToolTipText("Update the last selected student record");
        panel.add(btnUpdate);

        // Delete all selected rows.
        JButton btnDelete = new JButton("Delete");
        // Handle the delete button click.
        btnDelete.addActionListener(e -> handleDeleteButtonAction());
        btnDelete.setToolTipText("Delete selected student records");
        panel.add(btnDelete);

        // Download all selected rows.
        JButton btnDownload = new JButton("Download");
        // Handle the download button click.
        btnDownload.addActionListener(e -> handleDownloadButtonAction());
        btnDownload.setToolTipText("Download selected student records");
        panel.add(btnDownload);

        return panel;
    }

    // Validate and update the last selected record.
    private void handleUpdateButtonAction() {
        int selectedRow = studentTable.getSelectedRow();

        // Do nothing if there is no selected row.
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame,
                    "Please select a student record to update!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Make sure the student wants to update the record.
        int choice = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to update this Student record?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.NO_OPTION) return;

        // Get the data from the last selected row.
        String[] values = new String[7];
        for (int i = 0; i < 7; i++) {
            values[i] = (String) studentTable.getValueAt(selectedRow, i);
        }

        // Validate the data of the row and update the user for invalid values if needed.
        if (!isValidValues(values)) {
            JOptionPane.showMessageDialog(frame,
                    dialogErrors,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update the DataBase and the table.
        if (dbHelper.updateStudent(values[0], values[1], values[2], values[3], values[4], values[5], values[6],
                ChosenStudentId, ChosenCourseCode)) {

            JOptionPane.showMessageDialog(frame,
                    "Student record updated successfully!");
            // updating the table data without ruins former search operations.
            handleSearchButtonAction();
            return;
        }
        JOptionPane.showMessageDialog(frame, "Student already register to the course !",
                                 "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Delete selected students records.
    private void handleDeleteButtonAction() {
        int[] selectedRows = studentTable.getSelectedRows();

        // Do nothing if there is no selected rows.
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(frame,
        "Please select one or more student records to delete!");
            return;
        }

        // Make sure the student wants to delete the record.
        int choice = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to delete the selected student records?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.NO_OPTION) return;

        // Deleting the selected records.
        boolean isSuccess = true;
        for (int row : selectedRows) {
            String academicID = (String) studentTable.getValueAt(row, 0);
            String courseCode = (String) studentTable.getValueAt(row, 3);

            // If one deletion fails, set the flag to false but continue trying the other deletions.
            if (!dbHelper.deleteStudent(academicID, courseCode)) {
                isSuccess = false;
            }
        }

        // Updating the table data without ruins former search operations.
        handleSearchButtonAction();

        // Update the user if the operation succeed or not.
        if (isSuccess) {
            JOptionPane.showMessageDialog(frame,
                    "Selected Student records deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Error occurred while deleting some student records!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDownloadButtonAction() {
        int[] selectedRows = studentTable.getSelectedRows();

        // Do nothing if there is no selected rows.
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(frame, "Please select one or more student records to download!");
            return;
        }

        // Choose a location to save the file and set the extension of the file to txt.
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        fileChooser.setDialogTitle("Specify a file to save");
        int userSelection = fileChooser.showSaveDialog(frame);

        // Stop id operation failed.
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        // Make sure is a txt file.
        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
            fileToSave = new File(fileToSave + ".txt");
        }

        // Write the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
            for (int row : selectedRows) {
                // Get row Data.
                String academicID = (String) studentTable.getValueAt(row, 0);
                String name = (String) studentTable.getValueAt(row, 1);
                String courseName = (String) studentTable.getValueAt(row, 2);
                String courseCode = (String) studentTable.getValueAt(row, 3);
                String contactDetails = (String) studentTable.getValueAt(row, 4);
                String address = (String) studentTable.getValueAt(row, 5);
                String birthDate = (String) studentTable.getValueAt(row, 6);

                // Write details to file in readable format
                writer.write("Academic ID: " + academicID);
                writer.newLine();
                writer.write("Name: " + name);
                writer.newLine();
                writer.write("Course Name: " + courseName);
                writer.newLine();
                writer.write("Course Code: " + courseCode);
                writer.newLine();
                writer.write("Contact Details: " + contactDetails);
                writer.newLine();
                writer.write("Address: " + address);
                writer.newLine();
                writer.write("Birth Date: " + birthDate);
                writer.newLine();
                writer.newLine();
            }
            JOptionPane.showMessageDialog(frame, "The selected student records saved successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "An error occurred while saving the student records.");
            ex.printStackTrace();
        }
    }

    // Reset the txt input and dialog errors and table data
    public void cleanData(){
        refreshTableData("All", "None");
        dialogErrors = "";
        txtSearch.setText("");
    }

    // Indicate if there are some invalid values and set the dialog errors
    public boolean isValidValues(String[] values) {
        String[] fields = {"AcademicID", "Name", "CourseName", "CourseCode", "Contact", "Address",  "BirthDate"};
        dialogErrors = "";
        for (int i = 0; i < fields.length; i++) {
            // Check if value is valid and set the right validation filed in accordance.
            if (!Validator.isValid(values[i], fields[i])) {
                switch (fields[i]) {
                    case "AcademicID" -> dialogErrors += "Invalid Academic ID input ! (Integer only)\n";
                    case "Name" -> dialogErrors += "Invalid Name input ! (Letters and spaces only)\n";
                    case "BirthDate" -> dialogErrors += "Invalid Birth Date input ! (DD-MM-YYYY)\n";
                    case "Address" -> dialogErrors += "Invalid Address input ! (Letters,spaces,numbers,comma only)\n";
                    case "Contact" -> dialogErrors += "Invalid Contact input ! (Email or phone number only)\n";
                    case "CourseName" -> dialogErrors += "Invalid Course Name input ! (Letters and spaces only)\n";
                    case "CourseCode" -> dialogErrors += "Invalid Course Code input ! (Integer only)\n";
                }
            }
        }
        return dialogErrors.length() == 0;
    }

    public JPanel getDataPanel() {
        return dataPanel;
    }
}
