package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * This action handles instructors who attempt to join a course after
 * the instructor has been forced to re-authenticate himself by
 * {@link InstructorCourseJoinAction}. This action does the actual
 * joining of the instructor to the course.
 */
public class InstructorCourseJoinAuthenticatedAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        Assumption.assertNotNull(regkey);

        String institute = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        gateKeeper.verifyLoggedInUserPrivileges();

        /* Process authentication for the instructor to join course */
        try {

            if (institute == null) {
                logic.joinCourseForInstructor(regkey, account.googleId);
            } else {
                logic.joinCourseForInstructor(regkey, account.googleId, institute);
            }

        } catch (JoinCourseException | InvalidParametersException e) {
            // Does not sanitize for html to allow insertion of mailto link
            setStatusForException(e, e.getMessage());
            log.info(e.getMessage());
        }

        /* Set status to be shown to admin */
        final String joinedCourseMsg = "Action Instructor Joins Course"
                + "<br>Google ID: " + account.googleId
                + "<br>Key : " + StringHelper.decrypt(regkey);
        if (statusToAdmin == null) {
            statusToAdmin = joinedCourseMsg;
        } else {
            statusToAdmin += "<br><br>" + joinedCourseMsg;
        }

        /* Create redirection to instructor's homepage */
        RedirectResult response = createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        InstructorAttributes instructor = logic.getInstructorForRegistrationKey(regkey);
        if (instructor != null) {
            response.addResponseParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, instructor.courseId);
        }

        return response;
    }
}
