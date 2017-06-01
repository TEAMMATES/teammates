package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorCourseEnrollPageData;

/**
 * Action: showing page to enroll students into a course for an instructor.
 */
public class InstructorCourseEnrollPageAction extends Action {

    @Override
    public ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentsInfo = getRequestParamValue(Const.ParamsNames.STUDENTS_ENROLLMENT_INFO);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        /* Setup page data for 'Enroll' page of a course */
        InstructorCourseEnrollPageData pageData =
                new InstructorCourseEnrollPageData(account, sessionToken, courseId, studentsInfo);

        statusToAdmin = String.format(Const.StatusMessages.ADMIN_LOG_INSTRUCTOR_COURSE_ENROLL_PAGE_LOAD,
                                      courseId);
        addDataLossWarningToStatusToUser(courseId);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageData);
    }

    private void addDataLossWarningToStatusToUser(String courseId) {
        if (hasExistingResponses(courseId)) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_ENROLL_POSSIBLE_DATA_LOSS,
                                               StatusMessageColor.WARNING));
        }
    }

    private boolean hasExistingResponses(String courseId) {
        return logic.hasResponsesForCourse(courseId);
    }

}
