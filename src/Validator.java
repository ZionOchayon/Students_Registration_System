import java.util.regex.Pattern;

public class Validator {
    // Input Validation check using REGEX
    public static boolean isValid(String input, String field) {
        String PATTERN = switch (field) {
            case "AcademicID" -> "^\\d+\\s*$";
            case "Name" -> "^[A-Za-z\\s]+$";
            case "BirthDate" -> "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})\\s*$";
            case "Address" -> "^[A-Za-z\\s\\d,]+$";
            case "Contact" -> "^(\\d{10}|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\\s*$";
            case "CourseName" -> "^[A-Za-z\\s]+$";
            case "CourseCode" -> "^\\d+\\s*$";
            default -> "";
        };
        return Pattern.matches(PATTERN, input);
    }
}
