import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    // Connection handler.
    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hm4_advancedprogramming?useSSL=false&serverTimezone=UTC",
                "root",
                "1234");
    }

    // Insert student record into the DataBase.
    public boolean insertStudent(Student student) {
        String query = "INSERT INTO students(academicID, name, birthDate, address," +
                       " contactDetails, courseName, courseCode)" +
                       " VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

             // Using to fill the ? in the query VALUES.
             pstmt.setString(1, student.getAcademicID());
             pstmt.setString(2, student.getName());
             pstmt.setString(3, student.getBirthDate());
             pstmt.setString(4, student.getAddress());
             pstmt.setString(5, student.getContactDetails());
             pstmt.setString(6, student.getCourseName());
             pstmt.setString(7, student.getCourseCode());

             // Returns true if any DataBase rows were updated.
             return pstmt.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Fetch Students records from the DataBase.
    public List<Student> fetchStudents(String fetchBy, String value) {
        List<Student> students = new ArrayList<>();
        try (Connection conn = getConnection()) {

            // Set the query to fit the chosen filter.
            String query = switch (fetchBy) {
                case "Name" -> "SELECT * FROM students WHERE name LIKE ?";
                case "AcademicID" -> "SELECT * FROM students WHERE academicID LIKE ?";
                case "CourseName" -> "SELECT * FROM students WHERE courseName LIKE ?";
                case "CourseCode" -> "SELECT * FROM students WHERE courseCode LIKE ?";
                default -> "SELECT * FROM students";
            };

            PreparedStatement pstmt = conn.prepareStatement(query);

            // Uses to fill the ? in the query only if needed.
            if(!query.equals("SELECT * FROM students")) pstmt.setString(1, value + "%");

            // Fill the students records list.
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student(
                            rs.getString("academicID"),
                            rs.getString("name"),
                            rs.getString("birthDate"),
                            rs.getString("address"),
                            rs.getString("contactDetails"),
                            rs.getString("courseName"),
                            rs.getString("courseCode"));

                    students.add(student);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return students;
    }

    // Delete student record from the DataBase by academicID and courseCode,
    // because bout of them use as primary key in the DataBase
    // (Student cannot bew register into same course twice).
    public boolean deleteStudent(String academicID, String courseCode) {
        String query = "DELETE FROM students WHERE academicID = ? AND courseCode = ?";

        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);

            // Uses to fill the ? in the query.
            pstmt.setString(1, academicID);
            pstmt.setString(2, courseCode);

            // Returns true if any DataBase rows were updated.
            return pstmt.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }

    // Update student record from the DataBase with new data by academicID and courseCode.
    public boolean updateStudent(String newAcademicID, String newName, String newCourseName,
                                 String newCourseCode, String newContactDetails, String newAddress,
                                 String newBirthDate, String academicID, String courseCode) {

        // this query use to update the new data.
        String query = "UPDATE students SET academicID = ?, name = ?, birthDate = ?, address = ?," +
                       " contactDetails = ?, courseName = ?, courseCode = ?" +
                       " WHERE academicID = ? AND courseCode = ?";

        // this query use to validate that the new student is not already register to that course.
        String query2 = "SELECT academicID FROM students WHERE academicID = ? AND courseCode = ?";

        try (Connection conn = getConnection()) {
            // prepare and execute the validation query.
            PreparedStatement pstmt = conn.prepareStatement(query2);
            pstmt.setString(1, newAcademicID);
            pstmt.setString(2, newCourseCode);
            ResultSet rs = pstmt.executeQuery();

            // Update only if academicID and courseCode doesn't change
            // or the student is not already register to that course.
            if ((newAcademicID.equals(academicID) && newCourseCode.equals(courseCode)) || !rs.next()) {
                pstmt = conn.prepareStatement(query);

                // Uses to fill the ? in the query.
                pstmt.setString(1, newAcademicID);
                pstmt.setString(2, newName);
                pstmt.setString(3, newBirthDate);
                pstmt.setString(4, newAddress);
                pstmt.setString(5, newContactDetails);
                pstmt.setString(6, newCourseName);
                pstmt.setString(7, newCourseCode);
                pstmt.setString(8, academicID);
                pstmt.setString(9, courseCode);

                // Returns true if any DataBase rows were updated.
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
