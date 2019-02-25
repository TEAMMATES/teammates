package teammates.test.cases.webapi;

import java.time.ZoneId;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.SaveFeedbackSessionAction;
import teammates.ui.webapi.output.FeedbackSessionData;
import teammates.ui.webapi.output.ResponseVisibleSetting;
import teammates.ui.webapi.output.SessionVisibleSetting;
import teammates.ui.webapi.request.FeedbackSessionSaveRequest;

/**
 * SUT: {@link SaveFeedbackSessionAction}.
 */
public class SaveFeedbackSessionActionTest extends BaseActionTest<SaveFeedbackSessionAction> {

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
    protected void testExecute() throws Exception {
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
        FeedbackSessionSaveRequest saveRequest = getTypicalFeedbackSessionSaveRequest();

        SaveFeedbackSessionAction a = getAction(saveRequest, param);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        session = logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
        assertEquals(session.getCourseId(), response.getCourseId());
        assertEquals(session.getTimeZone().getId(), response.getTimeZone());
        assertEquals(session.getFeedbackSessionName(), response.getFeedbackSessionName());

        assertEquals(session.getInstructions(), response.getInstructions());

        assertEquals(session.getStartTime().toEpochMilli(), response.getSubmissionStartTimestamp());
        assertEquals(session.getEndTime().toEpochMilli(), response.getSubmissionEndTimestamp());
        assertEquals(session.getGracePeriodMinutes(), response.getGracePeriod());

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
        assertEquals(5, response.getGracePeriod());

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
        FeedbackSessionSaveRequest saveRequest = getTypicalFeedbackSessionSaveRequest();
        saveRequest.setCustomSessionVisibleTimestamp(saveRequest.getSubmissionStartTime().plusSeconds(10).toEpochMilli());

        InvalidHttpRequestBodyException ihrbe = assertThrows(InvalidHttpRequestBodyException.class, () -> {
            SaveFeedbackSessionAction a = getAction(saveRequest, param);
            getJsonResult(a);
        });

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
                        .withTimezone(ZoneId.of("Asia/Kathmandu"))
                        .build());

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        FeedbackSessionSaveRequest saveRequest = getTypicalFeedbackSessionSaveRequest();
        saveRequest.setSessionVisibleSetting(SessionVisibleSetting.AT_OPEN);
        saveRequest.setResponseVisibleSetting(ResponseVisibleSetting.LATER);

        SaveFeedbackSessionAction a = getAction(saveRequest, param);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        session = logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
        assertEquals(Const.TIME_REPRESENTS_FOLLOW_OPENING, session.getSessionVisibleFromTime());
        assertEquals(Const.TIME_REPRESENTS_LATER, session.getResultsVisibleFromTime());

        ______TS("success: At open session visible time, custom results visible time, UTC");

        logic.updateCourseCascade(
                CourseAttributes.updateOptionsBuilder(course.getId())
                        .withTimezone(ZoneId.of("UTC"))
                        .build());

        param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        saveRequest = getTypicalFeedbackSessionSaveRequest();
        saveRequest.setSessionVisibleSetting(SessionVisibleSetting.AT_OPEN);

        a = getAction(saveRequest, param);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

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
        FeedbackSessionSaveRequest saveRequest = getTypicalFeedbackSessionSaveRequest();
        saveRequest.setResponseVisibleSetting(ResponseVisibleSetting.LATER);

        SaveFeedbackSessionAction a = getAction(saveRequest, param);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
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
        FeedbackSessionSaveRequest saveRequest = getTypicalFeedbackSessionSaveRequest();
        saveRequest.setInstructions(null);

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            SaveFeedbackSessionAction a = getAction(saveRequest, param);
            getJsonResult(a);
        });
    }

    private FeedbackSessionSaveRequest getTypicalFeedbackSessionSaveRequest() {
        FeedbackSessionSaveRequest saveRequest = new FeedbackSessionSaveRequest();
        saveRequest.setInstructions("instructions");

        saveRequest.setSubmissionStartTimestamp(1444003051000L);
        saveRequest.setSubmissionEndTimestamp(1546003051000L);
        saveRequest.setGracePeriod(5);

        saveRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        saveRequest.setCustomSessionVisibleTimestamp(1440003051000L);

        saveRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        saveRequest.setCustomResponseVisibleTimestamp(1547003051000L);

        saveRequest.setClosingEmailEnabled(false);
        saveRequest.setPublishedEmailEnabled(false);

        return saveRequest;
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

        loginAsInstructor(instructor1OfCourse1.googleId);
        verifyCannotAccess(submissionParams);

        ______TS("inaccessible without ModifySessionPrivilege");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        verifyInaccessibleWithoutModifySessionPrivilege(submissionParams);

        ______TS("only instructors of the same course can access");

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
