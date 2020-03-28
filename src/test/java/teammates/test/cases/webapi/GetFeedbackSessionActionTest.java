package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.action.GetFeedbackSessionAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackSessionData;
import teammates.ui.webapi.output.FeedbackSessionPublishStatus;
import teammates.ui.webapi.output.FeedbackSessionSubmissionStatus;
import teammates.ui.webapi.output.ResponseVisibleSetting;
import teammates.ui.webapi.output.SessionVisibleSetting;
import teammates.ui.webapi.request.Intent;

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
        //See independent teste cases
    }

    @Test
    protected void testAccessControl_studentResult() {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Intent intent = Intent.STUDENT_SUBMISSION;
        String[] params = generateParameters(feedbackSession, intent, "", "", "");

        ______TS("Typical unauthorized cases");

        verifyInaccessibleWithoutLogin(params);
        String unregUserId = "unreg.user";
        loginAsUnregistered(unregUserId);
        verifyEntityNotFound(params);

        ______TS("student can access his own course session");

        verifyAccessibleForStudentsOfTheSameCourse(params);

        ______TS("Instructor cannot directly get student session");

        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").googleId);
        verifyEntityNotFound(params);

        ______TS("student cannot access other course session");
        FeedbackSessionAttributes otherCourseFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse2");
        params = generateParameters(otherCourseFeedbackSession, intent, "", "", "");
        verifyEntityNotFound(params);

        ______TS("Instructor with correct privilege moderate student session");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        params = generateParameters(feedbackSession, intent, "", student1InCourse1.email, "");

        verifyInaccessibleForInstructorsOfOtherCourses(params);
        verifyInaccessibleForStudents(params);

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");
        loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(params);

        updateInstructorWithOnlySectionPrivilege(helperOfCourse1,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section 1"}, params);
        verifyCanAccess(params);
        verifyAccessibleForAdminToMasqueradeAsInstructor(helperOfCourse1, params);

        ______TS("Instructor preview student session");
        params = generateParameters(feedbackSession, intent, "", "", student1InCourse1.email);

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, params);

        ______TS("Unregister student can access with correct key");
        StudentAttributes unregisteredStudent = logic.getStudentForGoogleId(
                student1InCourse1.getCourse(), student1InCourse1.googleId);
        params = generateParameters(feedbackSession, intent, StringHelper.encrypt(unregisteredStudent.getKey()), "", "");
        verifyAccessibleWithoutLogin(params);
    }

    @Test
    protected void testAccessControl_fullDetail() {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] params = generateParameters(feedbackSession, Intent.FULL_DETAIL, "", "", "");
        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
    }

    @Test
    protected void testAccessControl_instructorResult() {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Intent intent = Intent.INSTRUCTOR_RESULT;
        String[] params = generateParameters(feedbackSession, intent, "", "", "");
        ______TS("Only instructor with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, params
        );

        ______TS("Instructor moderates instructor submission with correct privilege will pass");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        params = generateParameters(feedbackSession, intent, "", instructor1OfCourse1.email, "");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, params);

        ______TS("Instructor preview instructor result with correct privilege will pass");

        String[] previewInstructorSubmissionParams =
                generateParameters(feedbackSession, intent,
                        "", "", instructor1OfCourse1.email);
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, previewInstructorSubmissionParams);
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
