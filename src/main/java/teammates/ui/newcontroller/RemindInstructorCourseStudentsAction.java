package teammates.ui.newcontroller;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Action: reminds all students in a course who have not joined.
 */
public class RemindInstructorCourseStudentsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException(
                new EntityDoesNotExistException("Course with ID " + courseId + " does not exist!")
            );
        }

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);

        boolean isSendingToStudent = studentEmail != null;
        boolean isSendingToInstructor = instructorEmail != null;
        if (isSendingToStudent) {
            gateKeeper.verifyAccessible(instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        } else if (isSendingToInstructor) {
            gateKeeper.verifyAccessible(instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        } else {
            // this is sending registration emails to all students in the course and we will check if the instructor
            // canmodifystudent for course level since for modifystudent privilege there is only course level setting for now
            gateKeeper.verifyAccessible(instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String previousPage = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_REMIND_STUDENT_IS_FROM);

        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException(
                new EntityDoesNotExistException("Course with ID " + courseId + " does not exist!")
            );
        }

        boolean isSendingToStudent = studentEmail != null;
        boolean isSendingToInstructor = instructorEmail != null;
        // this may not be necessary
        Map<String, RemindInstructorCourseStudentsAction.JoinEmailData> emailDataMap = new TreeMap<>();

        String statusMessage = "";
        String redirectUrl = "/web/instructor";

        if (isSendingToStudent) {
            taskQueuer.scheduleCourseRegistrationInviteToStudent(courseId, studentEmail, false);
            StudentAttributes studentData = logic.getStudentForEmail(courseId, studentEmail);
            if (studentData == null) {
                throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Student with email " + studentEmail + " does not exist "
                        + "in course " + courseId + "!")
                );
            }
            emailDataMap.put(studentEmail,
                    new RemindInstructorCourseStudentsAction.JoinEmailData(studentData.getName(), extractStudentRegistrationKey(studentData)));

            statusMessage += Const.StatusMessages.COURSE_REMINDER_SENT_TO + studentEmail;

            boolean isRequestedFromCourseDetailsPage = Const.PageNames.INSTRUCTOR_COURSE_DETAILS_PAGE.equals(previousPage);

            redirectUrl += isRequestedFromCourseDetailsPage
                    ? Const.ResourceURIs.INSTRUCTOR_COURSE_DETAILS
                    : Const.ResourceURIs.INSTRUCTOR_STUDENT_LIST_PAGE;
        } else if (isSendingToInstructor) {
            taskQueuer.scheduleCourseRegistrationInviteToInstructor(userInfo.id, instructorEmail, courseId, null, false);

            InstructorAttributes instructorData = logic.getInstructorForEmail(courseId, instructorEmail);
            if (instructorData == null) {
                throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Instructor with email " + instructorEmail + " does not exist "
                        + "in course " + courseId + "!")
                );
            }

            emailDataMap.put(instructorEmail,
                    new RemindInstructorCourseStudentsAction.JoinEmailData(instructorData.getName(), StringHelper.encrypt(instructorData.key)));

            statusMessage += Const.StatusMessages.COURSE_REMINDER_SENT_TO + instructorEmail;
            redirectUrl += Const.ResourceURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
        } else {
            List<StudentAttributes> studentDataList = logic.getUnregisteredStudentsForCourse(courseId);
            for (StudentAttributes student : studentDataList) {
                taskQueuer.scheduleCourseRegistrationInviteToStudent(course.getId(), student.getEmail(), false);
                emailDataMap.put(student.getEmail(),
                        new RemindInstructorCourseStudentsAction.JoinEmailData(student.getName(), extractStudentRegistrationKey(student)));
            }

            statusMessage += Const.StatusMessages.COURSE_REMINDERS_SENT;
            redirectUrl += Const.ResourceURIs.INSTRUCTOR_COURSE_DETAILS;
        }

        redirectUrl += "?" + Const.ParamsNames.COURSE_ID + "=" + courseId;
        RemindRedirectInfo output = new RemindRedirectInfo(redirectUrl, statusMessage);

        return new JsonResult(output);
    }

    /**
     * Data format for {@link RemindInstructorCourseStudentsAction}.
     */
    public static class RemindRedirectInfo extends ActionResult.ActionOutput {
        private final String redirectUrl;
        private final String statusMessage;

        public RemindRedirectInfo(String redirectUrl, String statusMessage) {
            this.redirectUrl = redirectUrl;
            this.statusMessage = statusMessage;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public String getStatusMessage() {
            return statusMessage;
        }
    }

    private String extractStudentRegistrationKey(StudentAttributes student) {
        String joinLink = Config.getFrontEndAppUrl(student.getRegistrationUrl()).toAbsoluteString();
        String keyParam = Const.ParamsNames.REGKEY + "=";
        int startIndex = joinLink.indexOf(keyParam) + keyParam.length();
        return joinLink.substring(startIndex);
    }

    private static class JoinEmailData {
        String userName;
        String regKey;

        JoinEmailData(String userName, String regKey) {
            this.userName = userName;
            this.regKey = regKey;
        }
    }
}