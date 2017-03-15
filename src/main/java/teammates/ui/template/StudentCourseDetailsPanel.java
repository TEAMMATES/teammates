package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

public class StudentCourseDetailsPanel {
    private String courseId;
    private String courseName;
    private List<InstructorAttributes> instructors;
    private String studentTeam;
    private String studentName;
    private String studentEmail;
    private List<StudentAttributes> teammates;

    public StudentCourseDetailsPanel(String courseId, String courseName, List<InstructorAttributes> instructors,
                                     String studentTeam, String studentName, String studentEmail,
                                     List<StudentAttributes> teammates) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructors = instructors;
        this.studentTeam = studentTeam;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.teammates = teammates;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<InstructorAttributes> getInstructors() {
        return instructors;
    }

    public String getStudentTeam() {
        return studentTeam;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public List<StudentAttributes> getTeammates() {
        return teammates;
    }

}
