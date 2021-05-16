package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionPublishStatus;
import teammates.ui.output.FeedbackSessionSubmissionStatus;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetFeedbackSessionAction}.
 */
public class GetFeedbackSessionActionTest extends BaseActionTest<GetFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        // TODO: Add test cases

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_SESSION_NAME,
                feedbackSessionAttributes.getFeedbackSessionName());
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName());

        ______TS("typical success case");

        String[] params = {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();
        assertEquals(feedbackSessionAttributes.getCourseId(), response.getCourseId());
        assertEquals(feedbackSessionAttributes.getFeedbackSessionName(), response.getFeedbackSessionName());
        assertEquals(feedbackSessionAttributes.getTimeZone().getId(), response.getTimeZone());
        assertEquals(feedbackSessionAttributes.getInstructions(), response.getInstructions());

        assertEquals(feedbackSessionAttributes.getStartTime().toEpochMilli(), response.getSubmissionStartTimestamp());
        assertEquals(feedbackSessionAttributes.getEndTime().toEpochMilli(), response.getSubmissionEndTimestamp());
        assertEquals(feedbackSessionAttributes.getGracePeriodMinutes(), response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(feedbackSessionAttributes.getSessionVisibleFromTime().toEpochMilli(),
                response.getCustomSessionVisibleTimestamp().longValue());

        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(feedbackSessionAttributes.getResultsVisibleFromTime().toEpochMilli(),
                response.getCustomResponseVisibleTimestamp().longValue());

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());
        assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, response.getPublishStatus());

        assertEquals(feedbackSessionAttributes.isClosingEmailEnabled(), response.getIsClosingEmailEnabled());
        assertEquals(feedbackSessionAttributes.isPublishedEmailEnabled(), response.getIsPublishedEmailEnabled());

        assertEquals(feedbackSessionAttributes.getCreatedTime().toEpochMilli(), response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("non-existent feedback session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName for Session123",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        loginAsInstructor(instructor1OfCourse1.googleId);
        verifyEntityNotFound(submissionParams);

        ______TS("only instructors of the same course can access full detail");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);

        ______TS("only students of the same course can access student result");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };

        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
        verifyInaccessibleForStudentsOfOtherCourse(submissionParams);

        ______TS("only instructors of the same course can access instructor result");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
    }

    @Test
    protected void testAccessControl_studentResult() throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Intent intent = Intent.STUDENT_SUBMISSION;
        String[] params = generateParameters(feedbackSession, intent, "", "", "");

        ______TS("Typical unauthorized cases");

        verifyInaccessibleWithoutLogin(params);
        String unregUserId = "unreg.user";
        loginAsUnregistered(unregUserId);
        verifyCannotAccess(params);

        ______TS("student can access his own course session");

        verifyAccessibleForStudentsOfTheSameCourse(params);

        ______TS("Instructor cannot directly get student session");

        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").googleId);
        verifyCannotAccess(params);

        ______TS("student cannot access other course session");
        FeedbackSessionAttributes otherCourseFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse2");
        params = generateParameters(otherCourseFeedbackSession, intent, "", "", "");
        verifyCannotAccess(params);

        ______TS("Instructor with correct privilege moderate student session");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        params = generateParameters(feedbackSession, intent, "", student1InCourse1.email, "");

        verifyInaccessibleForInstructorsOfOtherCourses(params);
        verifyInaccessibleForStudents(params);

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");
        loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(params);

        grantInstructorWithSectionPrivilege(helperOfCourse1,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section 1"});
        verifyCanAccess(params);
        verifyAccessibleForAdminToMasqueradeAsInstructor(helperOfCourse1, params);

        ______TS("Instructor preview student session");
        params = generateParameters(feedbackSession, intent, "", "", student1InCourse1.email);

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, params);
    }

    @Test
    protected void testAccessControl_fullDetail() {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] params = generateParameters(feedbackSession, Intent.FULL_DETAIL, "", "", "");
        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
    }

    @Test
    protected void testAccessControl_instructorResult() throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Intent intent = Intent.INSTRUCTOR_RESULT;
        String[] params = generateParameters(feedbackSession, intent, "", "", "");
        ______TS("Only instructor with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, params
        );

        ______TS("Instructor moderates instructor submission with correct privilege will pass");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        params = generateParameters(feedbackSession, Intent.INSTRUCTOR_SUBMISSION, "", instructor1OfCourse1.email, "");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, params);

        ______TS("Instructor preview instructor result with correct privilege will pass");

        String[] previewInstructorSubmissionParams =
                generateParameters(feedbackSession, Intent.INSTRUCTOR_SUBMISSION,
                        "", "", instructor1OfCourse1.email);
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, previewInstructorSubmissionParams);
    }

    private String[] generateParameters(FeedbackSessionAttributes session, Intent intent,
                                        String regKey, String moderatedPerson, String previewPerson) {
        return new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, intent.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedPerson,
                Const.ParamsNames.PREVIEWAS, previewPerson,
                Const.ParamsNames.REGKEY, regKey,
        };
    }
}
