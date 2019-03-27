package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.CourseEnrollmentResult;
import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.ui.template.EnrollResultPanel;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: saving the list of enrolled students for a course of an instructor.
 */
public class PostCourseEnrollSaveAction extends Action {
    private static final Logger log = Logger.getLogger();

    @Override
    protected AuthType getMinAuthLevel() {
        return authType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
    }

    @Override
    public ActionResult execute() throws EntityNotFoundException {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentsInfo = getRequestBody();

        /* Process enrollment list and setup data for page result */
        try {
            List<StudentAttributes>[] students = enrollAndProcessResultForDisplay(studentsInfo, courseId);
            EnrollResults dataFormat = new EnrollResults(getInstructorCourseEnrollResult(students));
            return new JsonResult(dataFormat);

        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);

        } catch (EnrollException | InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);

        } catch (EntityAlreadyExistsException e) {
            log.severe("Entity already exists exception occurred when updating student: " + e.getMessage());

            return new JsonResult("The enrollment failed, possibly because some students were re-enrolled before "
                            + "the previous enrollment action was still being processed by TEAMMATES database "
                            + "servers. Please try again after about 10 minutes. If the problem persists, "
                            + "please contact TEAMMATES support", HttpStatus.SC_CONFLICT);
        }
    }

    private List<StudentAttributes>[] enrollAndProcessResultForDisplay(String studentsInfo, String courseId)
            throws EnrollException, EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException {
        CourseEnrollmentResult enrollResult = logic.enrollStudents(studentsInfo, courseId);
        List<StudentAttributes> students = enrollResult.studentList;

        students.sort(Comparator.comparing(obj -> obj.updateStatus.numericRepresentation));

        return separateStudents(students);
    }

    /**
     * Separate the StudentData objects in the list into different categories based
     * on their updateStatus. Each category is put into a separate list.
     *
     * @return An array of lists of StudentAttributes objects in which each list contains
     *         student with the same updateStatus
     */
    @SuppressWarnings("unchecked")
    private List<StudentAttributes>[] separateStudents(List<StudentAttributes> students) {

        List<StudentAttributes>[] lists = new ArrayList[StudentUpdateStatus.STATUS_COUNT];
        for (int i = 0; i < StudentUpdateStatus.STATUS_COUNT; i++) {
            lists[i] = new ArrayList<>();
        }

        for (StudentAttributes student : students) {
            lists[student.updateStatus.numericRepresentation].add(student);
        }

        for (int i = 0; i < StudentUpdateStatus.STATUS_COUNT; i++) {
            StudentAttributes.sortByNameAndThenByEmail(lists[i]);
        }

        return lists;
    }

    private List<EnrollResultPanel> getInstructorCourseEnrollResult(List<StudentAttributes>[] students) {
        List<EnrollResultPanel> enrollResultPanelList = new ArrayList<>();

        for (int i = 0; i < StudentUpdateStatus.STATUS_COUNT; i++) {
            String panelClass = "";

            switch (StudentUpdateStatus.enumRepresentation(i)) {
            case ERROR :
                panelClass = "bg-danger";
                break;
            case NEW :
                panelClass = "bg-primary";
                break;
            case MODIFIED :
                panelClass = "bg-warning";
                break;
            case UNMODIFIED :
                panelClass = "bg-info";
                break;
            case NOT_IN_ENROLL_LIST :
                panelClass = "bg-default";
                break;
            case UNKNOWN :
                panelClass = "bg-danger";
                break;
            default :
                log.severe("Unknown Enrollment status " + i);
                break;
            }

            String messageForEnrollmentStatus = getMessageForEnrollmentStatus(i, students);
            EnrollResultPanel enrollResultPanel = new EnrollResultPanel(panelClass, messageForEnrollmentStatus, students[i]);
            enrollResultPanelList.add(enrollResultPanel);
        }
        return enrollResultPanelList;
    }

    private String getMessageForEnrollmentStatus(int enrollmentStatus, List<StudentAttributes>[] students) {

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

    /**
     * Data format for {@link PostCourseEnrollSaveAction}.
     */
    public static class EnrollResults extends ApiOutput {
        private final List<EnrollResultPanel> enrollResultPanelList;

        public EnrollResults(List<EnrollResultPanel> enrollResultPanelList) {
            this.enrollResultPanelList = enrollResultPanelList;
        }

        public List<EnrollResultPanel> getEnrollResultPanelList() {
            return enrollResultPanelList;
        }
    }
}

