package teammates.ui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
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
    protected void testExecute() {
        // TODO: Add test cases

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");
        String timeZone = feedbackSessionAttributes.getTimeZone();

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

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

        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();
        assertEquals(feedbackSessionAttributes.getCourseId(), response.getCourseId());
        assertEquals(feedbackSessionAttributes.getFeedbackSessionName(), response.getFeedbackSessionName());
        assertEquals(timeZone, response.getTimeZone());
        assertEquals(feedbackSessionAttributes.getInstructions(), response.getInstructions());

        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(feedbackSessionAttributes.getStartTime(),
                timeZone, true).toEpochMilli(),
                response.getSubmissionStartTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(feedbackSessionAttributes.getEndTime(),
                timeZone, true).toEpochMilli(),
                response.getSubmissionEndTimestamp());
        assertEquals(feedbackSessionAttributes.getGracePeriodMinutes(), response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(feedbackSessionAttributes.getSessionVisibleFromTime(),
                timeZone, true).toEpochMilli(),
                response.getCustomSessionVisibleTimestamp().longValue());

        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(feedbackSessionAttributes.getResultsVisibleFromTime(),
                timeZone, true).toEpochMilli(),
                response.getCustomResponseVisibleTimestamp().longValue());

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());
        assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, response.getPublishStatus());

        assertEquals(feedbackSessionAttributes.isClosingEmailEnabled(), response.getIsClosingEmailEnabled());
        assertEquals(feedbackSessionAttributes.isPublishedEmailEnabled(), response.getIsPublishedEmailEnabled());

        assertEquals(feedbackSessionAttributes.getCreatedTime().toEpochMilli(), response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());

        assertEqualDeadlines(feedbackSessionAttributes.getStudentDeadlines(), response.getStudentDeadlines(), timeZone);
        assertEqualDeadlines(feedbackSessionAttributes.getInstructorDeadlines(), response.getInstructorDeadlines(),
                timeZone);

        logoutUser();
    }

    @Test
    protected void testExecute_fullDetail() {

        ______TS("get full detail; no extensions; before end time");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        Map<String, Long> relativeStudentDeadlines = new HashMap<>();
        Map<String, Long> relativeInstructorDeadlines = new HashMap<>();
        FeedbackSessionAttributes relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(60 * 60,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, relativeFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, relativeFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        String timeZone = relativeFeedbackSession.getTimeZone();
        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(relativeFeedbackSession.getCourseId(), response.getCourseId());
        assertEquals(relativeFeedbackSession.getFeedbackSessionName(), response.getFeedbackSessionName());
        assertEquals(timeZone, response.getTimeZone());
        assertEquals(relativeFeedbackSession.getInstructions(), response.getInstructions());

        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getStartTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionStartTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getEndTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionEndTimestamp());
        assertEquals(relativeFeedbackSession.getGracePeriodMinutes(), response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getSessionVisibleFromTime(),
                        timeZone, true).toEpochMilli(),
                response.getSessionVisibleFromTimestamp().longValue());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getSessionVisibleFromTime(),
                        timeZone, true).toEpochMilli(),
                response.getCustomSessionVisibleTimestamp().longValue());

        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getResultsVisibleFromTime(),
                        timeZone, true).toEpochMilli(),
                response.getResultVisibleFromTimestamp().longValue());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getResultsVisibleFromTime(),
                        timeZone, true).toEpochMilli(),
                response.getCustomResponseVisibleTimestamp().longValue());

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());
        assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, response.getPublishStatus());

        assertEquals(relativeFeedbackSession.isClosingEmailEnabled(), response.getIsClosingEmailEnabled());
        assertEquals(relativeFeedbackSession.isPublishedEmailEnabled(), response.getIsPublishedEmailEnabled());

        assertEquals(relativeFeedbackSession.getCreatedTime().toEpochMilli(), response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());

        assertEqualDeadlines(relativeFeedbackSession.getStudentDeadlines(), response.getStudentDeadlines(), timeZone);
        assertEqualDeadlines(relativeFeedbackSession.getInstructorDeadlines(), response.getInstructorDeadlines(),
                timeZone);

        ______TS("get full detail; no extensions; after end time but within grace period");

        relativeStudentDeadlines = new HashMap<>();
        relativeInstructorDeadlines = new HashMap<>();
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertEqualDeadlines(relativeFeedbackSession.getStudentDeadlines(), response.getStudentDeadlines(), timeZone);
        assertEqualDeadlines(relativeFeedbackSession.getInstructorDeadlines(), response.getInstructorDeadlines(),
                timeZone);

        ______TS("get full detail; no extensions; after end time and beyond grace period");

        relativeStudentDeadlines = new HashMap<>();
        relativeInstructorDeadlines = new HashMap<>();
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertEqualDeadlines(relativeFeedbackSession.getStudentDeadlines(), response.getStudentDeadlines(), timeZone);
        assertEqualDeadlines(relativeFeedbackSession.getInstructorDeadlines(), response.getInstructorDeadlines(),
                timeZone);

        ______TS("get full detail; some extensions; before end time");

        relativeStudentDeadlines.put("student1InCourse1@gmail.tmt", 60L * 60 * 21);
        relativeStudentDeadlines.put("student2InCourse1@gmail.tmt", 60L * 60 * 22);
        relativeInstructorDeadlines.put("instr1@course1.tmt", 60L * 60 * 23);
        relativeInstructorDeadlines.put("instr2@course1.tmt", 60L * 60 * 24);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(60 * 60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertEqualDeadlines(relativeFeedbackSession.getStudentDeadlines(), response.getStudentDeadlines(), timeZone);
        assertEqualDeadlines(relativeFeedbackSession.getInstructorDeadlines(), response.getInstructorDeadlines(),
                timeZone);

        ______TS("get full detail; some extensions; after end time but within grace period");

        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertEqualDeadlines(relativeFeedbackSession.getStudentDeadlines(), response.getStudentDeadlines(), timeZone);
        assertEqualDeadlines(relativeFeedbackSession.getInstructorDeadlines(), response.getInstructorDeadlines(),
                timeZone);

        ______TS("get full detail; some extensions; after end time and beyond grace period");

        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertEqualDeadlines(relativeFeedbackSession.getStudentDeadlines(), response.getStudentDeadlines(), timeZone);
        assertEqualDeadlines(relativeFeedbackSession.getInstructorDeadlines(), response.getInstructorDeadlines(),
                timeZone);

        logoutUser();
    }

    @Test
    protected void testExecute_instructorSubmission() {

        ______TS("get submission by instructor with no extension; before end time");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        Map<String, Long> relativeStudentDeadlines = new HashMap<>();
        relativeStudentDeadlines.put("student1InCourse1@gmail.tmt", 60L * 60 * 21);
        relativeStudentDeadlines.put("student2InCourse1@gmail.tmt", 60L * 60 * 22);
        Map<String, Long> relativeInstructorDeadlines = new HashMap<>();
        relativeInstructorDeadlines.put("instr2@course1.tmt", 60L * 60 * 24);
        FeedbackSessionAttributes relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(60 * 60,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, relativeFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, relativeFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        String timeZone = relativeFeedbackSession.getTimeZone();
        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(relativeFeedbackSession.getCourseId(), response.getCourseId());
        assertEquals(relativeFeedbackSession.getFeedbackSessionName(), response.getFeedbackSessionName());
        assertEquals(timeZone, response.getTimeZone());
        assertEquals(relativeFeedbackSession.getInstructions(), response.getInstructions());

        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getStartTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionStartTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getEndTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionEndTimestamp());
        assertNull(response.getGracePeriod());

        assertNull(response.getSessionVisibleSetting());
        assertNull(response.getSessionVisibleFromTimestamp());
        assertNull(response.getCustomSessionVisibleTimestamp());

        assertNull(response.getResponseVisibleSetting());
        assertNull(response.getResultVisibleFromTimestamp());
        assertNull(response.getCustomResponseVisibleTimestamp());

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());
        assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, response.getPublishStatus());

        assertNull(response.getIsClosingEmailEnabled());
        assertNull(response.getIsPublishedEmailEnabled());

        assertEquals(0, response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get submission by instructor with no extension; after end time but within grace period");

        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get submission by instructor with no extension; after end time and beyond grace period");

        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get submission by instructor with extension; before end time");

        relativeInstructorDeadlines.put(instructor1OfCourse1.getEmail(), 60L * 60 * 23);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(60 * 60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().containsKey(instructor1OfCourse1.getEmail()));
        assertEquals(1, response.getInstructorDeadlines().size());

        ______TS("get submission by instructor with extension; after end time but before extended deadline");

        relativeInstructorDeadlines.put(instructor1OfCourse1.getEmail(), 60L * 60 * 3);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().containsKey(instructor1OfCourse1.getEmail()));
        assertEquals(1, response.getInstructorDeadlines().size());

        ______TS("get submission by instructor with extension; after extended deadline but within grace period");

        relativeInstructorDeadlines.put(instructor1OfCourse1.getEmail(), -60L * 3);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().containsKey(instructor1OfCourse1.getEmail()));
        assertEquals(1, response.getInstructorDeadlines().size());

        ______TS("get submission by instructor with extension; after extended deadline and beyond grace period");

        relativeInstructorDeadlines.put(instructor1OfCourse1.getEmail(), -60L * 60 * 3);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().containsKey(instructor1OfCourse1.getEmail()));
        assertEquals(1, response.getInstructorDeadlines().size());

        logoutUser();
    }

    @Test
    protected void testExecute_instructorResult() {

        ______TS("get result by instructor with no extension; before end time");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        Map<String, Long> relativeStudentDeadlines = new HashMap<>();
        relativeStudentDeadlines.put("student1InCourse1@gmail.tmt", 60L * 60 * 21);
        relativeStudentDeadlines.put("student2InCourse1@gmail.tmt", 60L * 60 * 22);
        Map<String, Long> relativeInstructorDeadlines = new HashMap<>();
        relativeInstructorDeadlines.put("instr2@course1.tmt", 60L * 60 * 24);
        FeedbackSessionAttributes relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(60 * 60,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, relativeFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, relativeFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        String timeZone = relativeFeedbackSession.getTimeZone();
        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(relativeFeedbackSession.getCourseId(), response.getCourseId());
        assertEquals(relativeFeedbackSession.getFeedbackSessionName(), response.getFeedbackSessionName());
        assertEquals(timeZone, response.getTimeZone());
        assertEquals(relativeFeedbackSession.getInstructions(), response.getInstructions());

        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getStartTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionStartTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getEndTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionEndTimestamp());
        assertNull(response.getGracePeriod());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getSessionVisibleFromTime(),
                        timeZone, true).toEpochMilli(),
                response.getSessionVisibleFromTimestamp().longValue());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getSessionVisibleFromTime(),
                        timeZone, true).toEpochMilli(),
                response.getCustomSessionVisibleTimestamp().longValue());

        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getResultsVisibleFromTime(),
                        timeZone, true).toEpochMilli(),
                response.getResultVisibleFromTimestamp().longValue());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getResultsVisibleFromTime(),
                        timeZone, true).toEpochMilli(),
                response.getCustomResponseVisibleTimestamp().longValue());

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());
        assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, response.getPublishStatus());

        assertNull(response.getIsClosingEmailEnabled());
        assertNull(response.getIsPublishedEmailEnabled());

        assertEquals(0, response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get result by instructor with no extension; after end time but within grace period");

        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get result by instructor with no extension; after end time and beyond grace period");

        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get result by instructor with extension; before end time");

        relativeInstructorDeadlines.put(instructor1OfCourse1.getEmail(), 60L * 60 * 23);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(60 * 60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().containsKey(instructor1OfCourse1.getEmail()));
        assertEquals(1, response.getInstructorDeadlines().size());

        ______TS("get result by instructor with extension; after end time but before extended deadline");

        relativeInstructorDeadlines.put(instructor1OfCourse1.getEmail(), 60L * 60 * 3);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().containsKey(instructor1OfCourse1.getEmail()));
        assertEquals(1, response.getInstructorDeadlines().size());

        ______TS("get result by instructor with extension; after extended deadline but within grace period");

        relativeInstructorDeadlines.put(instructor1OfCourse1.getEmail(), -60L * 3);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().containsKey(instructor1OfCourse1.getEmail()));
        assertEquals(1, response.getInstructorDeadlines().size());

        ______TS("get result by instructor with extension; after extended deadline and beyond grace period");

        relativeInstructorDeadlines.put(instructor1OfCourse1.getEmail(), -60L * 60 * 3);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().containsKey(instructor1OfCourse1.getEmail()));
        assertEquals(1, response.getInstructorDeadlines().size());

        logoutUser();
    }

    @Test
    protected void textExecute_studentSubmission() {

        ______TS("get submission by student with no extension; before end time");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());

        Map<String, Long> relativeStudentDeadlines = new HashMap<>();
        relativeStudentDeadlines.put("student2InCourse1@gmail.tmt", 60L * 60 * 22);
        Map<String, Long> relativeInstructorDeadlines = new HashMap<>();
        relativeInstructorDeadlines.put("instr1@course1.tmt", 60L * 60 * 23);
        relativeInstructorDeadlines.put("instr2@course1.tmt", 60L * 60 * 24);
        FeedbackSessionAttributes relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(60 * 60,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, relativeFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, relativeFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        String timeZone = relativeFeedbackSession.getTimeZone();
        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(relativeFeedbackSession.getCourseId(), response.getCourseId());
        assertEquals(relativeFeedbackSession.getFeedbackSessionName(), response.getFeedbackSessionName());
        assertEquals(timeZone, response.getTimeZone());
        assertEquals(relativeFeedbackSession.getInstructions(), response.getInstructions());

        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getStartTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionStartTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getEndTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionEndTimestamp());
        assertNull(response.getGracePeriod());

        assertNull(response.getSessionVisibleSetting());
        assertNull(response.getSessionVisibleFromTimestamp());
        assertNull(response.getCustomSessionVisibleTimestamp());

        assertNull(response.getResponseVisibleSetting());
        assertNull(response.getResultVisibleFromTimestamp());
        assertNull(response.getCustomResponseVisibleTimestamp());

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());
        assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, response.getPublishStatus());

        assertNull(response.getIsClosingEmailEnabled());
        assertNull(response.getIsPublishedEmailEnabled());

        assertEquals(0, response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get submission by student with no extension; after end time but within grace period");

        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get submission by student with no extension; after end time and beyond grace period");

        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get submission by student with extension; before end time");

        relativeStudentDeadlines.put(student1InCourse1.getEmail(), 60L * 60 * 21);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(60 * 60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1InCourse1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get submission by student with extension; after end time but before extended deadline");

        relativeStudentDeadlines.put(student1InCourse1.getEmail(), 60L * 60);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1InCourse1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get submission by student with extension; after extended deadline but within grace period");

        relativeStudentDeadlines.put(student1InCourse1.getEmail(), -60L);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1InCourse1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get submission by student with extension; after extended deadline and beyond grace period");

        relativeStudentDeadlines.put(student1InCourse1.getEmail(), -60L * 60);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1InCourse1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        logoutUser();
    }

    @Test
    protected void testExecute_studentResult() {

        ______TS("get result by student with no extension; before end time");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());

        Map<String, Long> relativeStudentDeadlines = new HashMap<>();
        relativeStudentDeadlines.put("student2InCourse1@gmail.tmt", 60L * 60 * 22);
        Map<String, Long> relativeInstructorDeadlines = new HashMap<>();
        relativeInstructorDeadlines.put("instr1@course1.tmt", 60L * 60 * 23);
        relativeInstructorDeadlines.put("instr2@course1.tmt", 60L * 60 * 24);
        FeedbackSessionAttributes relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(60 * 60,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, relativeFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, relativeFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };
        String timeZone = relativeFeedbackSession.getTimeZone();
        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(relativeFeedbackSession.getCourseId(), response.getCourseId());
        assertEquals(relativeFeedbackSession.getFeedbackSessionName(), response.getFeedbackSessionName());
        assertEquals(timeZone, response.getTimeZone());
        assertEquals(relativeFeedbackSession.getInstructions(), response.getInstructions());

        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getStartTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionStartTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(relativeFeedbackSession.getEndTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionEndTimestamp());
        assertNull(response.getGracePeriod());

        assertNull(response.getSessionVisibleSetting());
        assertNull(response.getSessionVisibleFromTimestamp());
        assertNull(response.getCustomSessionVisibleTimestamp());

        assertNull(response.getResponseVisibleSetting());
        assertNull(response.getResultVisibleFromTimestamp());
        assertNull(response.getCustomResponseVisibleTimestamp());

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());
        assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, response.getPublishStatus());

        assertNull(response.getIsClosingEmailEnabled());
        assertNull(response.getIsPublishedEmailEnabled());

        assertEquals(0, response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get result by student with no extension; after end time but within grace period");

        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get result by student with no extension; after end time and beyond grace period");

        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get result by student with extension; before end time");

        relativeStudentDeadlines.put(student1InCourse1.getEmail(), 60L * 60 * 21);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(60 * 60, relativeStudentDeadlines,
                relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1InCourse1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get result by student with extension; after end time but before extended deadline");

        relativeStudentDeadlines.put(student1InCourse1.getEmail(), 60L * 60);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1InCourse1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get result by student with extension; after extended deadline but within grace period");

        relativeStudentDeadlines.put(student1InCourse1.getEmail(), -60L);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1InCourse1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        ______TS("get result by student with extension; after extended deadline and beyond grace period");

        relativeStudentDeadlines.put(student1InCourse1.getEmail(), -60L * 60);
        relativeFeedbackSession = generateRelativeFeedbackSessionInTypicalCourse1(-60 * 60 * 24,
                relativeStudentDeadlines, relativeInstructorDeadlines);
        typicalBundle.feedbackSessions.put("relativeFeedbackSession", relativeFeedbackSession);
        removeAndRestoreDataBundle(typicalBundle);
        a = getAction(params);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1InCourse1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());

        logoutUser();
    }

    @Override
    @Test
    protected void testAccessControl() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("non-existent feedback session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName for Session123",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyEntityNotFoundAcl(submissionParams);

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
    protected void testAccessControl_studentResult() throws Exception {
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

        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").getGoogleId());
        verifyCannotAccess(params);

        ______TS("student cannot access other course session");
        FeedbackSessionAttributes otherCourseFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse2");
        params = generateParameters(otherCourseFeedbackSession, intent, "", "", "");
        verifyCannotAccess(params);

        ______TS("Instructor with correct privilege moderate student session");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        params = generateParameters(feedbackSession, intent, "", student1InCourse1.getEmail(), "");

        verifyInaccessibleForInstructorsOfOtherCourses(params);
        verifyInaccessibleForStudents(params);

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");
        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(params);

        grantInstructorWithSectionPrivilege(helperOfCourse1,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section 1"});
        verifyCanAccess(params);
        verifyAccessibleForAdminToMasqueradeAsInstructor(helperOfCourse1, params);

        ______TS("Instructor preview student session");
        params = generateParameters(feedbackSession, intent, "", "", student1InCourse1.getEmail());

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
    protected void testAccessControl_instructorResult() throws Exception {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Intent intent = Intent.INSTRUCTOR_RESULT;
        String[] params = generateParameters(feedbackSession, intent, "", "", "");
        ______TS("Only instructor with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, params
        );

        ______TS("Instructor moderates instructor submission with correct privilege will pass");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        params = generateParameters(feedbackSession, Intent.INSTRUCTOR_SUBMISSION, "", instructor1OfCourse1.getEmail(), "");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, params);

        ______TS("Instructor preview instructor result with correct privilege will pass");

        String[] previewInstructorSubmissionParams =
                generateParameters(feedbackSession, Intent.INSTRUCTOR_SUBMISSION,
                        "", "", instructor1OfCourse1.getEmail());
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

    private void assertEqualDeadlines(Map<String, Instant> expectedDeadlineInstants, Map<String, Long> actualDeadlines,
            String timeZone) {
        Map<String, Long> expectedDeadlines = new HashMap<>();
        expectedDeadlineInstants.forEach((emailAddress, deadlineInstant) -> {
            Long deadline = TimeHelper.getMidnightAdjustedInstantBasedOnZone(deadlineInstant, timeZone, true)
                    .toEpochMilli();
            expectedDeadlines.put(emailAddress, deadline);
        });
        assertEquals(expectedDeadlines, actualDeadlines);
    }

    private FeedbackSessionAttributes generateRelativeFeedbackSessionInTypicalCourse1(long secondsUntilEndTime,
            Map<String, Long> relativeStudentDeadlines, Map<String, Long> relativeInstructorDeadlines) {
        Instant now = Instant.now();
        Instant startTime = now.minusSeconds(60 * 60 * 24);
        Map<String, Instant> studentDeadlines = new HashMap<>();
        relativeStudentDeadlines.forEach((emailAddress, relativeStudentDeadline) -> {
            studentDeadlines.put(emailAddress, now.plusSeconds(relativeStudentDeadline));
        });
        Map<String, Instant> instructorDeadlines = new HashMap<>();
        relativeInstructorDeadlines.forEach((emailAddress, relativeInstructorDeadline) -> {
            instructorDeadlines.put(emailAddress, now.plusSeconds(relativeInstructorDeadline));
        });
        return FeedbackSessionAttributes
                .builder("relativeFeedbackSession", "idOfTypicalCourse1")
                .withCreatorEmail("instructor1@course1.tmt")
                .withSessionVisibleFromTime(startTime.minusSeconds(60 * 60))
                .withResultsVisibleFromTime(now.plusSeconds(60 * 60 * 24))
                .withStartTime(startTime)
                .withEndTime(now.plusSeconds(secondsUntilEndTime))
                .withGracePeriod(Duration.ofMinutes(15))
                .withStudentDeadlines(studentDeadlines)
                .withInstructorDeadlines(instructorDeadlines)
                .build();
    }
}
