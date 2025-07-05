// Student.java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Student {
    private String name;
    private double mark;
    private String grade;
    private String remark;
    private String timestamp;

    public Student(String name, double mark) {
        this.name = name;
        this.mark = mark;
        this.grade = GradeUtils.getGrade(mark);
        this.remark = GradeUtils.getRemark(mark);
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getName() {
        return name;
    }

    public double getMark() {
        return mark;
    }

    public String getGrade() {
        return grade;
    }

    public String getRemark() {
        return remark;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setMark(double mark) {
        this.mark = mark;
        this.grade = GradeUtils.getGrade(mark);
        this.remark = GradeUtils.getRemark(mark);
    }

    public String[] toCSVRow() {
        return new String[]{name, String.valueOf(mark), grade, remark, timestamp};
    }

    public static String[] getCSVHeaders() {
        return new String[]{"Name", "Mark", "Grade", "Remark", "Timestamp"};
    }
}
