package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.ui.template.EnrollResultPanel;

/**
 * PageData: page data for the 'Result' page after enrollment for a course
 */
public class InstructorCourseEnrollResultPageData extends PageData {
    protected static final Logger log = Utils.getLogger();
    
    private String courseId;
    private List<StudentAttributes>[] students;
    private boolean hasSection;
    private String enrollStudents;
    private List<EnrollResultPanel> enrollResultPanelList;
    
    public InstructorCourseEnrollResultPageData(AccountAttributes account, String courseId, 
                                                List<StudentAttributes>[] students, boolean hasSection, 
                                                String enrollStudents) {
        super(account);
        this.courseId = courseId;
        this.students = students;
        this.hasSection = hasSection;
        this.enrollStudents = enrollStudents;
        enrollResultPanelList = new ArrayList<EnrollResultPanel>();
        
        for (int i = 0; i < UpdateStatus.STATUS_COUNT; i++) {
            String panelClass = "";
            
            switch (UpdateStatus.enumRepresentation(i)) {
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
    
    public String getInstructorCourseEnrollLink() {
        return getInstructorCourseEnrollLink(courseId);
    }
}
