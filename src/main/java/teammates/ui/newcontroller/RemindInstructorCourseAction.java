package teammates.ui.newcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.common.util.Url;

/**
 * Action: remind a specific student in a course .
 */
public class RemindInstructorCourseAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
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
            gateKeeper.verifyAccessible(
                    instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        } else if (isSendingToInstructor) {
            gateKeeper.verifyAccessible(
                    instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        } else {
            // this is sending registration emails to all students in the course and we will check if the instructor
            // canmodifystudent for course level since for modifystudent privilege there is only course level setting for now
            gateKeeper.verifyAccessible(
                    instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        }

    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException(
                new EntityDoesNotExistException("Course with ID " + courseId + " does not exist!")
            );
        }

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String previousPage = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_REMIND_STUDENT_IS_FROM);

        boolean isSendingToStudent = studentEmail != null;
        boolean isSendingToInstructor = instructorEmail != null;

        StringBuilder statusMessage = new StringBuilder();
        String redirectUrl = "";
        if (isSendingToStudent) {
            taskQueuer.scheduleCourseRegistrationInviteToStudent(courseId, studentEmail, false);
            StudentAttributes studentData = logic.getStudentForEmail(courseId, studentEmail);
            if (studentData == null) {
                throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Student with email " + studentEmail + " does not exist "
                        + "in course " + courseId + "!")
                );
            }

            statusMessage.append(Const.StatusMessages.COURSE_REMINDER_SENT_TO).append(studentEmail);

            boolean isRequestedFromCourseDetailsPage =
                    Const.PageNames.INSTRUCTOR_COURSE_DETAILS_PAGE.equals(previousPage);
            redirectUrl = isRequestedFromCourseDetailsPage
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

            statusMessage.append(Const.StatusMessages.COURSE_REMINDER_SENT_TO).append(instructorEmail);
            redirectUrl = Const.ResourceURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
        } else {
            List<StudentAttributes> studentDataList = logic.getUnregisteredStudentsForCourse(courseId);
            for (StudentAttributes student : studentDataList) {
                taskQueuer.scheduleCourseRegistrationInviteToStudent(course.getId(), student.getEmail(), false);
            }
            statusMessage.append(Const.StatusMessages.COURSE_REMINDER_SENT_TO);
            redirectUrl = Const.ResourceURIs.INSTRUCTOR_COURSE_DETAILS;
        }

        RedirectInfo output = new RedirectInfo(redirectUrl, statusMessage.toString());
        output.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        output.updateUrl();

        return new JsonResult(output);
    }

    /**
     * Data format for {@link RemindInstructorCourseAction}.
     */
    public class RedirectInfo extends ActionResult.ActionOutput {
        private final String redirectUrl;
        private final String statusMessage;
        private Map<String, String> responseParams = new HashMap<>();

        public RedirectInfo(String redirectUrl, String statusMessage) {
            this.redirectUrl = redirectUrl;
            this.statusMessage = statusMessage;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public String getStatusMessage() {
            return statusMessage;
        }

        /**
         * Returns url of the result, including parameters.
         *         e.g. {@code /instructorHome?courseid?=abc}
         */
        public void addResponseParam(String key, String value) {
            responseParams.put(key, value);
        }

        /**
         * Add a (key,value) pair ot the list of response parameters.
         */
        public void updateUrl() {
            appendParameters(redirectUrl, responseParams);
        }

        private String appendParameters(String url, Map<String, String> params) {
            String returnValue = url;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                returnValue = Url.addParamToUrl(returnValue, entry.getKey(), entry.getValue());
            }
            return returnValue;
        }
    }
}
