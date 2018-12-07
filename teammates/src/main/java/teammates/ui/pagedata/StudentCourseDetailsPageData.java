package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.SanitizationHelper;
import teammates.ui.template.StudentCourseDetailsPanel;

public class StudentCourseDetailsPageData extends PageData {
    private StudentCourseDetailsPanel studentCourseDetailsPanel;

    public StudentCourseDetailsPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public void init(CourseDetailsBundle courseDetails, List<InstructorAttributes> instructors,
                         StudentAttributes student, TeamDetailsBundle team) {
        this.student = student;
        studentCourseDetailsPanel = createStudentCourseDetailsPanel(
                                        courseDetails, instructors, student, team);
    }

    public StudentCourseDetailsPanel getStudentCourseDetailsPanel() {
        return studentCourseDetailsPanel;
    }

    private StudentCourseDetailsPanel createStudentCourseDetailsPanel(
            CourseDetailsBundle courseDetails, List<InstructorAttributes> instructors,
            StudentAttributes student, TeamDetailsBundle team) {
        String courseId = courseDetails.course.getId();
        //TODO: [CourseAttribute] remove desanitization after data migration
        String courseName = SanitizationHelper.desanitizeIfHtmlSanitized(courseDetails.course.getName());
        String studentTeam = student.team;
        String studentName = student.name;
        String studentEmail = student.email;
        List<StudentAttributes> teammates = team.students;

        return new StudentCourseDetailsPanel(courseId, courseName, instructors, studentTeam,
                                                 studentName, studentEmail, teammates);
    }

}
