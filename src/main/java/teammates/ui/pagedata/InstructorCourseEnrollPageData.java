package teammates.ui.pagedata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Logger;

/**
 * PageData: this is page data for 'Enroll' page for a course of an instructor.
 */
public class InstructorCourseEnrollPageData extends PageData {

    private static final Logger log = Logger.getLogger();

    private String courseId;
    private String enrollStudents;
    private Map<String, String> enrollErrorLines = new HashMap<>();
    private Map<String, String> enrollNewStudentsLines = new HashMap();
    private Map<String, String> enrollModifiedStudentsLines = new HashMap();
    private Map<String, String> enrollUnmodifiedStudentsLines = new HashMap();

    public InstructorCourseEnrollPageData(AccountAttributes account, String sessionToken, String courseId,
            String enrollStudents) {
        super(account, sessionToken);
        this.courseId = courseId;
        this.enrollStudents = enrollStudents;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEnrollStudents() {
        return enrollStudents;
    }

    public Map<String, String> getEnrollErrorLines() {
        return enrollErrorLines;
    }

    public void setEnrollErrorLines(Map<String, String> enrollErrorLines) {
        this.enrollErrorLines = enrollErrorLines;
    }

    public void updateEnrollSuccessLines(List<StudentAttributes>[] students) {
        for (int i = 1; i < StudentUpdateStatus.STATUS_COUNT - 2; i++) {
            switch (StudentUpdateStatus.enumRepresentation(i)) {
            case NEW:
                for (StudentAttributes newStudent : students[i]) {
                    enrollNewStudentsLines.put(newStudent.email, "Student has been added successfully.");
                }
                break;
            case MODIFIED:
                for (StudentAttributes modifiedStudent : students[i]) {
                    enrollModifiedStudentsLines.put(modifiedStudent.email, "Student has been modified successfully.");
                }
                break;
            case UNMODIFIED:
                for (StudentAttributes unmodifiedStudent : students[i]) {
                    enrollUnmodifiedStudentsLines.put(unmodifiedStudent.email, "Existing student is unmodified.");
                }
                break;
            default:
                log.severe("Unknown Enrollment status " + i);
                break;
            }
        }
    }
}
