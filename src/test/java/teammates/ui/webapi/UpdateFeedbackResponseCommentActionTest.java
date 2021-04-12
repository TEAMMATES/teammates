package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.Intent;

/**
 * SUT: {@link UpdateFeedbackResponseCommentAction}.
 */
public class UpdateFeedbackResponseCommentActionTest extends BaseActionTest<UpdateFeedbackResponseCommentAction> {

    private CourseAttributes course;
    private InstructorAttributes instructor1OfCourse1;
    private InstructorAttributes instructor2OfCourse1;
    private InstructorAttributes helperOfCourse1;
    private StudentAttributes student1InCourse1;
    private StudentAttributes student2InCourse1;
    private StudentAttributes student3InCourse1;
    private FeedbackResponseAttributes response1ForQn1;
    private FeedbackResponseCommentAttributes comment1FromInstructor1;
    private FeedbackResponseCommentAttributes comment1FromStudent1;
    private FeedbackResponseCommentAttributes comment2FromStudent1;
    private FeedbackResponseCommentAttributes comment1FromTeam1;
    private FeedbackResponseCommentAttributes comment1FromInstructor1Q2;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = loadDataBundle("/FeedbackResponseCommentCRUDTest.json");
        removeAndRestoreDataBundle(dataBundle);

        course = dataBundle.courses.get("idOfCourse1");
        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        student2InCourse1 = dataBundle.students.get("student2InCourse1");
        student3InCourse1 = dataBundle.students.get("student3InCourse1");
        instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        instructor2OfCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        helperOfCourse1 = dataBundle.instructors.get("helperOfCourse1");
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        FeedbackQuestionAttributes qn3InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 3);
        FeedbackQuestionAttributes qn4InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 4);
        FeedbackQuestionAttributes qn6InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 6);
        response1ForQn1 = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());
        FeedbackResponseAttributes response1ForQn3 = logic.getFeedbackResponse(qn3InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
        FeedbackResponseAttributes response1ForQ4 = logic.getFeedbackResponse(qn4InSession1InCourse1.getId(),
                student1InCourse1.getTeam(), student3InCourse1.getTeam());
        FeedbackResponseAttributes response1ForQn6 = logic.getFeedbackResponse(qn6InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student3InCourse1.getEmail());
        comment1FromInstructor1 = logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQn1.getId());
        comment1FromStudent1 = logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQn3.getId());
        comment2FromStudent1 = logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQn6.getId());
        comment1FromTeam1 = logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQ4.getId());

        comment1FromInstructor1Q2 = dataBundle.feedbackResponseComments.get("comment1FromInstructor1Q2");
        comment1FromInstructor1Q2 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1Q2.getCommentGiver(), comment1FromInstructor1Q2.getCreatedAt());
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        // see individual test cases.
    }

    @Test
    protected void testExecute_notEnoughParams_shouldFail() {

        ______TS("Unsuccessful case: not enough parameters");

        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_typicalSuccessfulCases_shouldPass() {
        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1.commentGiver, comment1FromInstructor1.createdAt);
        assertNotNull("response comment not found", comment1FromInstructor1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for INSTRUCTOR_RESULT");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.commentText + " (Edited)",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS));
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc =
                logic.getFeedbackResponseComment(comment1FromInstructor1.getId());
        assertEquals(comment1FromInstructor1.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);

        ______TS("Typical successful case for STUDENT_SUBMISSION");
        loginAsStudent(student1InCourse1.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
        };
        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromStudent1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS), Arrays.asList(CommentVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(comment1FromStudent1.getId());
        assertEquals(comment1FromStudent1.getCommentText() + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.STUDENTS, frc.commentGiverType);
        assertEquals(student1InCourse1.getEmail(), frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);

        ______TS("Typical successful case for INSTRUCTOR_SUBMISSION");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };
        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS), Arrays.asList(CommentVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(comment1FromInstructor1.getId());
        assertEquals(comment1FromInstructor1.getCommentText() + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);
    }

    @Test
    protected void testExecute_emptyVisibilitySettings_shouldPass() {
        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1.commentGiver, comment1FromInstructor1.createdAt);
        assertNotNull("response comment not found", comment1FromInstructor1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Null show comments and show giver permissions");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.commentText + " (Edited)", new ArrayList<>(), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);
    }

    @Test
    protected void testExecute_variousVisibilitySettings_shouldPass() {
        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1.commentGiver, comment1FromInstructor1.createdAt);
        assertNotNull("response comment not found", comment1FromInstructor1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for unpublished session public to various recipients");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.commentText + " (Edited)", new ArrayList<>(), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.commentText + " (Edited)",
                Arrays.asList(CommentVisibilityType.GIVER), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.commentText + " (Edited)",
                Arrays.asList(CommentVisibilityType.RECIPIENT), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.commentText + " (Edited)",
                Arrays.asList(CommentVisibilityType.GIVER_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.commentText + " (Edited)",
                Arrays.asList(CommentVisibilityType.RECIPIENT_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.commentText + " (Edited)",
                Arrays.asList(CommentVisibilityType.STUDENTS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);
    }

    @Test
    protected void testExecute_nonExistingFeedbackResponse_shouldFail() {

        ______TS("Non-existent feedback response comment id");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.commentText + " (Edited)",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS));
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        UpdateFeedbackResponseCommentAction action0 = action;
        assertThrows(EntityNotFoundException.class, () -> getJsonResult(action0));
    }

    @Test
    protected void testExecute_instructorIsNotCommentGiver_shouldPass() {
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        ______TS("Instructor is not feedback response comment giver");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1Q2.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1Q2.commentText + " (Edited)",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS));
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(comment1FromInstructor1Q2.getId());
        assertEquals(comment1FromInstructor1Q2.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertEquals(instructor2OfCourse1.getEmail(), frc.lastEditorEmail);
        assertFalse(frc.isCommentFromFeedbackParticipant);
        assertEquals(
                Arrays.asList(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS), frc.getShowCommentTo());
        assertEquals(
                Arrays.asList(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS), frc.getShowGiverNameTo());
    }

    @Test
    protected void testExecute_typicalCasePublishedSession_shouldPass() throws Exception {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for published session");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        logic.publishFeedbackSession(
                comment1FromInstructor1Q2.feedbackSessionName, comment1FromInstructor1Q2.courseId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1Q2.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1Q2.commentText + " (Edited for published session)",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(comment1FromInstructor1Q2.getId());
        assertEquals(comment1FromInstructor1Q2.commentText + " (Edited for published session)",
                frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
    }

    @Test
    protected void testExecute_emptyCommentText_shouldFail() {
        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1.commentGiver, comment1FromInstructor1.createdAt);
        assertNotNull("response comment not found", comment1FromInstructor1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Unsuccessful case: empty comment text");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                "", new ArrayList<>(), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        assertEquals(BasicCommentSubmissionAction.FEEDBACK_RESPONSE_COMMENT_EMPTY, output.getMessage());
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {
        ______TS("invalid intent STUDENT_RESULT");
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
        };
        verifyHttpParameterFailure(invalidIntent1);

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment1FromInstructor1.getId().toString(),
        };
        verifyHttpParameterFailure(invalidIntent2);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        // see individual test cases
    }

    @Test
    protected void testAccessControl_accessibleWithPrivilege_shouldPass() {

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1Q2.getId().toString(),
        };
        ______TS("accessible for instructors of the same course");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        verifyCanAccess(submissionParams);
        loginAsAdmin();
        verifyCanMasquerade(instructor2OfCourse1.getGoogleId(), submissionParams);

        ______TS("inaccessible for helper instructors");
        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_typicalSuccessfulCase_shouldPass() {
        ______TS("successful case for instructor result");

        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1.commentGiver, comment1FromInstructor1.createdAt);
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyCanAccess(submissionParams);

        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        verifyCanAccess(submissionParams);

    }

    @Test
    protected void testAccessControl_invalidIntent_shouldFail() {

        ______TS("invalid intent STUDENT_RESULT");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent1).checkAccessControl());

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent2).checkAccessControl());
    }

    @Test
    protected void testAccessControl_updateCommentForOthersResponse_shouldFail() {

        ______TS("students access other students session and give comments");
        loginAsStudent(student2InCourse1.getGoogleId());
        String[] submissionParamsStudentToStudents = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
        };
        verifyCannotAccess(submissionParamsStudentToStudents);

        ______TS("students own comments");
        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCanAccess(submissionParamsStudentToStudents);

        ______TS("students access other students team comments");
        loginAsStudent(student3InCourse1.getGoogleId());
        String[] submissionParamsTeam = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromTeam1.getId().toString(),
        };
        verifyCannotAccess(submissionParamsTeam);

        ______TS("students own team comments");
        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCanAccess(submissionParamsTeam);

        ______TS("instructors access other instructor's session and give comments");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        String[] submissionParamsInstructorToInstructor = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };
        verifyCannotAccess(submissionParamsInstructorToInstructor);

        ______TS("instructors own comments");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyCanAccess(submissionParamsInstructorToInstructor);

    }

    @Test
    protected void testAccessControl_nonExistingResponseComment_shouldFail() {

        ______TS("Response comment doesn't exist");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };
        assertThrows(EntityNotFoundException.class, () -> getAction(submissionParams).checkSpecificAccessControl());
    }

    @Test
    protected void testAccessControl_instructorsWithCorrectPrivilege_shouldPass() throws Exception {
        String[] submissionParams = getSubmissionParamsForCrossSectionResponseComment();

        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);

        InstructorAttributes instructor = helperOfCourse1;
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorPrivileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);

        logic.updateInstructor(InstructorAttributes.updateOptionsWithEmailBuilder(course.getId(), instructor.email)
                .withPrivileges(instructorPrivileges).build());

        loginAsInstructor(instructor.googleId);
        verifyCanAccess(submissionParams);
        verifyCanMasquerade(instructor.googleId, submissionParams);
    }

    @Test
    protected void testAccessControl_instructorWithOnlyEitherSectionPrivilege_shouldFail() throws Exception {
        String[] submissionParams = getSubmissionParamsForCrossSectionResponseComment();

        InstructorAttributes instructor = helperOfCourse1;
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);

        logic.updateInstructor(InstructorAttributes.updateOptionsWithEmailBuilder(course.getId(), instructor.email)
                .withPrivileges(instructorPrivileges).build());

        loginAsInstructor(instructor.googleId);
        verifyCannotAccess(submissionParams);

        instructorPrivileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, false);
        instructorPrivileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        logic.updateInstructor(InstructorAttributes.updateOptionsWithEmailBuilder(course.getId(), instructor.email)
                .withPrivileges(instructorPrivileges).build());

        verifyCannotAccess(submissionParams);
    }

    private String[] getSubmissionParamsForCrossSectionResponseComment() {
        return new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment2FromStudent1.getId().toString(),
        };
    }
}
