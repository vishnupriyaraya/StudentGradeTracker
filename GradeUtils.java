// GradeUtils.java
public class GradeUtils {

    public static String getGrade(double mark) {
        if (mark >= 90) return "A";
        if (mark >= 80) return "B";
        if (mark >= 70) return "C";
        if (mark >= 60) return "D";
        return "F";
    }

    public static String getRemark(double mark) {
        String grade = getGrade(mark);
        return switch (grade) {
            case "A" -> "Excellent";
            case "B" -> "Very Good";
            case "C" -> "Good";
            case "D" -> "Needs Improvement";
            default -> "Poor";
        };
    }
}
