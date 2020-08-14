package teammates.test.cases.webapi;

import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.ui.webapi.action.FeedbackSessionUpdateRespondentWorkerAction;

/**
 * SUT: {@link FeedbackSessionUpdateRespondentWorkerAction}.
 */
public class FeedbackSessionUpdateRespondentWorkerActionTest
        extends BaseActionTest<FeedbackSessionUpdateRespondentWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_UPDATE_RESPONDENT_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }

    @Override
    @Test
    public void testExecute() throws EntityDoesNotExistException {
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("typical case: new student respondent");

        StudentAttributes student = typicalBundle.students.get("student4InCourse1");

        verifyRespondentNotInSessionRespondentsList(session, student.email, false);

        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, session.getCourseId(),
                ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                ParamsNames.RESPONDENT_EMAIL, student.email,
                ParamsNames.RESPONDENT_IS_INSTRUCTOR, "false",
                ParamsNames.RESPONDENT_IS_TO_BE_REMOVED, "false",
        };

        FeedbackSessionUpdateRespondentWorkerAction action = getAction(submissionParams);
        action.execute();

        verifyRespondentInSessionRespondentsList(session, student.email, false);

        ______TS("typical case: new instructor respondent");

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor2OfCourse1");

        verifyRespondentNotInSessionRespondentsList(session, instructor.email, true);

        submissionParams = new String[] {
                ParamsNames.COURSE_ID, session.getCourseId(),
                ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                ParamsNames.RESPONDENT_EMAIL, instructor.email,
                ParamsNames.RESPONDENT_IS_INSTRUCTOR, "true",
                ParamsNames.RESPONDENT_IS_TO_BE_REMOVED, "false",
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
                ParamsNames.RESPONDENT_IS_TO_BE_REMOVED, "true",
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
                ParamsNames.RESPONDENT_IS_TO_BE_REMOVED, "true",
        };

        action = getAction(submissionParams);
        action.execute();

        verifyRespondentNotInSessionRespondentsList(session, instructor.email, true);
    }

    private void verifyRespondentInSessionRespondentsList(FeedbackSessionAttributes session, String respondentEmail,
            boolean isInstructor) {
        FeedbackSessionAttributes sessionInDb =
                logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());

        Set<String> respondingStudentList = sessionInDb.getRespondingStudentList();
        Set<String> respondingInstructorList = sessionInDb.getRespondingInstructorList();
        if (isInstructor) {
            assertFalse(respondingStudentList.contains(respondentEmail));
            assertTrue(respondingInstructorList.contains(respondentEmail));
        } else {
            assertTrue(respondingStudentList.contains(respondentEmail));
            assertFalse(respondingInstructorList.contains(respondentEmail));
        }
    }

    private void verifyRespondentNotInSessionRespondentsList(FeedbackSessionAttributes session, String respondentEmail,
            boolean isInstructor) {
        FeedbackSessionAttributes sessionInDb =
                logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());

        Set<String> respondingStudentList = sessionInDb.getRespondingStudentList();
        Set<String> respondingInstructorList = sessionInDb.getRespondingInstructorList();
        if (isInstructor) {
            assertFalse(respondingInstructorList.contains(respondentEmail));
        } else {
            assertFalse(respondingStudentList.contains(respondentEmail));
        }
    }

}
