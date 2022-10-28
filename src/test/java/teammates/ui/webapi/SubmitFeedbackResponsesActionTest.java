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

    /**
     * Test if the feedback is submitted or not, if it is submitted, student can not modify the feedback again.
     * @author Zeyu Li
     */
    @Test
    public void testAccessControl_studentAlreadySubmit_shouldAllowIfNotSubmit() throws Exception {
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
        if (session1InCourse1.isPublished()) {
            ______TS("Feedback is already submitted, submit feedback failed");
            verifyCannotAccess(submissionParams);
        }
        Map<String, Instant> newStudentSubmission = Map.of(
                student1InCourse1.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withStudentDeadlines(newStudentSubmission)
                .build());

        if (session1InCourse1.isPublished()) {
            ______TS("Feedback is already submitted, submit feedback failed");
            verifyCannotAccess(submissionParams);
        } else {
            ______TS("Feedback is not submitted yet, submit feedback successfully");
            verifyCanAccess(submissionParams);
        }
    }

    /**
     * Test if the feedback is submitted or not, if it is submitted, instructor can not modify the feedback again.
     * @author Zeyu Li
     */
    @Test
    public void testAccessControl_instructorAlreadySubmit_shouldAllowIfNotSubmit() throws Exception {
        int questionNumber = 4;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestionAttributes qn4InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        Instant newEndTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withEndTime(newEndTime)
                .build());
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn4InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        if (session1InCourse1.isPublished()) {
            ______TS("Feedback is already submitted, submit feedback failed");
            verifyCannotAccess(submissionParams);
        }
        Map<String, Instant> newInstructorSubmission = Map.of(
                instructor1OfCourse1.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withInstructorDeadlines(newInstructorSubmission)
                .build());

        if (session1InCourse1.isPublished()) {
            ______TS("Feedback is already submitted, submit feedback failed");
            verifyCannotAccess(submissionParams);
        } else {
            ______TS("Feedback is not submitted yet, submit feedback successfully");
            verifyCanAccess(submissionParams);
        }
    }
}
