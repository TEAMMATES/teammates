package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.SessionSubmissionData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetSessionSubmissionDataAction}.
 */
public class GetSessionSubmissionDataActionIT extends BaseActionIT<GetSessionSubmissionDataAction> {

    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        logoutUser();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_SUBMISSION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testExecute() {
        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        Student student = typicalBundle.students.get("student1InCourse1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("Student submission data contains questions, recipients, and responses");
        loginAsStudent(student.getGoogleId());
        GetSessionSubmissionDataAction action = getAction(buildParams(session, Intent.STUDENT_SUBMISSION));
        SessionSubmissionData output = getOutput(action);

        assertFalse(output.getQuestions().isEmpty());
        assertTrue(output.getQuestions().stream().allMatch(question -> question.getQuestion().getQuestionNumber() > 0));
        assertTrue(output.getQuestions().stream().anyMatch(question -> !question.getRecipients().isEmpty()));
        assertTrue(output.getQuestions().stream().anyMatch(question -> !question.getResponses().isEmpty()));

        ______TS("Instructor submission data contains instructor questions and recipients");
        loginAsInstructor(instructor);
        action = getAction(buildParams(session, Intent.INSTRUCTOR_SUBMISSION));
        output = getOutput(action);

        assertFalse(output.getQuestions().isEmpty());
        assertTrue(output.getQuestions().stream().anyMatch(question -> !question.getRecipients().isEmpty()));

        ______TS("Preview mode returns recipients but no responses");
        String[] previewParams = buildParams(session, Intent.STUDENT_SUBMISSION,
                Const.ParamsNames.PREVIEWAS, student.getId().toString());
        action = getAction(previewParams);
        output = getOutput(action);

        assertTrue(output.getQuestions().stream().anyMatch(question -> !question.getRecipients().isEmpty()));
        assertTrue(output.getQuestions().stream().allMatch(question -> question.getResponses().isEmpty()));

        ______TS("Moderation filters questions instructors cannot see");
        String[] moderationParams = buildParams(session, Intent.INSTRUCTOR_SUBMISSION,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, instructor.getId().toString());
        action = getAction(moderationParams);
        output = getOutput(action);

        assertFalse(output.getQuestions().isEmpty());
        assertTrue(output.getQuestions().stream().allMatch(question -> canInstructorSee(question.getQuestion()
                .getFeedbackQuestionId())));
        assertEquals(1, output.getQuestions().get(0).getQuestion().getQuestionNumber());

        ______TS("Invalid intent");
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(buildParams(session, Intent.FULL_DETAIL));
        assertEquals("Unknown intent " + Intent.FULL_DETAIL, ihpe.getMessage());

        ______TS("Missing session");
        String[] missingSessionParams = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, UUID.randomUUID().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        EntityNotFoundException enfe = verifyEntityNotFound(missingSessionParams);
        assertEquals("Feedback session not found", enfe.getMessage());
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] studentSubmissionParams = buildParams(session, Intent.STUDENT_SUBMISSION);

        ______TS("Student submission access");
        verifyInaccessibleWithoutLogin(studentSubmissionParams);
        verifyInaccessibleForUnregisteredUsers(studentSubmissionParams);
        verifyAccessibleForStudentsOfTheSameCourse(course, studentSubmissionParams);
        verifyInaccessibleForStudentsOfOtherCourse(course, studentSubmissionParams);

        ______TS("Instructor submission access");
        String[] instructorSubmissionParams = buildParams(session, Intent.INSTRUCTOR_SUBMISSION);
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor);
        verifyCanAccess(instructorSubmissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(course, instructorSubmissionParams);
    }

    private SessionSubmissionData getOutput(GetSessionSubmissionDataAction action) {
        JsonResult result = getJsonResult(action);
        return (SessionSubmissionData) result.getOutput();
    }

    private String[] buildParams(FeedbackSession session, Intent intent, String... additionalParams) {
        String[] params = new String[4 + additionalParams.length];
        params[0] = Const.ParamsNames.FEEDBACK_SESSION_ID;
        params[1] = session.getId().toString();
        params[2] = Const.ParamsNames.INTENT;
        params[3] = intent.toString();
        System.arraycopy(additionalParams, 0, params, 4, additionalParams.length);
        return params;
    }

    private boolean canInstructorSee(UUID questionId) {
        return inTransaction(() -> {
            FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(questionId);
            return feedbackQuestion.getShowResponsesTo().contains(FeedbackVisibilityType.INSTRUCTORS)
                    && feedbackQuestion.getShowGiverNameTo().contains(FeedbackVisibilityType.INSTRUCTORS)
                    && feedbackQuestion.getShowRecipientNameTo().contains(FeedbackVisibilityType.INSTRUCTORS);
        });
    }
}
