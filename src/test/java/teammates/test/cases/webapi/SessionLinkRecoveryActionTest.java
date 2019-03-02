package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.TimeHelper;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.SessionLinkRecoveryAction;
import teammates.ui.webapi.output.LinkRecoveryResponseData;

/**
 * SUT: {@link SessionLinkRecoveryAction}.
 */
public class SessionLinkRecoveryActionTest extends BaseActionTest<SessionLinkRecoveryAction> {

    private FeedbackSessionAttributes session1InCourse3 = typicalBundle.feedbackSessions.get("session1InCourse3");
    private FeedbackSessionAttributes session2InCourse3 = typicalBundle.feedbackSessions.get("session2InCourse3");
    private FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
    private FeedbackSessionAttributes session2InCourse1 = typicalBundle.feedbackSessions.get("session2InCourse1");

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_LINK_RECOVERY;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    @Override
    public void beforeTestMethodSetup() {
        // change start time of one feedback session in the data bundle for the purpose of this testing.
        DataBundle dataBundle = typicalBundle;

        // opened and unpublished.
        session1InCourse3.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-20));
        dataBundle.feedbackSessions.put("session1InCourse3", session1InCourse3);

        // closed and unpublished
        session2InCourse3.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-19));
        session2InCourse3.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session2InCourse3.resetDeletedTime();
        dataBundle.feedbackSessions.put("session2InCourse3", session2InCourse3);

        // opened and published.
        session1InCourse1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-19));
        session1InCourse1.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        dataBundle.feedbackSessions.put("session1InCourse1", session1InCourse1);

        // closed and published
        session2InCourse1 = dataBundle.feedbackSessions.get("session2InCourse1");
        session2InCourse1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-18));
        session2InCourse1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session2InCourse1.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        dataBundle.feedbackSessions.put("session2InCourse1", session2InCourse1);

        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        StudentAttributes student1InCourse3 = typicalBundle.students.get("student1InCourse3");
        CourseAttributes course1 = typicalBundle.courses.get("typicalCourse1");
        CourseAttributes course3 = typicalBundle.courses.get("typicalCourse3");

        ______TS("Invalid parameters");

        // no params
        verifyHttpParameterFailure();

        ______TS("Typical case: non-existing email address");

        String[] nonExistingParam = new String[] {
                Const.ParamsNames.SESSION_LINK_RECOVERY_EMAIL, "non-existent email address.",
        };

        SessionLinkRecoveryAction a = getAction(nonExistingParam);
        JsonResult result = getJsonResult(a);

        LinkRecoveryResponseData output = (LinkRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been sent to "
                + "the specified email address: non-existent email address.", output.getMessage());
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyNumberOfEmailsSent(a, 0);

        ______TS("Typical case: successfully sent recovery link email: No feedback sessions found");

        String[] param = new String[] {
                Const.ParamsNames.SESSION_LINK_RECOVERY_EMAIL, student1InCourse2.getEmail(),
        };

        a = getAction(param);
        result = getJsonResult(a);

        output = (LinkRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been sent to the "
                        + "specified email address: " + student1InCourse2.getEmail(),
                output.getMessage());
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyNumberOfEmailsSent(a, 1);

        EmailWrapper emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(EmailType.FEEDBACK_ACCESS_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse2.getEmail(), emailSent.getRecipient());
        assertEquals(Templates.populateTemplate(
                Templates.EmailTemplates.SESSION_LINK_RECOVERY_ACCESS_LINKS_NONE,
                "${userEmail}", SanitizationHelper.sanitizeForHtml(student1InCourse2.getEmail()),
                "${supportEmail}", Config.SUPPORT_EMAIL), emailSent.getContent());

        ______TS("Typical case: successfully sent recovery link email: opened and unpublished, "
                + "closed and unpublished.");

        param = new String[] {
                Const.ParamsNames.SESSION_LINK_RECOVERY_EMAIL, student1InCourse3.getEmail(),
        };

        a = getAction(param);
        result = getJsonResult(a);

        output = (LinkRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been "
                        + "sent to the specified email address: " + student1InCourse3.getEmail(),
                output.getMessage());
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyNumberOfEmailsSent(a, 1);

        emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(EmailType.FEEDBACK_ACCESS_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse3.getEmail(), emailSent.getRecipient());

        StringBuilder linksFragmentValue = new StringBuilder(5000);

        // opened and unpublished.
        String registrationKey = logic.getStudentForEmail(student1InCourse3.getCourse(),
                student1InCourse3.getEmail()).getKey();
        String submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withCourseId(course3.getId())
                .withSessionName(session1InCourse3.getFeedbackSessionName())
                .withRegistrationKey(StringHelper.encrypt(registrationKey))
                .withStudentEmail(student1InCourse3.getEmail())
                .toAbsoluteString();
        String submitUrlHtml = "<a href=\"" + submitUrl + "\">" + submitUrl + "</a>";

        String reportUrlHtml = "(Feedback session is not yet published)";

        linksFragmentValue.append(Templates.populateTemplate(
                Templates.EmailTemplates.FRAGMENT_SESSION_LINK_RECOVERY_ACCESS_LINKS,
                "${courseId}", course3.getId(),
                "${courseName}", course3.getName(),
                "${feedbackSessionName}", session1InCourse3.getFeedbackSessionName(),
                "${deadline}", session1InCourse3.getEndTimeString() + (session1InCourse3.isClosed() ? " (Passed)" : ""),
                "${submitUrl}", submitUrlHtml,
                "${reportUrl}", reportUrlHtml));

        // closed and unpublished.
        submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withCourseId(course3.getId())
                .withSessionName(session2InCourse3.getFeedbackSessionName())
                .withRegistrationKey(StringHelper.encrypt(registrationKey))
                .withStudentEmail(student1InCourse3.getEmail())
                .toAbsoluteString();
        submitUrlHtml = "<a href=\"" + submitUrl + "\">" + submitUrl + "</a>";

        linksFragmentValue.append(Templates.populateTemplate(
                Templates.EmailTemplates.FRAGMENT_SESSION_LINK_RECOVERY_ACCESS_LINKS,
                "${courseId}", course3.getId(),
                "${courseName}", course3.getName(),
                "${feedbackSessionName}", session2InCourse3.getFeedbackSessionName(),
                "${deadline}", session2InCourse3.getEndTimeString() + (session2InCourse3.isClosed() ? " (Passed)" : ""),
                "${submitUrl}", submitUrlHtml,
                "${reportUrl}", reportUrlHtml));

        assertEquals(Templates.populateTemplate(
                Templates.EmailTemplates.SESSION_LINK_RECOVERY_ACCESS_LINKS,
                "${userName}", SanitizationHelper.sanitizeForHtml(student1InCourse3.getName()),
                "${linksFragment}", linksFragmentValue.toString(),
                "${userEmail}", SanitizationHelper.sanitizeForHtml(student1InCourse3.getEmail()),
                "${teammateHomePageLink}", Config.APP_URL,
                "${supportEmail}", Config.SUPPORT_EMAIL), emailSent.getContent());

        ______TS("Typical case: successfully sent recovery link email: opened and published, "
                + "closed and published.");

        param = new String[] {
                Const.ParamsNames.SESSION_LINK_RECOVERY_EMAIL, student1InCourse1.getEmail(),
        };

        a = getAction(param);
        result = getJsonResult(a);

        output = (LinkRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been sent "
                        + "to the specified email address: " + student1InCourse1.getEmail(),
                output.getMessage());
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyNumberOfEmailsSent(a, 1);

        emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(EmailType.FEEDBACK_ACCESS_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse1.getEmail(), emailSent.getRecipient());

        linksFragmentValue = new StringBuilder(5000);

        registrationKey = logic.getStudentForEmail(student1InCourse1.getCourse(),
                student1InCourse1.getEmail()).getKey();

        // opened and published.
        submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withCourseId(course1.getId())
                .withSessionName(session1InCourse1.getFeedbackSessionName())
                .withRegistrationKey(StringHelper.encrypt(registrationKey))
                .withStudentEmail(student1InCourse1.getEmail())
                .toAbsoluteString();
        submitUrlHtml = "<a href=\"" + submitUrl + "\">" + submitUrl + "</a>";

        String reportUrl = Config.getFrontEndAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                .withCourseId(course1.getId())
                .withSessionName(session1InCourse1.getFeedbackSessionName())
                .toAbsoluteString();
        reportUrlHtml = "<a href=\"" + reportUrl + "\">" + reportUrl + "</a>";

        linksFragmentValue.append(Templates.populateTemplate(
                Templates.EmailTemplates.FRAGMENT_SESSION_LINK_RECOVERY_ACCESS_LINKS,
                "${courseId}", course1.getId(),
                "${courseName}", course1.getName(),
                "${feedbackSessionName}", session1InCourse1.getFeedbackSessionName(),
                "${deadline}", session1InCourse1.getEndTimeString() + (session1InCourse1.isClosed() ? " (Passed)" : ""),
                "${submitUrl}", submitUrlHtml,
                "${reportUrl}", reportUrlHtml));

        // closed and published.
        submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withCourseId(course1.getId())
                .withSessionName(session2InCourse1.getFeedbackSessionName())
                .withRegistrationKey(StringHelper.encrypt(registrationKey))
                .withStudentEmail(student1InCourse1.getEmail())
                .toAbsoluteString();
        submitUrlHtml = "<a href=\"" + submitUrl + "\">" + submitUrl + "</a>";
        reportUrl = Config.getFrontEndAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                .withCourseId(course1.getId())
                .withSessionName(session2InCourse1.getFeedbackSessionName())
                .toAbsoluteString();
        reportUrlHtml = "<a href=\"" + reportUrl + "\">" + reportUrl + "</a>";

        linksFragmentValue.append(Templates.populateTemplate(
                Templates.EmailTemplates.FRAGMENT_SESSION_LINK_RECOVERY_ACCESS_LINKS,
                "${courseId}", course1.getId(),
                "${courseName}", course1.getName(),
                "${feedbackSessionName}", session2InCourse1.getFeedbackSessionName(),
                "${deadline}", session2InCourse1.getEndTimeString() + (session2InCourse1.isClosed() ? " (Passed)" : ""),
                "${submitUrl}", submitUrlHtml,
                "${reportUrl}", reportUrlHtml));

        assertEquals(Templates.populateTemplate(
                Templates.EmailTemplates.SESSION_LINK_RECOVERY_ACCESS_LINKS,
                "${userName}", SanitizationHelper.sanitizeForHtml(student1InCourse1.getName()),
                "${linksFragment}", linksFragmentValue.toString(),
                "${userEmail}", SanitizationHelper.sanitizeForHtml(student1InCourse1.getEmail()),
                "${teammateHomePageLink}", Config.APP_URL,
                "${supportEmail}", Config.SUPPORT_EMAIL), emailSent.getContent());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAnyUserCanAccess();
    }
}
