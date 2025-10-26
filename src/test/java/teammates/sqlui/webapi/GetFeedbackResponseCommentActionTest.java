package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.Intent;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetFeedbackResponseCommentAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetFeedbackResponseCommentAction}.
 */
public class GetFeedbackResponseCommentActionTest extends BaseActionTest<GetFeedbackResponseCommentAction> {

    private static final String COURSE_1 = "course-1";
    private static final String COURSE_2 = "course-2";
    private static final String STUDENT_1 = "student-1";
    private static final String STUDENT_2 = "student-2";
    private static final String INSTRUCTOR_1 = "instructor-1";
    private static final String INSTRUCTOR_2 = "instructor-2";

    private Instructor instructorOfCourse1;
    private Instructor instructorOfCourse2;
    private FeedbackResponse responseForQ1;
    private FeedbackResponse responseForQ2;
    private FeedbackSession feedbackSessionInCourse1;
    private Student studentInCourse1;
    private Student studentInCourse2;
    private FeedbackResponseComment commentForQ1Response1;
    private FeedbackResponseComment commentForQ2Response1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        // setup courses and session
        Course course1 = generateCourse(COURSE_1);
        Course course2 = generateCourse(COURSE_2);
        feedbackSessionInCourse1 = generateSessionInCourse("feedbackSession-1", course1);

        // setup students and instructors
        studentInCourse1 = generateStudentInCourse(STUDENT_1, course1);
        studentInCourse2 = generateStudentInCourse(STUDENT_2, course2);
        instructorOfCourse1 = generateInstructorInCourse(INSTRUCTOR_1, course1);
        instructorOfCourse2 = generateInstructorInCourse(INSTRUCTOR_2, course2);

        // setup comments and responses
        FeedbackQuestion qn1InSession1InCourse1 = getTypicalFeedbackQuestionForSession(feedbackSessionInCourse1);
        qn1InSession1InCourse1.setGiverType(FeedbackParticipantType.STUDENTS);
        FeedbackQuestion qn2InSession1InCourse1 = getTypicalFeedbackQuestionForSession(feedbackSessionInCourse1);
        qn2InSession1InCourse1.setGiverType(FeedbackParticipantType.INSTRUCTORS);

        responseForQ1 = getTypicalFeedbackResponseForQuestion(qn1InSession1InCourse1);
        responseForQ2 = getTypicalFeedbackResponseForQuestion(qn2InSession1InCourse1);
        
        commentForQ1Response1 = generateComment(responseForQ1, STUDENT_1, FeedbackParticipantType.STUDENTS, "Student 1 Comment");
        commentForQ2Response1 = generateComment(responseForQ2, INSTRUCTOR_1, FeedbackParticipantType.INSTRUCTORS, "Instructor 1 Comment");
    }

    @Test
    void testExecute_notEnoughParameters_shouldFail() {
        loginAsInstructor(instructorOfCourse1.getGoogleId());

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString());
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {
        loginAsInstructor(instructorOfCourse1.getGoogleId());

        when(mockLogic.getFeedbackResponse(any())).thenReturn(responseForQ1);

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ1.getId().toString()),
        };
        verifyHttpParameterFailure(submissionParams);

        loginAsStudent(studentInCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ1.getId().toString()),
        };
        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    void testExecute_studentSubmissionTypicalSuccessCase_shouldPass() {
        loginAsStudent(studentInCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ1.getId().toString()),
        };

        FeedbackResponseComment expectedComment = commentForQ1Response1;
        when(mockLogic.getFeedbackResponse(any())).thenReturn(responseForQ1);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(any())).thenReturn(expectedComment);

        FeedbackResponseCommentData actualComment = getFeedbackResponseComments(submissionParams);
        assertEquals(actualComment.getFeedbackCommentText(), expectedComment.getCommentText());
        assertEquals(actualComment.getCommentGiver(), expectedComment.getGiver());
    }

    @Test
    void testExecute_instructorSubmissionTypicalSuccessCase_shouldPass() {
        loginAsStudent(studentInCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ2.getId().toString()),
        };

        FeedbackResponseComment expectedComment = commentForQ2Response1;
        when(mockLogic.getFeedbackResponse(any())).thenReturn(responseForQ2);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(any())).thenReturn(expectedComment);

        FeedbackResponseCommentData actualComment = getFeedbackResponseComments(submissionParams);
        assertEquals(actualComment.getFeedbackCommentText(), expectedComment.getCommentText());
        assertEquals(actualComment.getCommentGiver(), expectedComment.getGiver());
    }

    @Test
    void testExecute_commnetDoesNotExist_shouldFail() {
        loginAsStudent(studentInCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ1.getId().toString()),
        };

        when(mockLogic.getFeedbackResponse(any())).thenReturn(responseForQ1);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(any())).thenReturn(null);

        GetFeedbackResponseCommentAction action = getAction(submissionParams);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action, HttpStatus.SC_NO_CONTENT).getOutput();
        assertEquals("Comment not found", actionOutput.getMessage());
    }

    @Test
    void testExecute_responseDoesNotExist_shouldThrowException() {
        loginAsStudent(studentInCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ1.getId().toString()),
        };

        when(mockLogic.getFeedbackResponse(any())).thenReturn(null);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(any())).thenReturn(null);

        EntityNotFoundException enfe = verifyEntityNotFound(submissionParams);
        assertEquals("The feedback response does not exist.", enfe.getMessage());
    }

    @Test
    void testAccessControl() {
        loginAsStudent(studentInCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ1.getId().toString()),
        };

        when(mockLogic.getFeedbackSession(any(), any())).thenReturn(feedbackSessionInCourse1);
        when(mockLogic.getFeedbackResponse(any())).thenReturn(responseForQ1);
        when(mockLogic.getStudentByGoogleId(any(), any())).thenReturn(studentInCourse1);

        verifyCanAccess(submissionParams);

        loginAsInstructor(instructorOfCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ2.getId().toString()),
        };
        when(mockLogic.getFeedbackSession(any(), any())).thenReturn(feedbackSessionInCourse1);
        when(mockLogic.getFeedbackResponse(any())).thenReturn(responseForQ2);
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(instructorOfCourse1);

        verifyCanAccess(submissionParams);
    }

    @Test
    void testAccessControl_invalidIntent_shouldFail() {
        loginAsStudent(studentInCourse1.getGoogleId());
        String[] studentInvalidIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ1.getId().toString()),
        };

        when(mockLogic.getFeedbackSession(any(), any())).thenReturn(feedbackSessionInCourse1);
        when(mockLogic.getFeedbackResponse(any())).thenReturn(responseForQ1);

        verifyHttpParameterFailureAcl(studentInvalidIntentParams);

        loginAsInstructor(instructorOfCourse1.getGoogleId());
        String[] instructorInvalidIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ1.getId().toString()),
        };

        verifyHttpParameterFailureAcl(instructorInvalidIntentParams);
    }

    @Test
    void testAccessControl_responseDoesNotExist_shouldFail() {
        loginAsInstructor(instructorOfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt("responseIdOfNonExistingResponse"),
        };
        when(mockLogic.getFeedbackResponse(any())).thenReturn(null);

        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    void testAccessControl_accessAcrossCourses_shouldFail() {

        // instructor access other instructor's response from different course
        loginAsInstructor(instructorOfCourse2.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ2.getId().toString()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, instructorOfCourse2.getEmail(),
        };
        when(mockLogic.getFeedbackSession(any(), any())).thenReturn(feedbackSessionInCourse1);
        when(mockLogic.getFeedbackResponse(any())).thenReturn(responseForQ2);
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(instructorOfCourse2);

        verifyCannotAccess(submissionParams);

        // students access other students' response from different course
        loginAsStudent(studentInCourse2.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseForQ1.getId().toString()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, studentInCourse2.getEmail(),
        };
        when(mockLogic.getFeedbackSession(any(), any())).thenReturn(feedbackSessionInCourse1);
        when(mockLogic.getFeedbackResponse(any())).thenReturn(responseForQ1);
        when(mockLogic.getStudentByGoogleId(any(), any())).thenReturn(studentInCourse2);

        verifyCannotAccess(submissionParams);
    }

    private FeedbackResponseCommentData getFeedbackResponseComments(String[] params) {
        GetFeedbackResponseCommentAction action = getAction(params);
        JsonResult actualResult = getJsonResult(action);
        return (FeedbackResponseCommentData) actualResult.getOutput();
    }

    private Course generateCourse(String id) {
        Course c = new Course(id, "Typical Course " + id,
                "Africa/Johannesburg", "TEAMMATES Test Institute 0");
        c.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        c.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        return c;
    }

    private Student generateStudentInCourse(String id, Course course) {
        String email = id + "@gmail.com";
        String name = id;
        String googleId = id;
        Student s = new Student(course, name, email, "comment for " + id);
        s.setAccount(new Account(googleId, name, email));
        return s;
    }

    private Instructor generateInstructorInCourse(String id, Course course) {
        Instructor i = new Instructor(course, id,
                id + "@tm.tmt", false,
                "", null,
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER));
        Account a = getTypicalAccount();
        a.setGoogleId(id);
        i.setAccount(a);
        return i;
    }

    private FeedbackSession generateSessionInCourse(String feedbackSession, Course course) {
        FeedbackSession fs = new FeedbackSession(feedbackSession, course,
                "instructor1@gmail.com", "generic instructions",
                Instant.parse("2012-04-01T22:00:00Z"), Instant.parse("2027-04-30T22:00:00Z"),
                Instant.parse("2012-03-28T22:00:00Z"), Instant.parse("2027-05-01T22:00:00Z"),
                Duration.ofHours(10), true, true, true);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));

        return fs;
    }

    private FeedbackResponseComment generateComment(FeedbackResponse response, String author, FeedbackParticipantType participantType, String text) {
        FeedbackResponseComment comment = new FeedbackResponseComment(response, author + "@teammates.tmt",
                participantType, getTypicalSection(), getTypicalSection(),
                text, false, false,
                new ArrayList<>(), new ArrayList<>(), author + "@teammates.tmt");
        comment.setId((long) Math.random());
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());

        return comment;
    }
}
