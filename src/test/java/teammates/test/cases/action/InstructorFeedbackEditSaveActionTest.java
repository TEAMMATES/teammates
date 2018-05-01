package teammates.test.cases.action;

import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackEditSaveAction;
import teammates.ui.pagedata.InstructorFeedbackEditPageData;

/**
 * SUT: {@link InstructorFeedbackEditSaveAction}.
 */
public class InstructorFeedbackEditSaveActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws TeammatesException {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        String expectedString = "";

        ______TS("failure: Not enough parameters");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        verifyAssumptionFailure();
        //TODO make sure IFESA does assertNotNull for required parameters then uncomment
        //verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId,
        //                        Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName);

        ______TS("success: Typical case");

        String[] params = createParamsForTypicalFeedbackSession(instructor1ofCourse1.courseId,
                                                                session.getFeedbackSessionName());

        InstructorFeedbackEditSaveAction a = getAction(params);
        AjaxResult ar = getAjaxResult(a);
        InstructorFeedbackEditPageData pageData = (InstructorFeedbackEditPageData) ar.data;

        StatusMessage statusMessage = pageData.getStatusMessagesToUser().get(0);
        verifyStatusMessage(statusMessage, Const.StatusMessages.FEEDBACK_SESSION_EDITED, StatusMessageColor.SUCCESS);
        assertFalse(pageData.getHasError());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditSave|||instructorFeedbackEditSave|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||Updated Feedback Session "
                + "<span class=\"bold\">(First feedback session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> 2012-01-31T22:00:00Z"
                + "<span class=\"bold\"> to</span> 2014-12-31T22:00:00Z<br>"
                + "<span class=\"bold\">Session visible from:</span> 2011-12-31T22:00:00Z<br>"
                + "<span class=\"bold\">Results visible from:</span> 1970-06-22T00:00:00Z<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: instructions>|||/page/instructorFeedbackEditSave";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("failure: Invalid parameters");

        params[15] = "Thu, 01 Mar, 2012";
        // params[25] = "UTC";

        a = getAction(params);
        ar = getAjaxResult(a);
        pageData = (InstructorFeedbackEditPageData) ar.data;

        expectedString = "The start time for this feedback session cannot be "
                         + "earlier than the time when the session will be visible.";
        statusMessage = pageData.getStatusMessagesToUser().get(0);
        verifyStatusMessage(statusMessage, expectedString, StatusMessageColor.DANGER);
        assertTrue(pageData.getHasError());

        ______TS("success: Time zone with DST, gap start time, overlap end time");

        backDoorLogic.updateCourse(course.getId(), course.getName(), "Europe/Andorra");
        // After Sun, 25 Mar 2012, 01:59:59 AM: clocks sprang forward to Sun, 25 Mar 2012, 03:00:00 AM
        params[5] = "Sun, 25 Mar, 2012";
        params[7] = "2";
        // After Sun, 28 Oct 2012, 02:59:59 AM: clocks fell back to Sun, 28 Oct 2012, 02:00:00 AM
        params[9] = "Sun, 28 Oct, 2012";
        params[11] = "2";

        a = getAction(params);
        ar = getAjaxResult(a);
        pageData = (InstructorFeedbackEditPageData) ar.data;

        expectedString = String.format(Const.StatusMessages.AMBIGUOUS_LOCAL_DATE_TIME_GAP,
                "start time", "Sun, 25 Mar 2012, 02:00 AM", "Sun, 25 Mar 2012, 03:00 AM CEST (UTC+0200)");
        verifyStatusMessage(pageData.getStatusMessagesToUser().get(0), expectedString, StatusMessageColor.WARNING);

        expectedString = String.format(Const.StatusMessages.AMBIGUOUS_LOCAL_DATE_TIME_OVERLAP,
                "end time", "Sun, 28 Oct 2012, 02:00 AM",
                "Sun, 28 Oct 2012, 02:00 AM CEST (UTC+0200)", "Sun, 28 Oct 2012, 02:00 AM CET (UTC+0100)",
                "Sun, 28 Oct 2012, 02:00 AM CEST (UTC+0200)");
        verifyStatusMessage(pageData.getStatusMessagesToUser().get(1), expectedString, StatusMessageColor.WARNING);

        expectedString = Const.StatusMessages.FEEDBACK_SESSION_EDITED;
        verifyStatusMessage(pageData.getStatusMessagesToUser().get(2), expectedString, StatusMessageColor.SUCCESS);

        assertFalse(pageData.getHasError());

        ______TS("success: Custom time zone, At open show session, 'later' show results");

        params = createParamsForTypicalFeedbackSession(instructor1ofCourse1.courseId,
                                                       session.getFeedbackSessionName());
        backDoorLogic.updateCourse(course.getId(), course.getName(), "Asia/Kathmandu");
        params[13] = Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN;
        params[19] = Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER;

        a = getAction(params);
        ar = getAjaxResult(a);
        pageData = (InstructorFeedbackEditPageData) ar.data;

        statusMessage = pageData.getStatusMessagesToUser().get(0);
        verifyStatusMessage(statusMessage, Const.StatusMessages.FEEDBACK_SESSION_EDITED, StatusMessageColor.SUCCESS);
        assertFalse(pageData.getHasError());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditSave|||instructorFeedbackEditSave|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||Updated Feedback Session "
                + "<span class=\"bold\">(First feedback session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> 2012-01-31T18:15:00Z"
                + "<span class=\"bold\"> to</span> 2014-12-31T18:15:00Z<br>"
                + "<span class=\"bold\">Session visible from:</span> 1970-12-31T00:00:00Z<br>"
                + "<span class=\"bold\">Results visible from:</span> 1970-01-01T00:00:00Z<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: instructions>|||/page/instructorFeedbackEditSave";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("success: At open session visible time, custom results visible time, UTC");

        params = createParamsCombinationForFeedbackSession(
                         instructor1ofCourse1.courseId, session.getFeedbackSessionName(), 1);
        backDoorLogic.updateCourse(course.getId(), course.getName(), "UTC");

        a = getAction(params);
        ar = getAjaxResult(a);
        pageData = (InstructorFeedbackEditPageData) ar.data;

        statusMessage = pageData.getStatusMessagesToUser().get(0);
        verifyStatusMessage(statusMessage, Const.StatusMessages.FEEDBACK_SESSION_EDITED, StatusMessageColor.SUCCESS);
        assertFalse(pageData.getHasError());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditSave|||instructorFeedbackEditSave|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||Updated Feedback Session "
                + "<span class=\"bold\">(First feedback session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> 2012-02-01T00:00:00Z"
                + "<span class=\"bold\"> to</span> 2015-01-01T00:00:00Z<br>"
                + "<span class=\"bold\">Session visible from:</span> 1970-12-31T00:00:00Z<br>"
                + "<span class=\"bold\">Results visible from:</span> 2014-05-08T02:00:00Z<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: instructions>|||/page/instructorFeedbackEditSave";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("success: Masquerade mode, manual release results, UTC");

        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        params = createParamsForTypicalFeedbackSession(instructor1ofCourse1.courseId,
                                                       session.getFeedbackSessionName());
        params[19] = Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER;

        params = addUserIdToParams(instructor1ofCourse1.googleId, params);

        a = getAction(params);
        ar = getAjaxResult(a);
        pageData = (InstructorFeedbackEditPageData) ar.data;

        statusMessage = pageData.getStatusMessagesToUser().get(0);
        verifyStatusMessage(statusMessage, Const.StatusMessages.FEEDBACK_SESSION_EDITED, StatusMessageColor.SUCCESS);
        assertFalse(pageData.getHasError());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditSave|||instructorFeedbackEditSave|||true|||"
                + "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||Updated Feedback Session "
                + "<span class=\"bold\">(First feedback session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> 2012-02-01T00:00:00Z"
                + "<span class=\"bold\"> to</span> 2015-01-01T00:00:00Z<br>"
                + "<span class=\"bold\">Session visible from:</span> 2012-01-01T00:00:00Z<br>"
                + "<span class=\"bold\">Results visible from:</span> 1970-01-01T00:00:00Z<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: instructions>|||/page/instructorFeedbackEditSave";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedString, a.getLogMessage(), adminUserId);

        ______TS("failure: Invalid grace period");

        params[26] = "12dsf";
        verifyAssumptionFailure(params);

        ______TS("failure: Null grace period");

        params = ArrayUtils.remove(params, 26);
        params = ArrayUtils.remove(params, 26);
        verifyAssumptionFailure(params);
    }

    @Override
    protected InstructorFeedbackEditSaveAction getAction(String... params) {
        return (InstructorFeedbackEditSaveAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams =
                createParamsForTypicalFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName());

        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
