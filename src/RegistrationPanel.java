import javax.swing.*;

public class RegistrationPanel {
    private final DatabaseHelper dbHelper;
    private final JFrame frame;
    private JTextField txtAcademicID, txtName, txtBirthDate, txtAddress, txtContact, txtCourseName, txtCourseCode;
    private JLabel validAcademicID, validName, validBirthDate,validAddress, validContact, validCourseName, validCourseCode;
    private JPanel registrationPanel;
    private JButton submitButton;

    public RegistrationPanel(DatabaseHelper dbHelper,JFrame frame){
        this.dbHelper = dbHelper;
        this.frame = frame;

        // Handle click on submit button.
        submitButton.addActionListener(e -> handleSubmitButtonAction());
    }

    private void handleSubmitButtonAction() {
        // Reset the validation labels
        resetValidationsLabels();

        // If txt values are not valid do nothing
        if (!isValidValues()) return;

        Student student = new Student(
                txtAcademicID.getText(),
                txtName.getText(),
                txtBirthDate.getText(),
                txtAddress.getText(),
                txtContact.getText(),
                txtCourseName.getText(),
                txtCourseCode.getText()
        );

        // Insert student record into the DataBase and update the user.
        if (dbHelper.insertStudent(student)) {
            cleanData();
            JOptionPane.showMessageDialog(frame, "Student added successfully!");
        } else {
            JOptionPane.showMessageDialog(frame, "Student already register to the course!"
                    , "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Reset the txt input and validation labels
    public void cleanData(){
        resetValidationsLabels();
        resetTextFields();
    }

    private void resetValidationsLabels(){
        validAcademicID.setText("");
        validName.setText("");
        validBirthDate.setText("");
        validAddress.setText("");
        validContact.setText("");
        validCourseName.setText("");
        validCourseCode.setText("");
    }

    private void resetTextFields(){
        txtAcademicID.setText("");
        txtName.setText("");
        txtBirthDate.setText("");
        txtAddress.setText("");
        txtContact.setText("");
        txtCourseName.setText("");
        txtCourseCode.setText("");
    }

    // Indicate if there are some invalid values and set the validation labels
    public boolean isValidValues() {
        String[] fields = {"AcademicID", "Name", "BirthDate", "Address", "Contact",
                            "CourseName", "CourseCode"};
        String[] values = {txtAcademicID.getText(), txtName.getText(), txtBirthDate.getText(),
                           txtAddress.getText(), txtContact.getText(), txtCourseName.getText(),
                           txtCourseCode.getText()};
        boolean error = false;
        for (int i = 0; i < fields.length; i++) {
            // Check if value is valid and set the right validation filed in accordance.
            if (!Validator.isValid(values[i], fields[i])) {
                error = true;
                switch (fields[i]) {
                    case "AcademicID" -> validAcademicID.setText("Invalid input ! (Integer only)");
                    case "Name" -> validName.setText("Invalid input ! (Letters and spaces only)");
                    case "BirthDate" -> validBirthDate.setText("Invalid input ! (DD-MM-YYYY)");
                    case "Address" -> validAddress.setText("Invalid input ! (Letters,spaces,numbers,comma)");
                    case "Contact" -> validContact.setText("Invalid input ! (Email or phone number only)");
                    case "CourseName" -> validCourseName.setText("Invalid input ! (Letters and spaces only)");
                    case "CourseCode" -> validCourseCode.setText("Invalid input ! (Integer only) ");
                }
            }
        }
        return !error;
    }

    public JPanel getRegistrationPanel() {
        return registrationPanel;
    }
}
