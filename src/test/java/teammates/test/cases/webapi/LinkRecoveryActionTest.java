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
import teammates.ui.webapi.action.LinkRecoveryAction;
import teammates.ui.webapi.output.EmailRestoreResponseData;

/**
 * SUT: {@link LinkRecoveryAction}.
 */
public class LinkRecoveryActionTest extends BaseActionTest<LinkRecoveryAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.LINK_RECOVERY;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    @Override
    public void beforeTestMethodSetup() {
        // change start time of one feedback session in the data bundle for the purpose of this testing.
        DataBundle dataBundle = getTypicalDataBundle();
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse3");
        fs.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-3));
        dataBundle.feedbackSessions.put("session1InCourse3", fs);
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student1InCourse3 = typicalBundle.students.get("student1InCourse3");
        CourseAttributes course3 = typicalBundle.courses.get("typicalCourse3");
        FeedbackSessionAttributes session1InCourse3 = typicalBundle.feedbackSessions.get("session1InCourse3");

        ______TS("Invalid parameters");

        // no params
        verifyHttpParameterFailure();

        ______TS("Typical case: non-existing email");

        String[] nonExistingParam = new String[] {
                Const.ParamsNames.RECOVERY_EMAIL, "non-existent email",
        };

        LinkRecoveryAction a = getAction(nonExistingParam);
        JsonResult result = getJsonResult(a);

        EmailRestoreResponseData output = (EmailRestoreResponseData) result.getOutput();

        assertEquals("No response found with given email.", output.getMessage());
        assertFalse(output.getStatus());
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyNumberOfEmailsSent(a, 0);

        ______TS("Typical case: successfully sent recovery link email: No feedback sessions found");

        String[] param = new String[] {
                Const.ParamsNames.RECOVERY_EMAIL, student1InCourse1.getEmail(),
        };

        a = getAction(param);
        result = getJsonResult(a);

        output = (EmailRestoreResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been sent to the specified email.",
                output.getMessage());
        assertTrue(output.getStatus());
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyNumberOfEmailsSent(a, 1);

        EmailWrapper emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(EmailType.FEEDBACK_ACCESS_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse1.getEmail(), emailSent.getRecipient());
        assertEquals(Templates.populateTemplate(
                Templates.EmailTemplates.USER_FEEDBACK_SESSIONS_ACCESS_LINKS_NONE,
                "${userEmail}", SanitizationHelper.sanitizeForHtml(student1InCourse1.getEmail()),
                "${supportEmail}", Config.SUPPORT_EMAIL), emailSent.getContent());

        ______TS("Typical case: successfully sent recovery link email: Feedback sessions found");

        param = new String[] {
                Const.ParamsNames.RECOVERY_EMAIL, student1InCourse3.getEmail(),
        };

        a = getAction(param);
        result = getJsonResult(a);

        output = (EmailRestoreResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been sent to the specified email.",
                output.getMessage());
        assertTrue(output.getStatus());
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyNumberOfEmailsSent(a, 1);

        emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(EmailType.FEEDBACK_ACCESS_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse3.getEmail(), emailSent.getRecipient());

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

        String linksFragmentValue = Templates.populateTemplate(
                Templates.EmailTemplates.FRAGMENT_FEEDBACK_SESSION_ACCESS_LINK,
                "${courseId}", course3.getId(),
                "${courseName}", course3.getName(),
                "${feedbackSessionName}", session1InCourse3.getFeedbackSessionName(),
                "${deadline}", session1InCourse3.getEndTimeString() + (session1InCourse3.isClosed() ? " (Passed)" : ""),
                "${submitUrl}", submitUrlHtml,
                "${reportUrl}", reportUrlHtml);

        assertEquals(Templates.populateTemplate(
                Templates.EmailTemplates.USER_FEEDBACK_SESSIONS_ACCESS_LINKS,
                "${userName}", SanitizationHelper.sanitizeForHtml(student1InCourse3.getName()),
                "${linksFragment}", linksFragmentValue,
                "${userEmail}", SanitizationHelper.sanitizeForHtml(student1InCourse3.getEmail()),
                "${teammateHomePageLink}", Config.APP_URL,
                "${supportEmail}", Config.SUPPORT_EMAIL), emailSent.getContent());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAnyUserCanAccess();
    }
}
