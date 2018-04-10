package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.ui.template.EnrollResultPanel;

/**
 * PageData: page data for the 'Result' page after enrollment for a course.
 */
public class InstructorCourseEnrollResultPageData extends PageData {

    private static final Logger log = Logger.getLogger();

    private String courseId;
    private List<StudentAttributes>[] students;
    private boolean hasSection;
    private String enrollStudents;
    private List<EnrollResultPanel> enrollResultPanelList;

    public InstructorCourseEnrollResultPageData(AccountAttributes account, String sessionToken, String courseId,
                                                List<StudentAttributes>[] students, boolean hasSection,
                                                String enrollStudents) {
        super(account, sessionToken);
        this.courseId = courseId;
        this.students = students;
        this.hasSection = hasSection;
        this.enrollStudents = enrollStudents;
        enrollResultPanelList = new ArrayList<>();

        for (int i = 0; i < StudentUpdateStatus.STATUS_COUNT; i++) {
            String panelClass = "";

            switch (StudentUpdateStatus.enumRepresentation(i)) {
            case ERROR :
                panelClass = "panel-danger";
                break;
            case NEW :
                panelClass = "panel-primary";
                break;
            case MODIFIED :
                panelClass = "panel-warning";
                break;
            case UNMODIFIED :
                panelClass = "panel-info";
                break;
            case NOT_IN_ENROLL_LIST :
                panelClass = "panel-default";
                break;
            case UNKNOWN :
                panelClass = "panel-danger";
                break;
            default :
                log.severe("Unknown Enrollment status " + i);
                break;
            }

            String messageForEnrollmentStatus = getMessageForEnrollmentStatus(i);
            EnrollResultPanel enrollResultPanel = new EnrollResultPanel(panelClass, messageForEnrollmentStatus, students[i]);
            enrollResultPanelList.add(enrollResultPanel);
        }
    }

    public String getCourseId() {
        return courseId;
    }

    public boolean isHasSection() {
        return hasSection;
    }

    public String getEnrollStudents() {
        return enrollStudents;
    }

    public List<EnrollResultPanel> getEnrollResultPanelList() {
        return enrollResultPanelList;
    }

    private String getMessageForEnrollmentStatus(int enrollmentStatus) {

        StudentUpdateStatus status = StudentUpdateStatus.enumRepresentation(enrollmentStatus);

        switch (status) {
        case ERROR:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_ERROR,
                    students[StudentUpdateStatus.ERROR.numericRepresentation].size());
        case NEW:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_ADDED,
                    students[StudentUpdateStatus.NEW.numericRepresentation].size());
        case MODIFIED:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_MODIFIED,
                    students[StudentUpdateStatus.MODIFIED.numericRepresentation].size());
        case UNMODIFIED:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_UNMODIFIED,
                    students[StudentUpdateStatus.UNMODIFIED.numericRepresentation].size());
        case NOT_IN_ENROLL_LIST:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_NOT_IN_LIST,
                    students[StudentUpdateStatus.NOT_IN_ENROLL_LIST.numericRepresentation].size());
        case UNKNOWN:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_UNKNOWN,
                    students[StudentUpdateStatus.UNKNOWN.numericRepresentation].size());
        default:
            log.severe("Unknown Enrollment status " + enrollmentStatus);
            return "There are students:";
        }
    }

    public String getInstructorCourseEnrollLink() {
        return getInstructorCourseEnrollLink(courseId);
    }
}
