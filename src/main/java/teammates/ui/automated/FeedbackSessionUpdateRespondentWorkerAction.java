package teammates.ui.automated;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Logger;

/**
 * Task queue worker action: appends to or removes from the respondent list of a feedback session.
 */
public class FeedbackSessionUpdateRespondentWorkerAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected String getActionDescription() {
        return null;
    }

    @Override
    protected String getActionMessage() {
        return null;
    }

    @Override
    public void execute() {
        String courseId = getRequestParamValue(ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(ParamsNames.COURSE_ID, courseId);
        String feedbackSessionName = getRequestParamValue(ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        String email = getRequestParamValue(ParamsNames.RESPONDENT_EMAIL);
        Assumption.assertPostParamNotNull(ParamsNames.RESPONDENT_EMAIL, email);
        String isInstructorString = getRequestParamValue(ParamsNames.RESPONDENT_IS_INSTRUCTOR);
        Assumption.assertPostParamNotNull(ParamsNames.RESPONDENT_IS_INSTRUCTOR, isInstructorString);
        String isToBeRemovedString = getRequestParamValue(ParamsNames.RESPONDENT_IS_TO_BE_REMOVED);
        Assumption.assertPostParamNotNull(ParamsNames.RESPONDENT_IS_TO_BE_REMOVED, isToBeRemovedString);

        boolean isInstructor = Boolean.parseBoolean(isInstructorString);
        boolean isToBeRemoved = Boolean.parseBoolean(isToBeRemovedString);

        try {
            if (isInstructor) {
                if (isToBeRemoved) {
                    logic.deleteInstructorRespondent(email, feedbackSessionName, courseId);
                } else {
                    logic.addInstructorRespondent(email, feedbackSessionName, courseId);
                }
            } else {
                if (isToBeRemoved) {
                    logic.deleteStudentRespondent(email, feedbackSessionName, courseId);
                } else {
                    logic.addStudentRespondent(email, feedbackSessionName, courseId);
                }
            }
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Failed to " + (isToBeRemoved ? "remove" : "append") + " "
                    + (isInstructor ? "instructor" : "student") + " respondent: "
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }
}
