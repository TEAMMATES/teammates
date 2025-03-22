package teammates.sqlui.webapi;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentCreateRequest;
import teammates.ui.request.Intent;
import teammates.ui.webapi.CreateFeedbackResponseCommentAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateFeedbackResponseCommentAction}.
 */
public class CreateFeedbackResponseCommentActionTest extends BaseActionTest<CreateFeedbackResponseCommentAction> {

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    private Course typicalCourse;
    private Instructor typicalInstructor;
    private Student typicalStudent;
    private FeedbackSession typicalFeedbackSession;
    private FeedbackResponse typicalFeedbackResponse;
    private FeedbackResponseCommentCreateRequest typicalRequestBody;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        mockHibernateUtil.when(HibernateUtil::flushSession).thenAnswer(Answers.RETURNS_DEFAULTS);

        typicalCourse = getTypicalCourse();
        typicalInstructor = getTypicalInstructor();
        typicalStudent = getTypicalStudent();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        FeedbackQuestion typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(typicalFeedbackQuestion);
    }

    @AfterMethod
    public void tearDown() {
        mockHibernateUtil.close();
    }

    @Test
    void testExecute_emptyHttpParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_unencryptedResponseId_throwsInvalidHttpParameterException() {
        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalFeedbackResponse.getId().toString(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_nullComment_throwsInvalidHttpRequestBodyException() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                null,
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.GIVER));

        verifyHttpRequestBodyFailure(typicalRequestBody, params);
    }

    @Test
    void testExecute_emptyComment_throwsInvalidHttpRequestBodyException() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.GIVER));

        verifyHttpRequestBodyFailure(typicalRequestBody, params);
    }

    @Test
    void testExecute_invalidIntent_throwsInvalidHttpParameterException() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "invalid intent",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS));

        verifyHttpParameterFailure(typicalRequestBody, params);
    }

    @Test
    void testExecute_commentAlreadyExist_throwsInvalidOperationException() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "invalid intent",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS));

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenThrow(EntityAlreadyExistsException.class);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyInvalidOperation(typicalRequestBody, params);
    }

    @Test
    void testExecute_unpublishedSessionForInstructorResult_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "Comment to first response",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.GIVER));

        FeedbackResponseComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenReturn(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateFeedbackResponseCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();

        assertEquals("Comment to first response", commentData.getFeedbackCommentText());
        assertEquals(typicalInstructor.getEmail(), commentData.getCommentGiver());
    }

    @Test
    void testExecute_unpublishedSessionEmptyGiverPermission_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "Empty giver permissions",
                new ArrayList<>(),
                new ArrayList<>());

        FeedbackResponseComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenReturn(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateFeedbackResponseCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();

        assertEquals("Empty giver permissions", commentData.getFeedbackCommentText());
    }

    @Test
    void testExecute_unpublishedSessionCommentShownToGiver_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "Comment shown to giver",
                Arrays.asList(CommentVisibilityType.GIVER),
                new ArrayList<>());

        FeedbackResponseComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenReturn(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateFeedbackResponseCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();

        assertEquals("Comment shown to giver", commentData.getFeedbackCommentText());
    }

    @Test
    void testExecute_unpublishedSessionCommentShownToRecipient_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "Comment shown to recipient",
                Arrays.asList(CommentVisibilityType.RECIPIENT),
                new ArrayList<>());

        FeedbackResponseComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenReturn(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateFeedbackResponseCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();

        assertEquals("Comment shown to recipient", commentData.getFeedbackCommentText());
    }

    @Test
    void testExecute_unpublishedSessionCommentShownToGiverTeam_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "Comment shown to giver team",
                Arrays.asList(CommentVisibilityType.GIVER_TEAM_MEMBERS),
                new ArrayList<>());

        FeedbackResponseComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenReturn(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateFeedbackResponseCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();

        assertEquals("Comment shown to giver team", commentData.getFeedbackCommentText());
    }

    @Test
    void testExecute_unpublishedSessionCommentShownToRecipientTeam_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "Comment shown to recipient team",
                Arrays.asList(CommentVisibilityType.RECIPIENT_TEAM_MEMBERS),
                new ArrayList<>());

        FeedbackResponseComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenReturn(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateFeedbackResponseCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();

        assertEquals("Comment shown to recipient team", commentData.getFeedbackCommentText());
    }

    @Test
    void testExecute_unpublishedSessionCommentShownToStudents_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "Comment shown to students",
                Arrays.asList(CommentVisibilityType.STUDENTS),
                new ArrayList<>());

        FeedbackResponseComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenReturn(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateFeedbackResponseCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();

        assertEquals("Comment shown to students", commentData.getFeedbackCommentText());
    }

    @Test
    void testExecute_publishedSessionForInstructorResult_success() throws Exception {
        typicalFeedbackSession.setResultsVisibleFromTime(Instant.now());
        assertTrue(typicalFeedbackSession.isPublished());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "Comment to first response, published session",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.GIVER));

        FeedbackResponseComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenReturn(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateFeedbackResponseCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();

        assertEquals("Comment to first response, published session", commentData.getFeedbackCommentText());
        assertEquals(typicalInstructor.getEmail(), commentData.getCommentGiver());
    }

    @Test
    void testExecute_typicalCaseForInstructorSubmission_success() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "Instructor submission comment",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS));

        FeedbackResponseComment typicalComment = getTypicalCommentForInstructorSubmission();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenReturn(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateFeedbackResponseCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();

        assertEquals("Instructor submission comment", commentData.getFeedbackCommentText());
        assertEquals(typicalInstructor.getEmail(), commentData.getCommentGiver());
    }

    @Test
    void testExecute_typicalCaseForStudentSubmission_success() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        typicalRequestBody = new FeedbackResponseCommentCreateRequest(
                "Student submission comment",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS));

        FeedbackResponseComment typicalComment = getTypicalCommentForStudentSubmission();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);
        when(mockLogic.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenReturn(typicalComment);

        loginAsStudent(typicalStudent.getGoogleId());

        CreateFeedbackResponseCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();

        assertEquals("Student submission comment", commentData.getFeedbackCommentText());
        assertEquals(typicalStudent.getEmail(), commentData.getCommentGiver());
    }

    @Test
    void testAccessControl_typicalCaseForInstructorResult_canAccess() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        loginAsInstructor(typicalInstructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_contributionQuestionResponse_instructorNotAllowedToAddComment() {
        FeedbackQuestion contributionQuestion = FeedbackQuestion.makeQuestion(
                typicalFeedbackSession,
                2,
                "contribution question",
                FeedbackParticipantType.SELF,
                FeedbackParticipantType.TEAMS,
                2,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new FeedbackContributionQuestionDetails("test contribution question"));

        FeedbackResponse contributionResponse = FeedbackResponse.makeResponse(
                contributionQuestion,
                "test giver",
                getTypicalSection(),
                "test recipient",
                getTypicalSection(),
                new FeedbackContributionResponseDetails());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(contributionResponse.getId().toString()),
        };

        when(mockLogic.getFeedbackResponse(contributionResponse.getId())).thenReturn(contributionResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyHttpParameterFailureAcl(params);
    }

    @Test
    void testAccessControl_invalidIntent_throwsInvalidHttpParameterException() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        verifyHttpParameterFailureAcl(params);
    }

    @Test
    void testAccessControl_submitCommentForOthersResponse_cannotAccess() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        loginAsStudent(typicalStudent.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutSubmitSessionInSectionsPrivilege_cannotAccess() {
        Instructor instructorWithoutAccess = getTypicalInstructor();
        instructorWithoutAccess.setPrivileges(new InstructorPrivileges(INSTRUCTOR_PERMISSION_ROLE_OBSERVER));

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutAccess.getGoogleId()))
                .thenReturn(instructorWithoutAccess);

        loginAsInstructor(instructorWithoutAccess.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorWithOnlyEitherPrivilege_cannotAccessCrossSectionComment() {
        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_logOut_cannotAccess() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        logoutUser();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_studentAccessInstructorResponse_cannotAccess() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        loginAsStudent(typicalStudent.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorInSameCourse_canAccess() {
        Instructor instructorInSameCourse = getTypicalInstructor();
        instructorInSameCourse.setEmail("instructor2@teammates.tmt");

        loginAsInstructor(instructorInSameCourse.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorInSameCourse.getGoogleId()))
                .thenReturn(instructorInSameCourse);

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_adminToMasqueradeAsInstructor_canAccess() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(any(String.class), any(String.class)))
                .thenReturn(typicalInstructor);

        loginAsAdmin();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(typicalFeedbackResponse.getId().toString()),
        };

        verifyCanMasquerade(typicalInstructor.getGoogleId(), params);
    }

    @Test
    void testAccessControl_instructorSubmissionPastEndTimeBeforeDeadLineWithinGracePeriod_canAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        FeedbackResponse feedbackResponse = FeedbackResponse.makeResponse(
                feedbackQuestion,
                typicalInstructor.getEmail(),
                getTypicalSection(),
                "test recipient",
                getTypicalSection(),
                getTypicalFeedbackResponseDetails());

        when(mockLogic.getFeedbackResponse(feedbackResponse.getId())).thenReturn(feedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        // mock the extended deadline to be 10 minutes ago with a 15 minute grace period
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalInstructor))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(10)));

        loginAsInstructor(typicalInstructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(feedbackResponse.getId().toString()),
        };

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorSubmissionPastDeadLine_cannotAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        FeedbackResponse feedbackResponse = FeedbackResponse.makeResponse(
                feedbackQuestion,
                typicalInstructor.getEmail(),
                getTypicalSection(),
                "test recipient",
                getTypicalSection(),
                getTypicalFeedbackResponseDetails());

        when(mockLogic.getFeedbackResponse(feedbackResponse.getId())).thenReturn(feedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalInstructor))
                .thenReturn(feedbackSessionPastEndTime.getEndTime());

        loginAsInstructor(typicalInstructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(feedbackResponse.getId().toString()),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_studentSubmissionPastEndTimeBeforeDeadlineWithinGracePeriod_canAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        feedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        FeedbackResponse feedbackResponse = FeedbackResponse.makeResponse(
                feedbackQuestion,
                typicalStudent.getEmail(),
                getTypicalSection(),
                "test recipient",
                getTypicalSection(),
                getTypicalFeedbackResponseDetails());

        when(mockLogic.getFeedbackResponse(feedbackResponse.getId())).thenReturn(feedbackResponse);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);

        // mock the extended deadline to be 10 minutes in the future
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalStudent))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(10)));

        loginAsStudent(typicalStudent.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(feedbackResponse.getId().toString()),
        };

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_studentSubmissionPastDeadline_cannotAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        feedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        FeedbackResponse feedbackResponse = FeedbackResponse.makeResponse(
                feedbackQuestion,
                typicalStudent.getEmail(),
                getTypicalSection(),
                "test recipient",
                getTypicalSection(),
                getTypicalFeedbackResponseDetails());

        when(mockLogic.getFeedbackResponse(feedbackResponse.getId())).thenReturn(feedbackResponse);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalStudent))
                .thenReturn(feedbackSessionPastEndTime.getEndTime());

        loginAsStudent(typicalStudent.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(), Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(feedbackResponse.getId().toString()),
        };

        verifyCannotAccess(params);
    }

    private FeedbackResponseComment getTypicalCommentForInstructorResult() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalFeedbackResponse,
                typicalInstructor.getEmail(),
                FeedbackParticipantType.INSTRUCTORS,
                typicalFeedbackResponse.getGiverSection(),
                typicalFeedbackResponse.getRecipientSection(),
                typicalRequestBody.getCommentText(),
                false,
                false,
                typicalRequestBody.getShowCommentTo(),
                typicalRequestBody.getShowGiverNameTo(),
                typicalInstructor.getEmail());
        feedbackResponseComment.setId((long) 1);
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getTypicalCommentForInstructorSubmission() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalFeedbackResponse,
                typicalInstructor.getEmail(),
                FeedbackParticipantType.INSTRUCTORS,
                typicalFeedbackResponse.getGiverSection(),
                typicalFeedbackResponse.getRecipientSection(),
                typicalRequestBody.getCommentText(),
                true,
                true,
                typicalRequestBody.getShowCommentTo(),
                typicalRequestBody.getShowGiverNameTo(),
                typicalInstructor.getEmail());
        feedbackResponseComment.setId((long) 2);
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getTypicalCommentForStudentSubmission() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalFeedbackResponse,
                typicalStudent.getEmail(),
                FeedbackParticipantType.STUDENTS,
                typicalFeedbackResponse.getGiverSection(),
                typicalFeedbackResponse.getRecipientSection(),
                typicalRequestBody.getCommentText(),
                true,
                true,
                typicalRequestBody.getShowCommentTo(),
                typicalRequestBody.getShowGiverNameTo(),
                typicalStudent.getEmail());
        feedbackResponseComment.setId((long) 3);
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackSession getFeedbackSessionPastEndTime() {
        return new FeedbackSession(
                typicalFeedbackSession.getName(),
                typicalFeedbackSession.getCourse(),
                typicalFeedbackSession.getCreatorEmail(),
                typicalFeedbackSession.getInstructions(),
                Instant.now().minus(Duration.ofHours(2)),
                Instant.now().minus(Duration.ofHours(1)),
                Instant.now().minus(Duration.ofHours(1)),
                Instant.now().minus(Duration.ofHours(1)),
                Duration.ofMinutes(15),
                false,
                false,
                false);
    }

}
