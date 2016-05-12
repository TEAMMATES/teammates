package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.ui.template.StudentCourseDetailsPanel;

public class StudentCourseDetailsPageData extends PageData {
    private StudentCourseDetailsPanel studentCourseDetailsPanel;
    
    public StudentCourseDetailsPageData(final AccountAttributes account) {
        super(account);
    }
    
    public void init(final CourseDetailsBundle courseDetails, final List<InstructorAttributes> instructors, 
                         final StudentAttributes student, final TeamDetailsBundle team) { 
        this.student = student;
        studentCourseDetailsPanel = createStudentCourseDetailsPanel(
                                        courseDetails, instructors, student, team);
    }
    
    public StudentCourseDetailsPanel getStudentCourseDetailsPanel() {
        return studentCourseDetailsPanel;
    }

    private StudentCourseDetailsPanel createStudentCourseDetailsPanel(final CourseDetailsBundle courseDetails, 
                                    final List<InstructorAttributes> instructors, final StudentAttributes student, final TeamDetailsBundle team) {
        String courseId = courseDetails.course.id;
        String courseName = courseDetails.course.name; 
        String studentTeam = student.team;
        String studentName = student.name;
        String studentEmail = student.email;
        List<StudentAttributes> teammates = team.students;
        
        return new StudentCourseDetailsPanel(courseId, courseName, instructors, studentTeam,
                                                 studentName, studentEmail, teammates);
    }
    
}
