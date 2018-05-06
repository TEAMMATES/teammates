package teammates.ui.controller;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;

/**
 * Action: remind instructor or student to register for a course by sending reminder emails.
 */
public class InstructorCourseRemindAction extends Action {

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException("Course with ID " + courseId + " does not exist!");
        }

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        String previousPage = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_REMIND_STUDENT_IS_FROM);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
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

        /* Process sending emails and setup status to be shown to user and admin */
        Map<String, JoinEmailData> emailDataMap = new TreeMap<>();

        String redirectUrl = "";
        if (isSendingToStudent) {
            taskQueuer.scheduleCourseRegistrationInviteToStudent(courseId, studentEmail, false);
            StudentAttributes studentData = logic.getStudentForEmail(courseId, studentEmail);
            if (studentData == null) {
                throw new EntityDoesNotExistException("Student with email " + studentEmail + " does not exist "
                                                      + "in course " + courseId + "!");
            }
            emailDataMap.put(studentEmail,
                             new JoinEmailData(studentData.getName(), extractStudentRegistrationKey(studentData)));

            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_REMINDER_SENT_TO + studentEmail,
                                               StatusMessageColor.SUCCESS));

            boolean isRequestedFromCourseDetailsPage =
                    Const.PageNames.INSTRUCTOR_COURSE_DETAILS_PAGE.equals(previousPage);
            redirectUrl = isRequestedFromCourseDetailsPage
                    ? Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE
                    : Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE;
        } else if (isSendingToInstructor) {
            taskQueuer.scheduleCourseRegistrationInviteToInstructor(loggedInUser.googleId,
                    instructorEmail, courseId);

            InstructorAttributes instructorData = logic.getInstructorForEmail(courseId, instructorEmail);
            if (instructorData == null) {
                throw new EntityDoesNotExistException("Instructor with email " + instructorEmail + " does not exist "
                                                      + "in course " + courseId + "!");
            }

            emailDataMap.put(instructorEmail,
                    new JoinEmailData(instructorData.getName(), StringHelper.encrypt(instructorData.key)));

            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_REMINDER_SENT_TO + instructorEmail,
                                               StatusMessageColor.SUCCESS));
            redirectUrl = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
        } else {
            List<StudentAttributes> studentDataList = logic.getUnregisteredStudentsForCourse(courseId);
            for (StudentAttributes student : studentDataList) {
                taskQueuer.scheduleCourseRegistrationInviteToStudent(course.getId(), student.getEmail(), false);
                emailDataMap.put(student.getEmail(),
                        new JoinEmailData(student.getName(), extractStudentRegistrationKey(student)));
            }

            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_REMINDERS_SENT, StatusMessageColor.SUCCESS));
            redirectUrl = Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE;
        }

        statusToAdmin = generateStatusToAdmin(emailDataMap, courseId);

        /* Create redirection with URL based on type of sending email */
        RedirectResult response = createRedirectResult(redirectUrl);
        response.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);

        return response;

    }

    private String generateStatusToAdmin(Map<String, JoinEmailData> emailDataMap, String courseId) {
        StringBuilder statusToAdmin = new StringBuilder(200);
        statusToAdmin.append("Registration Key sent to the following users in Course <span class=\"bold\">[")
                     .append(courseId)
                     .append("]</span>:<br>");

        emailDataMap.forEach((userEmail, joinEmailData) -> statusToAdmin.append(joinEmailData.userName)
                         .append("<span class=\"bold\"> (").append(userEmail).append(")</span>.<br>")
                         .append(joinEmailData.regKey).append("<br>"));

        return statusToAdmin.toString();
    }

    private String extractStudentRegistrationKey(StudentAttributes student) {
        String joinLink = Config.getAppUrl(student.getRegistrationUrl()).toAbsoluteString();
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
