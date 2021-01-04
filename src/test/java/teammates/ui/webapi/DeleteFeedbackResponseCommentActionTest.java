package teammates.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.Intent;

/**
 * SUT: {@link DeleteFeedbackResponseCommentAction}.
 */
public class DeleteFeedbackResponseCommentActionTest extends BaseActionTest<DeleteFeedbackResponseCommentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    protected void refreshTestData() {
        typicalBundle = loadDataBundle("/FeedbackResponseCommentCRUDTest.json");
        removeAndRestoreDataBundle(typicalBundle);
    }

    @Override
    @Test
    public void testExecute() {
        FeedbackResponseCommentAttributes feedbackResponseComment =
                typicalBundle.feedbackResponseComments.get("comment1FromInstructor1");

        feedbackResponseComment = logic.getFeedbackResponseComment(feedbackResponseComment.getFeedbackResponseId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.googleId);

        ______TS("Unsuccessful case: not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical successful case, comment deleted");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        DeleteFeedbackResponseCommentAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertNull(logic.getFeedbackResponseComment(feedbackResponseComment.getId()));
        assertEquals("Successfully deleted feedback response comment.", output.getMessage());

        ______TS("Non-existent feedback response comment, non-existent comment should fail silently");

        submissionParams = new String[] {
                // non-existent feedback response comment id
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        output = (MessageOutput) result.getOutput();

        assertNull(logic.getFeedbackResponseComment(123123123123123L));
        assertEquals("Successfully deleted feedback response comment.", output.getMessage());
    }

    @Override
    protected void testAccessControl() throws Exception {
        // See each independent test case
    }

    @Test
    protected void testAccessControlsForCommentByInstructor() throws Exception {
        int questionNumber = 1;
        CourseAttributes course = typicalBundle.courses.get("idOfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromInstructor1Q2");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ1");

        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);
        comment.feedbackResponseId = response.getId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId()),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        verifyInaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);

        ______TS("Comment giver without privilege should pass");

        InstructorAttributes instructor1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();

        logic.updateInstructor(InstructorAttributes.updateOptionsWithEmailBuilder(course.getId(), instructor1.email)
                .withPrivileges(instructorPrivileges).build());

        loginAsInstructor(instructor1.googleId);
        verifyCanAccess(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(instructor1, submissionParams);

        ______TS("Instructor with correct privilege should pass");

        InstructorAttributes instructor2 = typicalBundle.instructors.get("instructor2OfCourse1");

        grantInstructorWithSectionPrivilege(instructor2,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A", "Section B"});

        loginAsInstructor(instructor2.googleId);
        verifyCanAccess(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(instructor2, submissionParams);

        ______TS("Instructor with only section 1 privilege should fail");

        grantInstructorWithSectionPrivilege(instructor2,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A"});
        verifyCannotAccess(submissionParams);

        ______TS("Instructor with only section 2 privilege should fail");

        grantInstructorWithSectionPrivilege(instructor2,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section B"});
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControlsForCommentByInstructorAsFeedbackParticipant() {
        int questionNumber = 1;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromInstructor1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ1");

        FeedbackQuestionAttributes question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        ______TS("Instructor who give the comment can delete comment");

        InstructorAttributes instructorWhoGiveComment = typicalBundle.instructors.get("instructor1OfCourse1");
        assertEquals(instructorWhoGiveComment.getEmail(), comment.getCommentGiver());
        loginAsInstructor(instructorWhoGiveComment.getGoogleId());
        verifyCanAccess(submissionParams);

        ______TS("Different instructor of same course cannot delete comment");

        InstructorAttributes differentInstructorInSameCourse = typicalBundle.instructors.get("instructor2OfCourse1");
        assertNotEquals(differentInstructorInSameCourse.getEmail(), comment.getCommentGiver());
        loginAsInstructor(differentInstructorInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControlsForCommentByStudent() {
        int questionNumber = 3;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromStudent1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ3");

        FeedbackQuestionAttributes question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Student who give the comment can delete comment");

        StudentAttributes studentWhoGiveComment = typicalBundle.students.get("student1InCourse1");
        assertEquals(studentWhoGiveComment.getEmail(), comment.getCommentGiver());
        loginAsStudent(studentWhoGiveComment.getGoogleId());
        verifyCanAccess(submissionParams);

        ______TS("Different student of same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = typicalBundle.students.get("student2InCourse1");
        assertNotEquals(differentStudentInSameCourse.getEmail(), comment.getCommentGiver());
        loginAsStudent(differentStudentInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("Typical cases: unauthorized users");

        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
    }

    @Test
    public void testCrossSectionAccessControl() throws InvalidParametersException, EntityDoesNotExistException {
        int questionNumber = 6;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment2FromStudent1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ6");

        FeedbackQuestionAttributes question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Instructor with correct privilege can delete comment");

        InstructorAttributes instructor = typicalBundle.instructors.get("helperOfCourse1");

        String[] instructorParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A", "Section B"});

        loginAsInstructor(instructor.googleId);
        verifyCanAccess(instructorParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(instructor, instructorParams);

        ______TS("Instructor with only section A privilege cannot delete comment");

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A"});

        verifyCannotAccess(submissionParams);

        ______TS("Instructor with only section B privilege cannot delete comment");

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section B"});

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControlsForCommentByTeam() throws InvalidParametersException, EntityDoesNotExistException {
        int questionNumber = 4;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromTeam1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ4");

        FeedbackQuestionAttributes question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        assertEquals(FeedbackParticipantType.TEAMS, question.getGiverType());
        response = logic.getFeedbackResponse(question.getId(), response.giver, response.recipient);
        comment = logic.getFeedbackResponseComment(response.getId(), comment.commentGiver, comment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Different student of different team and same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = typicalBundle.students.get("student3InCourse1");
        assertNotEquals(differentStudentInSameCourse.getTeam(), response.getGiver());
        loginAsStudent(differentStudentInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("Different student of same team can delete comment");

        StudentAttributes differentStudentInSameTeam = typicalBundle.students.get("student2InCourse1");
        assertEquals(differentStudentInSameTeam.getTeam(), response.getGiver());
        loginAsStudent(differentStudentInSameTeam.getGoogleId());
        verifyCanAccess(submissionParams);

        ______TS("Typical cases: unauthorized users");

        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);

        ______TS("Instructor with correct privilege can delete comment");

        String[] instructorParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        InstructorAttributes instructor = typicalBundle.instructors.get("helperOfCourse1");
        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A", "Section B"});

        loginAsInstructor(instructor.googleId);
        verifyCanAccess(instructorParams);
        verifyCanMasquerade(instructor.googleId, instructorParams);

        ______TS("Instructor with only section A privilege cannot delete comment");

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A"});

        verifyCannotAccess(submissionParams);

        ______TS("Instructor with only section B privilege cannot delete comment");

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section B"});

        verifyCannotAccess(submissionParams);
    }
}
