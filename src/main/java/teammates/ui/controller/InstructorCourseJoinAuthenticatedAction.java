package teammates.ui.controller;

import java.time.ZoneId;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.SessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;

/**
 * This action handles instructors who attempt to join a course after
 * the instructor has been forced to re-authenticate himself by
 * {@link InstructorCourseJoinAction}. This action does the actual
 * joining of the instructor to the course.
 */
public class InstructorCourseJoinAuthenticatedAction extends CourseJoinAuthenticatedAbstractAction {

    private static final Logger log = Logger.getLogger();

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

        // When instructor is added by admin and only sample course
        // and sessions in that course are included and their timezones are updated
        if (institute != null) {
            String timeZone = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_TIMEZONE);
            List<CourseAttributes> courses = logic.getCoursesForInstructor(account.googleId);

            for (CourseAttributes course : courses) {
                try {
                    logic.updateCourse(course.getId(), course.getName(), timeZone);
                } catch (InvalidParametersException e) {
                    setStatusForException(e, e.getMessage());
                    log.info(e.getMessage());
                }
                List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsForCourse(course.getId());
                for(FeedbackSessionAttributes session : sessions) {
                    session.setTimeZone(ZoneId.of(timeZone));
                    try {
                        logic.updateFeedbackSession(session);
                    } catch (InvalidParametersException e) {
                        setStatusForException(e, e.getMessage());
                        log.info(e.getMessage());
                    }
                }
            }
        }

        /* Set status to be shown to admin */
        StringBuffer joinedCourseMsg = new StringBuffer(100);
        joinedCourseMsg.append("Action Instructor Joins Course<br>Google ID: ").append(account.googleId);
        try {
            joinedCourseMsg.append("<br>Key : ").append(StringHelper.decrypt(regkey));
        } catch (InvalidParametersException e) {
            joinedCourseMsg.append("<br>Key could not be decrypted.");
            // no need to do setStatusForException and logging, as this case is already caught above
        }

        if (statusToAdmin == null) {
            statusToAdmin = joinedCourseMsg.toString();
        } else {
            statusToAdmin += "<br><br>" + joinedCourseMsg.toString();
        }

        /* Create redirection to instructor's homepage */
        RedirectResult response = createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        InstructorAttributes instructor = logic.getInstructorForRegistrationKey(regkey);
        if (instructor != null) {
            response.addResponseParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, instructor.courseId);
            sendCourseRegisteredEmail(instructor.name, instructor.email, true, instructor.courseId);
        }

        return response;
    }
}
