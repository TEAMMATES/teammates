package teammates.sqlui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.request.Intent;
import teammates.ui.webapi.SubmitFeedbackResponsesAction;

import java.time.Instant;
import java.util.Map;

/**
 * SUT: {@link SubmitFeedbackResponsesAction}
 * **/

public class SubmitFeedbackResponsesActionTest extends BaseActionTest<SubmitFeedbackResponsesAction>
{

    private Instructor instructor;
    private FeedbackSessionAttributes feedbackSessionAttributes;

    @Override
    String getActionUri()
    {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    String getRequestMethod()
    {
        return PUT;
    }

    @BeforeMethod
    protected void setUp()
    {
        this.instructor = getTypicalInstructor();
        this.feedbackSessionAttributes = getTypicalFeedbackSessionAttributes();
    }


//    private void setEndTime(FeedbackSessionAttributes session, int days)
//            throws InvalidParametersException, EntityDoesNotExistException
//    {
//        String sessionName = session.getFeedbackSessionName();
//        String courseId = session.getCourseId();
//        Instant endTime = TimeHelper.getInstantDaysOffsetFromNow(days);
//
//        logic.updateFeedbackSession(
//                FeedbackSessionAttributes.updateOptionsBuilder(sessionName, courseId)
//                        .withEndTime(endTime)
//                        .build());
//    }
//
//    private void setInstructorDeadline(FeedbackSessionAttributes session,
//            InstructorAttributes instructor,
//            int days)
//            throws InvalidParametersException, EntityDoesNotExistException {
//        String sessionName = session.getFeedbackSessionName();
//        String courseId = session.getCourseId();
//
//        Map<String, Instant> deadlines = Map.of(instructor.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(days));
//
//        logic.updateFeedbackSession(
//                FeedbackSessionAttributes.updateOptionsBuilder(sessionName, courseId)
//                        .withInstructorDeadlines(deadlines)
//                        .build());
//    }
//
//    private String[] buildSubmissionParams(FeedbackSessionAttributes session,
//            int questionNumber,
//            Intent intent) {
//        FeedbackQuestionAttributes question = getQuestion(session, questionNumber);
//        return buildSubmissionParams(question, intent);
//    }
//
//    private String[] buildSubmissionParams(FeedbackQuestionAttributes question,
//            Intent intent) {
//        String questionId = question != null ? question.getId() : "";
//
//        return new String[] {Const.ParamsNames.FEEDBACK_QUESTION_ID, questionId, Const.ParamsNames.INTENT,
//                intent.toString()};
//    }
//
//    @Test
//    public void testAccessControl_feedbackSubmissionQuestionExists_shouldAllow() throws Exception {
//        FeedbackSessionAttributes session = getSession("session1InCourse2");
//        InstructorAttributes instructor = loginInstructor("instructor1OfCourse2");
//        setEndTime(session, 1);
//        setInstructorDeadline(session, instructor, 40);
//
//        int questionNumber = 2;
//        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);
//
//        verifyCanAccess(submissionParams);
//    }

    @Test
    public void testExecute_noHttpParameters_shouldFail() {
        loginAsInstructor(instructor.getGoogleId());

        verifyHttpParameterFailure(new String[] {});
    }

    @Test
    public void testExecute_noFeedbackQuestionId_shouldFail() {
        loginAsInstructor(instructor.getGoogleId());

        ______TS("Not enough parameters for request; should fail.");
        String[] submissionParams = new String[] {Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString()};
        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    public void testExecute_feedbackQuestionDoesNotExist_shouldFail() {
        loginAsInstructor(instructor.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "non-existent id"};
        verifyEntityNotFound(submissionParams);
    }
}
