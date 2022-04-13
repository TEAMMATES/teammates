package teammates.ui.webapi;

import java.time.Instant;
import java.util.Map;

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
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
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
                feedbackResponseComment.getCommentGiver(), feedbackResponseComment.getCreatedAt());
        assertNotNull("response comment not found", feedbackResponseComment);

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

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
    protected void testAccessControl() {
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
        response = logic.getFeedbackResponse(question.getId(), response.getGiver(), response.getRecipient());
        comment = logic.getFeedbackResponseComment(response.getId(), comment.getCommentGiver(), comment.getCreatedAt());
        comment.setFeedbackResponseId(response.getId());

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

        logic.updateInstructor(InstructorAttributes.updateOptionsWithEmailBuilder(course.getId(), instructor1.getEmail())
                .withPrivileges(instructorPrivileges).build());

        loginAsInstructor(instructor1.getGoogleId());
        verifyCanAccess(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(instructor1, submissionParams);

        ______TS("Instructor with correct privilege should pass");

        InstructorAttributes instructor2 = typicalBundle.instructors.get("instructor2OfCourse1");

        grantInstructorWithSectionPrivilege(instructor2,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A", "Section B"});

        loginAsInstructor(instructor2.getGoogleId());
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
        response = logic.getFeedbackResponse(question.getId(), response.getGiver(), response.getRecipient());
        comment = logic.getFeedbackResponseComment(response.getId(), comment.getCommentGiver(), comment.getCreatedAt());

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
        response = logic.getFeedbackResponse(question.getId(), response.getGiver(), response.getRecipient());
        comment = logic.getFeedbackResponseComment(response.getId(), comment.getCommentGiver(), comment.getCreatedAt());

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
    public void testCrossSectionAccessControl() throws Exception {
        int questionNumber = 6;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment2FromStudent1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ6");

        FeedbackQuestionAttributes question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.getGiver(), response.getRecipient());
        comment = logic.getFeedbackResponseComment(response.getId(), comment.getCommentGiver(), comment.getCreatedAt());

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

        loginAsInstructor(instructor.getGoogleId());
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
    public void testAccessControlsForCommentByTeam() throws Exception {
        int questionNumber = 4;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromTeam1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ4");

        FeedbackQuestionAttributes question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        assertEquals(FeedbackParticipantType.TEAMS, question.getGiverType());
        response = logic.getFeedbackResponse(question.getId(), response.getGiver(), response.getRecipient());
        comment = logic.getFeedbackResponseComment(response.getId(), comment.getCommentGiver(), comment.getCreatedAt());

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

        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(instructorParams);
        verifyCanMasquerade(instructor.getGoogleId(), instructorParams);

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
    public void testAccessControl_instructorSubmissionPastEndTime_shouldAllowIfBeforeDeadline() throws Exception {
        int questionNumber = 1;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackResponseAttributes response1ForQ1 = typicalBundle.feedbackResponses.get("response1ForQ1");
        FeedbackResponseCommentAttributes comment1FromInstructor1 = typicalBundle.feedbackResponseComments
                .get("comment1FromInstructor1");
        FeedbackQuestionAttributes qn1InSession1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);
        response1ForQ1 = logic.getFeedbackResponse(qn1InSession1.getId(), response1ForQ1.getGiver(),
                response1ForQ1.getRecipient());
        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQ1.getId(),
                comment1FromInstructor1.getCommentGiver(), comment1FromInstructor1.getCreatedAt());

        Instant newEndTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withEndTime(newEndTime)
                .build());
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
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
        int questionNumber = 3;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        FeedbackResponseAttributes response1ForQ3 = typicalBundle.feedbackResponses.get("response1ForQ3");
        FeedbackResponseCommentAttributes comment1FromStudent1 = typicalBundle.feedbackResponseComments
                .get("comment1FromStudent1");
        FeedbackQuestionAttributes qn3InSession1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);
        response1ForQ3 = logic.getFeedbackResponse(qn3InSession1.getId(), response1ForQ3.getGiver(),
                response1ForQ3.getRecipient());
        comment1FromStudent1 = logic.getFeedbackResponseComment(response1ForQ3.getId(),
                comment1FromStudent1.getCommentGiver(), comment1FromStudent1.getCreatedAt());

        Instant newEndTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withEndTime(newEndTime)
                .build());
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
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
}
