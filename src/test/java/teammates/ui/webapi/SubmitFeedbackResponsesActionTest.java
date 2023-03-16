package teammates.ui.webapi;

import java.time.Instant;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.request.Intent;

/**
 * SUT: {@link SubmitFeedbackResponsesAction}.
 */
public class SubmitFeedbackResponsesActionTest extends BaseActionTest<SubmitFeedbackResponsesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    public void testExecute() {
        // See each independent test case.
    }

    @Override
    protected void testAccessControl() {
        // See each independent test case.
    }

    @Test
    public void testAccessControl_instructorSubmissionPastEndTime_shouldAllowIfBeforeDeadline() throws Exception {
        int questionNumber = 4;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestionAttributes qn4InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        Instant newEndTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withInstructorDeadlines(Map.of())
                .withEndTime(newEndTime)
                .build());
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn4InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        ______TS("No selective deadline; should fail.");

        verifyCannotAccess(submissionParams);

        ______TS("After selective deadline; should fail.");

        Map<String, Instant> newInstructorDeadlines = Map.of(
                instructor1OfCourse1.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withInstructorDeadlines(newInstructorDeadlines)
                .build());
        verifyCannotAccess(submissionParams);

        ______TS("Before selective deadline; should pass.");

        newInstructorDeadlines = Map.of(
                instructor1OfCourse1.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withInstructorDeadlines(newInstructorDeadlines)
                .build());
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionPastEndTime_shouldAllowIfBeforeDeadline() throws Exception {
        int questionNumber = 1;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        Instant newEndTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withEndTime(newEndTime)
                .build());
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("No selective deadline; should fail.");

        verifyCannotAccess(submissionParams);

        ______TS("After selective deadline; should fail.");

        Map<String, Instant> newStudentDeadlines = Map.of(
                student1InCourse1.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withStudentDeadlines(newStudentDeadlines)
                .build());
        verifyCannotAccess(submissionParams);

        ______TS("Before selective deadline; should pass.");

        newStudentDeadlines = Map.of(
                student1InCourse1.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withStudentDeadlines(newStudentDeadlines)
                .build());
        verifyCanAccess(submissionParams);
    }
    @Test
    public void testExecute_submitResponse() throws Exception {
        // set up the feedback session and question
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes feedbackQuestion = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");
    
        // set up the user who is submitting the response
        StudentAttributes student = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student.getGoogleId());

        // set up the submission parameters
        String[] submissionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestion.getId(),
                Const.ParamsNames.RESPONSE_TEXT, "This is my response."
         };

         // execute the action
         SubmitFeedbackResponsesAction action = getAction(submissionParams);
         ActionResult result = action.execute();
    
        // verify that the response was submitted successfully
        FeedbackQuestionAttributes updatedQuestion = logic.getFeedbackQuestion(feedbackSession.getFeedbackSessionName(),
        feedbackSession.getCourseId(), feedbackQuestion.getQuestionNumber());
         List<FeedbackResponseAttributes> responses = updatedQuestion.getResponses();
         FeedbackResponseAttributes response = responses.stream()
        .filter(r -> r.giverEmail.equals(student.getEmail()))
        .findFirst()
        .orElse(null);
         assertNotNull(response);
         assertEquals(submissionParams[Const.ParamsNames.RESPONSE_TEXT], response.getResponseText());
    
        // verify that the action succeeded
         assertEquals(Const.StatusCodes.OK, result.getStatusCode());
        }

        @Test
        public void testEmptySubmission() {
            // Define session and feedback question configuration
            FeedbackSession session = new FeedbackSession();
            FeedbackQuestion question = new FeedbackQuestion(1, "How did you find the lecture?", 
                FeedbackQuestion.Type.TEXT);
        
            // Define user and submission parameters
            User student = new User("John Doe");
            Map<String, String> submissionParams = new HashMap<>();
            submissionParams.put("questionId", "1");
            submissionParams.put("answerText", "");
        
            // Submit empty answer and verify that it fails
            SubmitFeedbackResponsesAction action = new SubmitFeedbackResponsesAction();
            action.setSession(session);
            action.setQuestion(question);
            action.setUser(student);
            action.setSubmissionParams(submissionParams);
            ActionResult result = action.execute();
            assertEquals(Const.StatusCodes.ERROR, result.getStatus());
        }
        
        @Test
        public void testClosedSession() {
            // Define session and feedback question configuration
            FeedbackSession session = new FeedbackSession();
            session.setStatus(FeedbackSession.Status.CLOSED);
            FeedbackQuestion question = new FeedbackQuestion(1, "How did you find the lecture?", 
                FeedbackQuestion.Type.TEXT);
        
            // Define user and submission parameters
            User student = new User("John Doe");
            Map<String, String> submissionParams = new HashMap<>();
            submissionParams.put("questionId", "1");
            submissionParams.put("answerText", "It was good");
        
            // Submit answer after session has closed and verify that it fails
            SubmitFeedbackResponsesAction action = new SubmitFeedbackResponsesAction();
            action.setSession(session);
            action.setQuestion(question);
            action.setUser(student);
            action.setSubmissionParams(submissionParams);
            ActionResult result = action.execute();
            assertEquals(Const.StatusCodes.ERROR, result.getStatus());
        }
        
}
