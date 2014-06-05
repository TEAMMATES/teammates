package teammates.ui.controller;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.util.Const;
import teammates.common.util.Utils;

/**
 * PageData: page data for the 'Result' page after enrollment for a course
 */
public class InstructorCourseEnrollResultPageData extends PageData {
    
    public InstructorCourseEnrollResultPageData(AccountAttributes account) {
        super(account);
    }

    protected static final Logger log = Utils.getLogger();
    
    public String courseId;

    public List<StudentAttributes>[] students;
    
    public boolean hasSection;

    public String enrollStudents;

    public String getMessageForEnrollmentStatus(int enrollmentStatus) {

        UpdateStatus status = UpdateStatus.enumRepresentation(enrollmentStatus);

        switch (status) {
        case ERROR:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_ERROR,
                    students[UpdateStatus.ERROR.numericRepresentation].size());
        case NEW:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_ADDED,
                    students[UpdateStatus.NEW.numericRepresentation].size());
        case MODIFIED:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_MODIFIED,
                    students[UpdateStatus.MODIFIED.numericRepresentation].size());
        case UNMODIFIED:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_UNMODIFIED,
                    students[UpdateStatus.UNMODIFIED.numericRepresentation].size());
        case NOT_IN_ENROLL_LIST:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_NOT_IN_LIST,
                    students[UpdateStatus.NOT_IN_ENROLL_LIST.numericRepresentation].size());
        case UNKNOWN:
            return String.format(Const.StatusMessages.COURSE_ENROLL_STUDENTS_UNKNOWN,
                    students[UpdateStatus.UNKNOWN.numericRepresentation].size());
        default:
            log.severe("Unknown Enrollment status " + enrollmentStatus);
            return "There are students:";
        }
    }

}
