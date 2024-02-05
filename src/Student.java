public class Student {
    private String academicID, name, birthDate, address, contactDetails, courseName, courseCode;

    public Student(String academicID, String name, String birthDate, String address
                 , String contactDetails, String courseName, String courseCode){
        this.academicID = academicID;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.contactDetails = contactDetails;
        this.courseName = courseName;
        this.courseCode = courseCode;
    }

    public String getAcademicID() {
        return academicID;
    }

    public String getName() {
        return name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getAddress() {
        return address;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }
}
