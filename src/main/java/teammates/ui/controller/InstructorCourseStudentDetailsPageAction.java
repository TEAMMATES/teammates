package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorCourseStudentDetailsPageData;

public class InstructorCourseStudentDetailsPageAction extends Action {

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, studentEmail);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_COURSE_DETAILS,
                                               StatusMessageColor.DANGER));
            isError = true;
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId), student.section,
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);

        boolean hasSection = logic.hasIndicatedSections(courseId);

        StudentProfileAttributes studentProfile = loadStudentProfile(student, instructor);

        InstructorCourseStudentDetailsPageData data =
                new InstructorCourseStudentDetailsPageData(account, sessionToken, student, studentProfile,
                                                           hasSection);

        statusToAdmin = "instructorCourseStudentDetails Page Load<br>"
                        + "Viewing details for Student <span class=\"bold\">" + studentEmail
                        + "</span> in Course <span class=\"bold\">[" + courseId + "]</span>";

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS, data);

    }

    private StudentProfileAttributes loadStudentProfile(StudentAttributes student, InstructorAttributes currentInstructor) {
        StudentProfileAttributes studentProfile = null;
        boolean isInstructorAllowedToViewStudent = currentInstructor.isAllowedForPrivilege(student.section,
                                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
        if (isInstructorAllowedToViewStudent && !student.googleId.isEmpty()) {
            studentProfile = logic.getStudentProfile(student.googleId);
        }

        // this means that the user is returning to the page and is not the first time
        boolean hasExistingStatus = !statusToUser.isEmpty()
                                        || session.getAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST) != null;
        if (studentProfile == null && !hasExistingStatus) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_NOT_CREATED,
                                               StatusMessageColor.WARNING));
        }
        if (!isInstructorAllowedToViewStudent && !hasExistingStatus) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_UNACCESSIBLE_TO_INSTRUCTOR,
                                               StatusMessageColor.WARNING));
        }
        return studentProfile;
    }
}
