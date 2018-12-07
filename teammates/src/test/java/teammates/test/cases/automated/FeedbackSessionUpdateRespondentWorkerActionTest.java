package teammates.test.cases.automated;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.automated.FeedbackSessionUpdateRespondentWorkerAction;

/**
 * SUT: {@link FeedbackSessionUpdateRespondentWorkerAction}.
 */
public class FeedbackSessionUpdateRespondentWorkerActionTest extends BaseAutomatedActionTest {

    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_UPDATE_RESPONDENT_WORKER_URL;
    }

    @Test
    public void allTests() throws EntityDoesNotExistException {
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");

        ______TS("typical case: new student respondent");

        StudentAttributes student = dataBundle.students.get("student4InCourse1");

        verifyRespondentNotInSessionRespondentsList(session, student.email, false);

        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, session.getCourseId(),
                ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                ParamsNames.RESPONDENT_EMAIL, student.email,
                ParamsNames.RESPONDENT_IS_INSTRUCTOR, "false",
                ParamsNames.RESPONDENT_IS_TO_BE_REMOVED, "false"
        };

        FeedbackSessionUpdateRespondentWorkerAction action = getAction(submissionParams);
        action.execute();

        verifyRespondentInSessionRespondentsList(session, student.email, false);

        ______TS("typical case: new instructor respondent");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor2OfCourse1");

        verifyRespondentNotInSessionRespondentsList(session, instructor.email, true);

        submissionParams = new String[] {
                ParamsNames.COURSE_ID, session.getCourseId(),
                ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                ParamsNames.RESPONDENT_EMAIL, instructor.email,
                ParamsNames.RESPONDENT_IS_INSTRUCTOR, "true",
                ParamsNames.RESPONDENT_IS_TO_BE_REMOVED, "false"
        };

        action = getAction(submissionParams);
        action.execute();

        verifyRespondentInSessionRespondentsList(session, instructor.email, true);

        ______TS("typical case: deleted student respondent");

        verifyRespondentInSessionRespondentsList(session, student.email, false);

        submissionParams = new String[] {
                ParamsNames.COURSE_ID, session.getCourseId(),
                ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                ParamsNames.RESPONDENT_EMAIL, student.email,
                ParamsNames.RESPONDENT_IS_INSTRUCTOR, "false",
                ParamsNames.RESPONDENT_IS_TO_BE_REMOVED, "true"
        };

        action = getAction(submissionParams);
        action.execute();

        verifyRespondentNotInSessionRespondentsList(session, student.email, false);

        ______TS("typical case: deleted instructor respondent");

        verifyRespondentInSessionRespondentsList(session, instructor.email, true);

        submissionParams = new String[] {
                ParamsNames.COURSE_ID, session.getCourseId(),
                ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                ParamsNames.RESPONDENT_EMAIL, instructor.email,
                ParamsNames.RESPONDENT_IS_INSTRUCTOR, "true",
                ParamsNames.RESPONDENT_IS_TO_BE_REMOVED, "true"
        };

        action = getAction(submissionParams);
        action.execute();

        verifyRespondentNotInSessionRespondentsList(session, instructor.email, true);
    }

    @Override
    protected FeedbackSessionUpdateRespondentWorkerAction getAction(String... params) {
        return (FeedbackSessionUpdateRespondentWorkerAction)
                gaeSimulation.getAutomatedActionObject(getActionUri(), params);
    }

    private void verifyRespondentInSessionRespondentsList(FeedbackSessionAttributes session, String respondentEmail,
            boolean isInstructor) throws EntityDoesNotExistException {
        FeedbackSessionResponseStatus responseStatus =
                fsLogic.getFeedbackSessionResponseStatus(session.getFeedbackSessionName(), session.getCourseId());
        assertFalse(responseStatus.getStudentsWhoDidNotRespond().contains(respondentEmail));
        if (!isInstructor) {
            assertTrue(responseStatus.getStudentsWhoResponded().contains(respondentEmail));
        }
    }

    private void verifyRespondentNotInSessionRespondentsList(FeedbackSessionAttributes session, String respondentEmail,
            boolean isInstructor) throws EntityDoesNotExistException {
        FeedbackSessionResponseStatus responseStatus =
                fsLogic.getFeedbackSessionResponseStatus(session.getFeedbackSessionName(), session.getCourseId());
        assertTrue(responseStatus.getStudentsWhoDidNotRespond().contains(respondentEmail));
        if (!isInstructor) {
            assertFalse(responseStatus.getStudentsWhoResponded().contains(respondentEmail));
        }
    }

}
