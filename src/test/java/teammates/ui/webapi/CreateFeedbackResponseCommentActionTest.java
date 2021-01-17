package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.AssertHelper;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.FeedbackResponseCommentCreateRequest;
import teammates.ui.request.Intent;

/**
 * SUT: {@link CreateFeedbackResponseCommentAction}.
 */
public class CreateFeedbackResponseCommentActionTest extends BaseActionTest<CreateFeedbackResponseCommentAction> {

    private FeedbackSessionAttributes session1InCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private InstructorAttributes instructor2OfCourse1;
    private InstructorAttributes helperOfCourse1;
    private FeedbackResponseAttributes response1ForQ1;
    private FeedbackResponseAttributes response1ForQ2;
    private FeedbackResponseAttributes response1ForQ3;
    private FeedbackResponseAttributes response2ForQ3;
    private FeedbackResponseAttributes response2ForQ4;
    private FeedbackResponseAttributes response1ForQ5;
    private FeedbackResponseAttributes response1ForQ6;
    private StudentAttributes student1InCourse1;
    private StudentAttributes student2InCourse1;
    private StudentAttributes student3InCourse1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = loadDataBundle("/FeedbackResponseCommentCRUDTest.json");
        removeAndRestoreDataBundle(dataBundle);

        instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        instructor2OfCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        helperOfCourse1 = dataBundle.instructors.get("helperOfCourse1");
        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        student2InCourse1 = dataBundle.students.get("student2InCourse1");
        student3InCourse1 = dataBundle.students.get("student3InCourse1");
        session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        FeedbackQuestionAttributes qn2InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 2);
        FeedbackQuestionAttributes qn3InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 3);
        FeedbackQuestionAttributes qn4InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 4);
        FeedbackQuestionAttributes qn5InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 5);
        FeedbackQuestionAttributes qn6InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 6);
        response1ForQ1 = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());
        response1ForQ2 = logic.getFeedbackResponse(qn2InSession1InCourse1.getId(),
                instructor1OfCourse1.getEmail(), student1InCourse1.getEmail());
        response1ForQ3 = logic.getFeedbackResponse(qn3InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
        response2ForQ3 = logic.getFeedbackResponse(qn3InSession1InCourse1.getId(),
                student2InCourse1.getEmail(), student2InCourse1.getEmail());
        response2ForQ4 = logic.getFeedbackResponse(qn4InSession1InCourse1.getId(),
                student1InCourse1.getTeam(), student1InCourse1.getTeam());
        response1ForQ5 = logic.getFeedbackResponse(qn5InSession1InCourse1.getId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());
        response1ForQ6 = logic.getFeedbackResponse(qn6InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student3InCourse1.getEmail());
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        //see individual test cases.
    }

    @Test
    public void testExecute_invalidHttpParameters_shouldFail() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("not enough parameters");
        verifyHttpParameterFailure();

        ______TS("unencrypted responseId");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    public void testExecute_unpublishedSessionForInstructorResult_shouldPass() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("successful case for unpublished session for INSTRUCTOR_RESULT");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };
        FeedbackResponseCommentCreateRequest requestBody =
                new FeedbackResponseCommentCreateRequest("Comment to first response",
                        Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                        Arrays.asList(CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.GIVER));
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        JsonResult r = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();
        assertEquals("Comment to first response", commentData.getFeedbackCommentText());

        List<FeedbackResponseCommentAttributes> frcList =
                getInstructorComments(response1ForQ1.getId(), "Comment to first response");
        assertEquals(1, frcList.size());
        FeedbackResponseCommentAttributes frc = frcList.get(0);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
        assertFalse(frc.isVisibilityFollowingFeedbackQuestion);
    }

    @Test
    public void testExecute_unpublishedSessionEmptyGiverPermission_shouldPass() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("typical successful case for unpublished session empty giver permissions");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Empty giver permissions", new ArrayList<>(), new ArrayList<>());
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);
    }

    @Test
    public void testExecute_unpublishedSessionValidVisibilitySettings_shouldPass() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("typical successful case for unpublished session shown to various recipients");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Null comment permissions", new ArrayList<>(), new ArrayList<>());
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        requestBody = new FeedbackResponseCommentCreateRequest("Comment shown to giver",
                Arrays.asList(CommentVisibilityType.GIVER), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        requestBody = new FeedbackResponseCommentCreateRequest("Comment shown to receiver",
                Arrays.asList(CommentVisibilityType.RECIPIENT), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        requestBody =
                new FeedbackResponseCommentCreateRequest("Comment shown to own team members",
                        Arrays.asList(CommentVisibilityType.GIVER_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        requestBody = new FeedbackResponseCommentCreateRequest(
                "Comment shown to receiver team members",
                Arrays.asList(CommentVisibilityType.GIVER_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        requestBody = new FeedbackResponseCommentCreateRequest("Comment shown to students",
                Arrays.asList(CommentVisibilityType.STUDENTS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

    }

    @Test
    public void testExecute_publishedSessionForInstructorResult_shouldPass() throws Exception {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        logic.publishFeedbackSession(session1InCourse1.getFeedbackSessionName(),
                session1InCourse1.getCourseId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Comment to first response, published session",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS));
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        List<FeedbackResponseCommentAttributes> frcList = getInstructorComments(response1ForQ1.getId(),
                "Comment to first response, published session");
        assertEquals(1, frcList.size());
        FeedbackResponseCommentAttributes frc = frcList.get(0);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
        assertFalse(frc.isVisibilityFollowingFeedbackQuestion);
    }

    @Test
    public void testExecute_emptyCommentText_shouldFail() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Unsuccessful case: empty comment text");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest("",
                new ArrayList<>(), new ArrayList<>());
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        assertEquals(BasicCommentSubmissionAction.FEEDBACK_RESPONSE_COMMENT_EMPTY, output.getMessage());
    }

    @Test
    protected void testExecute_typicalCaseForSubmission_shouldPass() {
        // clean any existing comments.
        logic.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder().withResponseId(response1ForQ3.getId()).build());
        assertNull(logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQ3.getId()));
        logic.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder().withResponseId(response1ForQ1.getId()).build());
        assertNull(logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQ1.getId()));

        ______TS("Successful case: student submission");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Student submission comment", Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS));
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes comment =
                logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQ3.getId());
        assertEquals(comment.getCommentText(), "Student submission comment");
        assertEquals(student1InCourse1.getEmail(), comment.getCommentGiver());
        assertTrue(comment.isCommentFromFeedbackParticipant);
        assertTrue(comment.isVisibilityFollowingFeedbackQuestion);
        assertEquals(FeedbackParticipantType.STUDENTS, comment.getCommentGiverType());

        ______TS("Successful case: instructor submission");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        requestBody = new FeedbackResponseCommentCreateRequest(
                "Instructor submission comment", Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        comment = logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQ1.getId());
        assertEquals(comment.getCommentText(), "Instructor submission comment");
        assertEquals(instructor1OfCourse1.getEmail(), comment.getCommentGiver());
        assertTrue(comment.isCommentFromFeedbackParticipant);
        assertTrue(comment.isVisibilityFollowingFeedbackQuestion);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, comment.getCommentGiverType());
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {

        ______TS("invalid intent STUDENT_RESULT");
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };
        verifyHttpParameterFailure(invalidIntent1);

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };
        verifyHttpParameterFailure(invalidIntent2);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        // see individual test cases
    }

    @Test
    protected void testAccessControl_textTypeQuestionResponse_notAllowedToAddComment() {
        String[] submissionParamsInstructor = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ2.getId()),
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        assertThrows(InvalidHttpParameterException.class,
                () -> getAction(submissionParamsInstructor).checkSpecificAccessControl());
    }

    @Test
    public void testAccessControl_contributionQuestionResponse_instructorNotAllowedToAddComment() {
        DataBundle contributionDataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDataBundle(contributionDataBundle);
        InstructorAttributes instructorAttributes = contributionDataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes contributionSession = contributionDataBundle.feedbackSessions.get("contribSession");
        FeedbackQuestionAttributes contributionQuestion = logic.getFeedbackQuestion(
                contributionSession.getFeedbackSessionName(), contributionSession.getCourseId(), 1);
        FeedbackResponseAttributes contributionResponse =
                contributionDataBundle.feedbackResponses.get("response1ForQ1S5C1");
        contributionResponse = logic.getFeedbackResponse(
                contributionQuestion.getId(), contributionResponse.getGiver(), contributionResponse.getRecipient());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(contributionResponse.getId()),
        };

        loginAsInstructor(instructorAttributes.getGoogleId());
        InvalidHttpParameterException exception = assertThrows(
                InvalidHttpParameterException.class, () -> getAction(submissionParams).checkSpecificAccessControl());
        AssertHelper.assertContains("Invalid question type for instructor comment", exception.getMessage());
    }

    @Test
    protected void testAccessControl_commentAlreadyExist_shouldNotCreateAgain() {
        ______TS("students give a comment already exists");

        assertNotNull(logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQ3.getId()));

        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParamsStudent = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };

        assertThrows(InvalidHttpParameterException.class,
                () -> getAction(submissionParamsStudent).checkSpecificAccessControl());

        ______TS("instructors give a comment already exists");

        assertNotNull(logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQ1.getId()));

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParamsInstructor = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        assertThrows(InvalidHttpParameterException.class,
                () -> getAction(submissionParamsInstructor).checkSpecificAccessControl());
    }

    @Test
    protected void testAccessControl_submitCommentForOthersResponse_shouldFail() {

        ______TS("students access other students session and give comments");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParamsStudentToStudents = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response2ForQ3.getId()),
        };
        verifyCannotAccess(submissionParamsStudentToStudents);

        ______TS("students access own response to give comments");

        loginAsStudent(student2InCourse1.getGoogleId());
        verifyCanAccess(submissionParamsStudentToStudents);

        ______TS("student teams access other students session and give comments");

        loginAsStudent(student3InCourse1.getGoogleId());
        String[] submissionParamsTeam = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response2ForQ4.getId()),
        };
        verifyCannotAccess(submissionParamsTeam);

        ______TS("student teams access own response to give comments");

        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCanAccess(submissionParamsTeam);

        ______TS("instructors access other instructor's session and give comments");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        String[] submissionParamsInstructorToInstructor = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ5.getId()),
        };
        verifyCannotAccess(submissionParamsInstructorToInstructor);

        ______TS("instructors access own response to give comments");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyCanAccess(submissionParamsInstructorToInstructor);
    }

    @Test
    protected void testAccessControl_invalidIntent_shouldFail() {

        ______TS("invalid intent STUDENT_RESULT");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent1).checkAccessControl());

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent2).checkAccessControl());
    }

    @Test
    protected void testAccessControl_instructorWithoutSubmitSessionInSectionsPrivilege_shouldFail() {

        loginAsInstructor(helperOfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_logOut_shouldFail() {

        gaeSimulation.logoutUser();
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_studentAccessInstructorResponse_shouldFail() {

        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_accessibleForInstructorInSameCourse_shouldPass() {

        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };
        verifyCanAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_accessibleForAdminToMasqueradeAsInstructor_shouldPass() {

        loginAsAdmin();
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };
        verifyCanMasquerade(instructor1OfCourse1.getGoogleId(), submissionParams);
    }

    @Test
    protected void testAccessControl_onlyInstructorsWithCorrectPrivilege_shouldPass() throws Exception {

        String[] submissionParams = getSubmissionParamsForCrossSectionResponse();

        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
        InstructorAttributes instructor = helperOfCourse1;

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS,
                new String[] {"Section A", "Section B"});

        loginAsInstructor(instructor.googleId);
        verifyCanAccess(submissionParams);

        verifyCanMasquerade(instructor.googleId, submissionParams);
    }

    @Test
    protected void testAccessControl_onlyInstructorsWithOnlyEitherPrivilege_shouldFail() throws Exception {
        String[] submissionParams = getSubmissionParamsForCrossSectionResponse();

        InstructorAttributes instructor = helperOfCourse1;
        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS,
                new String[] {"Section A"});

        loginAsInstructor(instructor.googleId);
        verifyCannotAccess(submissionParams);

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS,
                new String[] {"Section B"});

        verifyCannotAccess(submissionParams);
    }

    private String[] getSubmissionParamsForCrossSectionResponse() {
        return new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ6.getId()),
        };

    }

    /**
     * Filters instructor comments according to comment text from all comments on a response.
     *
     * @param responseId response id of response
     * @param commentText comment text
     * @return instructor comments
     */
    private List<FeedbackResponseCommentAttributes> getInstructorComments(String responseId, String commentText) {
        return logic.getFeedbackResponseCommentForResponse(responseId)
                .stream()
                .filter(comment -> comment.commentText.equals(commentText))
                .collect(Collectors.toList());
    }

}
