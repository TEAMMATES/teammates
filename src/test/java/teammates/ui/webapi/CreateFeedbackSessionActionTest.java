package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelperExtension;
import teammates.common.util.TimeHelperExtension;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackSessionCreateRequest;

/**
 * SUT: {@link CreateFeedbackSessionAction}.
 */
public class CreateFeedbackSessionActionTest extends BaseActionTest<CreateFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case");

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        FeedbackSessionCreateRequest createRequest = getTypicalCreateRequest(course.getTimeZone());

        CreateFeedbackSessionAction a = getAction(createRequest, params);
        JsonResult r = getJsonResult(a);

        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        FeedbackSessionAttributes createdSession =
                logic.getFeedbackSession(createRequest.getFeedbackSessionName(), course.getId());
        assertEquals(createdSession.getCourseId(), response.getCourseId());
        assertEquals(createdSession.getTimeZone(), response.getTimeZone());
        assertEquals(createdSession.getFeedbackSessionName(), response.getFeedbackSessionName());

        assertEquals(createdSession.getInstructions(), response.getInstructions());

        assertEquals(createdSession.getStartTime().toEpochMilli(), response.getSubmissionStartTimestamp());
        assertEquals(createdSession.getEndTime().toEpochMilli(), response.getSubmissionEndTimestamp());
        assertEquals(createdSession.getGracePeriodMinutes(), response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(createdSession.getSessionVisibleFromTime().toEpochMilli(),
                response.getCustomSessionVisibleTimestamp().longValue());
        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(createdSession.getResultsVisibleFromTime().toEpochMilli(),
                response.getCustomResponseVisibleTimestamp().longValue());

        assertEquals(createdSession.isClosingEmailEnabled(), response.getIsClosingEmailEnabled());
        assertEquals(createdSession.isPublishedEmailEnabled(), response.getIsPublishedEmailEnabled());

        assertEquals(createdSession.getCreatedTime().toEpochMilli(), response.getCreatedAtTimestamp());
        assertNull(createdSession.getDeletedTime());

        assertEquals("new feedback session", response.getFeedbackSessionName());
        assertEquals("instructions", response.getInstructions());
        assertEquals(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, createdSession.getTimeZone()).toEpochMilli(), response.getSubmissionStartTimestamp());
        assertEquals(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, createdSession.getTimeZone()).toEpochMilli(), response.getSubmissionEndTimestamp());
        assertEquals(5, response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, createdSession.getTimeZone()).toEpochMilli(), response.getCustomSessionVisibleTimestamp().longValue());

        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, createdSession.getTimeZone()).toEpochMilli(), response.getCustomResponseVisibleTimestamp().longValue());

        assertFalse(response.getIsClosingEmailEnabled());
        assertFalse(response.getIsPublishedEmailEnabled());

        assertNotNull(response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());

        ______TS("Error: try to add the same session again");

        verifyInvalidOperation(getTypicalCreateRequest(course.getTimeZone()), params);

        ______TS("Error: Invalid parameters (invalid session name > 64 characters)");

        FeedbackSessionCreateRequest request = getTypicalCreateRequest(course.getTimeZone());
        request.setFeedbackSessionName(StringHelperExtension.generateStringOfLength(65));
        verifyHttpRequestBodyFailure(request, params);

        ______TS("Unsuccessful case: test null session name");

        request = getTypicalCreateRequest(course.getTimeZone());
        request.setFeedbackSessionName(null);
        verifyHttpRequestBodyFailure(request, params);

        ______TS("Add course with extra space (in middle and trailing)");

        createRequest = getTypicalCreateRequest(course.getTimeZone());
        createRequest.setFeedbackSessionName("Name with extra  space ");

        a = getAction(createRequest, params);
        r = getJsonResult(a);

        response = (FeedbackSessionData) r.getOutput();

        assertEquals("Name with extra space", response.getFeedbackSessionName());

        ______TS("Copy feedback session case");
        FeedbackSessionAttributes toCopySession = typicalBundle.feedbackSessions.get("session1InCourse2");
        createRequest = getCopySessionCreateRequest(toCopySession);
        a = getAction(createRequest, params);
        r = getJsonResult(a);

        response = (FeedbackSessionData) r.getOutput();

        FeedbackSessionAttributes copiedSession =
                logic.getFeedbackSession(createRequest.getFeedbackSessionName(), course.getId());
        assertEquals(copiedSession.getCourseId(), response.getCourseId());
        assertEquals(copiedSession.getTimeZone(), response.getTimeZone());
        assertEquals(copiedSession.getFeedbackSessionName(), response.getFeedbackSessionName());
        assertEquals(copiedSession.getInstructions(), response.getInstructions());

        assertEquals(copiedSession.getStartTime().toEpochMilli(), response.getSubmissionStartTimestamp());
        assertEquals(copiedSession.getEndTime().toEpochMilli(), response.getSubmissionEndTimestamp());
        assertEquals(copiedSession.getGracePeriodMinutes(), response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(copiedSession.getSessionVisibleFromTime().toEpochMilli(),
                response.getCustomSessionVisibleTimestamp().longValue());
        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(copiedSession.getResultsVisibleFromTime().toEpochMilli(),
                response.getCustomResponseVisibleTimestamp().longValue());

        assertEquals(copiedSession.isClosingEmailEnabled(), response.getIsClosingEmailEnabled());
        assertEquals(copiedSession.isPublishedEmailEnabled(), response.getIsPublishedEmailEnabled());

        assertEquals(copiedSession.getCreatedTime().toEpochMilli(), response.getCreatedAtTimestamp());
        assertNull(copiedSession.getDeletedTime());

        assertEquals("copied feedback session", response.getFeedbackSessionName());
        assertEquals(copiedSession.getInstructions(), toCopySession.getInstructions());
        assertEquals(copiedSession.getStartTime(),
                TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(2, toCopySession.getTimeZone()));
        assertEquals(copiedSession.getEndTime(),
                TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(7, toCopySession.getTimeZone()));
        assertEquals(copiedSession.getGracePeriodMinutes(), toCopySession.getGracePeriodMinutes());
        assertEquals(copiedSession.getSessionVisibleFromTime(),
                TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(2, toCopySession.getTimeZone()));
        assertEquals(copiedSession.getResultsVisibleFromTime(),
                TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(7, toCopySession.getTimeZone()));
        assertEquals(copiedSession.isOpeningEmailEnabled(), toCopySession.isOpeningEmailEnabled());
        assertEquals(copiedSession.isClosingEmailEnabled(), toCopySession.isClosingEmailEnabled());
        assertEquals(copiedSession.isPublishedEmailEnabled(), toCopySession.isPublishedEmailEnabled());

        FeedbackQuestionAttributes feedbackQuestion = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse2");
        FeedbackQuestionAttributes copiedQuestion =
                logic.getFeedbackQuestion(copiedSession.getFeedbackSessionName(), copiedSession.getCourseId(), 1);
        assertEquals(feedbackQuestion.getQuestionDetails(), copiedQuestion.getQuestionDetails());
        assertEquals(feedbackQuestion.getQuestionDescription(), copiedQuestion.getQuestionDescription());
        assertEquals(feedbackQuestion.getGiverType(), copiedQuestion.getGiverType());
        assertEquals(feedbackQuestion.getRecipientType(), copiedQuestion.getRecipientType());
        assertEquals(feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo(),
                        copiedQuestion.getNumberOfEntitiesToGiveFeedbackTo());
        assertEquals(feedbackQuestion.getShowResponsesTo(), copiedQuestion.getShowResponsesTo());
        assertEquals(feedbackQuestion.getShowGiverNameTo(), copiedQuestion.getShowGiverNameTo());
        assertEquals(feedbackQuestion.getShowRecipientNameTo(), copiedQuestion.getShowRecipientNameTo());
    }

    @Test
    public void testExecute_masqueradeMode_shouldCreateFeedbackSession() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        params = addUserIdToParams(instructor1ofCourse1.getGoogleId(), params);

        FeedbackSessionCreateRequest createRequest = getTypicalCreateRequest(course.getTimeZone());

        CreateFeedbackSessionAction a = getAction(createRequest, params);
        getJsonResult(a);
    }

    private FeedbackSessionCreateRequest getTypicalCreateRequest(String timeZone) {
        FeedbackSessionCreateRequest createRequest =
                new FeedbackSessionCreateRequest();
        createRequest.setFeedbackSessionName("new feedback session");
        createRequest.setInstructions("instructions");

        // Preprocess session timings to adhere stricter checks
        createRequest.setSubmissionStartTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, timeZone).toEpochMilli());
        createRequest.setSubmissionEndTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, timeZone).toEpochMilli());
        createRequest.setGracePeriod(5);

        createRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        createRequest.setCustomSessionVisibleTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, timeZone).toEpochMilli());

        createRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        createRequest.setCustomResponseVisibleTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, timeZone).toEpochMilli());

        createRequest.setClosingEmailEnabled(false);
        createRequest.setPublishedEmailEnabled(false);

        return createRequest;
    }

    private FeedbackSessionCreateRequest getCopySessionCreateRequest(FeedbackSessionAttributes toCopySession) {
        FeedbackSessionCreateRequest createRequest = new FeedbackSessionCreateRequest();
        createRequest.setFeedbackSessionName("copied feedback session");
        createRequest.setToCopyCourseId(toCopySession.getCourseId());
        createRequest.setToCopySessionName(toCopySession.getFeedbackSessionName());
        createRequest.setInstructions(toCopySession.getInstructions());

        // Preprocess session timings preprocessing to adhere stricter checks
        createRequest.setSubmissionStartTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, toCopySession.getTimeZone()).toEpochMilli());
        createRequest.setSubmissionEndTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, toCopySession.getTimeZone()).toEpochMilli());
        createRequest.setGracePeriod(toCopySession.getGracePeriodMinutes());

        createRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        createRequest.setCustomSessionVisibleTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, toCopySession.getTimeZone()).toEpochMilli());

        createRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        createRequest.setCustomResponseVisibleTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, toCopySession.getTimeZone()).toEpochMilli());

        createRequest.setClosingEmailEnabled(toCopySession.isClosingEmailEnabled());
        createRequest.setPublishedEmailEnabled(toCopySession.isPublishedEmailEnabled());
        return createRequest;
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, params);
    }

}
