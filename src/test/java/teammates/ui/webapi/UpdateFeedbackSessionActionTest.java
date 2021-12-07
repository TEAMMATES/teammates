package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * SUT: {@link UpdateFeedbackSessionAction}.
 */
public class UpdateFeedbackSessionActionTest extends BaseActionTest<UpdateFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, session.getCourseId());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName());

        ______TS("success: Typical case");

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();

        UpdateFeedbackSessionAction a = getAction(updateRequest, param);
        JsonResult r = getJsonResult(a);

        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        session = logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
        assertEquals(session.getCourseId(), response.getCourseId());
        assertEquals(session.getTimeZone(), response.getTimeZone());
        assertEquals(session.getFeedbackSessionName(), response.getFeedbackSessionName());

        assertEquals(session.getInstructions(), response.getInstructions());

        assertEquals(session.getStartTime().toEpochMilli(), response.getSubmissionStartTimestamp());
        assertEquals(session.getEndTime().toEpochMilli(), response.getSubmissionEndTimestamp());
        assertEquals(session.getGracePeriodMinutes(), response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(session.getSessionVisibleFromTime().toEpochMilli(),
                response.getCustomSessionVisibleTimestamp().longValue());
        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(session.getResultsVisibleFromTime().toEpochMilli(),
                response.getCustomResponseVisibleTimestamp().longValue());

        assertEquals(session.isClosingEmailEnabled(), response.getIsClosingEmailEnabled());
        assertEquals(session.isPublishedEmailEnabled(), response.getIsPublishedEmailEnabled());

        assertEquals(session.getCreatedTime().toEpochMilli(), response.getCreatedAtTimestamp());
        assertNull(session.getDeletedTime());

        assertEquals("instructions", response.getInstructions());
        assertEquals(1444003051000L, response.getSubmissionStartTimestamp());
        assertEquals(1546003051000L, response.getSubmissionEndTimestamp());
        assertEquals(5, response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(1440003051000L, response.getCustomSessionVisibleTimestamp().longValue());

        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(1547003051000L, response.getCustomResponseVisibleTimestamp().longValue());

        assertFalse(response.getIsClosingEmailEnabled());
        assertFalse(response.getIsPublishedEmailEnabled());

        assertNotNull(response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());
    }

    @Test
    public void testExecute_startTimeEarlierThanVisibleTime_shouldGiveInvalidParametersError() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();
        updateRequest.setCustomSessionVisibleTimestamp(
                updateRequest.getSubmissionStartTime().plusSeconds(10).toEpochMilli());

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(updateRequest, param);
        assertEquals("The start time for this feedback session cannot be "
                + "earlier than the time when the session will be visible.", ihrbe.getMessage());
    }

    @Test
    public void testExecute_differentFeedbackSessionVisibleResponseVisibleSetting_shouldConvertToSpecialTime()
            throws Exception {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("success: Custom time zone, At open show session, 'later' show results");

        logic.updateCourseCascade(
                CourseAttributes.updateOptionsBuilder(course.getId())
                        .withTimezone("Asia/Kathmandu")
                        .build());

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();
        updateRequest.setSessionVisibleSetting(SessionVisibleSetting.AT_OPEN);
        updateRequest.setResponseVisibleSetting(ResponseVisibleSetting.LATER);

        UpdateFeedbackSessionAction a = getAction(updateRequest, param);
        getJsonResult(a);

        session = logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
        assertEquals(Const.TIME_REPRESENTS_FOLLOW_OPENING, session.getSessionVisibleFromTime());
        assertEquals(Const.TIME_REPRESENTS_LATER, session.getResultsVisibleFromTime());

        ______TS("success: At open session visible time, custom results visible time, UTC");

        logic.updateCourseCascade(
                CourseAttributes.updateOptionsBuilder(course.getId())
                        .withTimezone("UTC")
                        .build());

        param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        updateRequest = getTypicalFeedbackSessionUpdateRequest();
        updateRequest.setSessionVisibleSetting(SessionVisibleSetting.AT_OPEN);

        a = getAction(updateRequest, param);
        getJsonResult(a);

        session = logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
        assertEquals(Const.TIME_REPRESENTS_FOLLOW_OPENING, session.getSessionVisibleFromTime());
        assertEquals(1547003051000L, session.getResultsVisibleFromTime().toEpochMilli());
    }

    @Test
    public void testExecute_masqueradeModeWithManualReleaseResult_shouldEditSessionSuccessfully() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsAdmin();

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        param = addUserIdToParams(instructor1ofCourse1.getGoogleId(), param);
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();
        updateRequest.setResponseVisibleSetting(ResponseVisibleSetting.LATER);

        UpdateFeedbackSessionAction a = getAction(updateRequest, param);
        getJsonResult(a);
    }

    @Test
    public void testExecute_invalidRequestBody_shouldThrowException() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();
        updateRequest.setInstructions(null);

        verifyHttpRequestBodyFailure(updateRequest, param);
    }

    private FeedbackSessionUpdateRequest getTypicalFeedbackSessionUpdateRequest() {
        FeedbackSessionUpdateRequest updateRequest = new FeedbackSessionUpdateRequest();
        updateRequest.setInstructions("instructions");

        updateRequest.setSubmissionStartTimestamp(1444003051000L);
        updateRequest.setSubmissionEndTimestamp(1546003051000L);
        updateRequest.setGracePeriod(5);

        updateRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        updateRequest.setCustomSessionVisibleTimestamp(1440003051000L);

        updateRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        updateRequest.setCustomResponseVisibleTimestamp(1547003051000L);

        updateRequest.setClosingEmailEnabled(false);
        updateRequest.setPublishedEmailEnabled(false);

        return updateRequest;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("non-existent feedback session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "abcSession",
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyEntityNotFoundAcl(submissionParams);

        ______TS("inaccessible without ModifySessionPrivilege");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        verifyInaccessibleWithoutModifySessionPrivilege(submissionParams);

        ______TS("only instructors of the same course with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

}
